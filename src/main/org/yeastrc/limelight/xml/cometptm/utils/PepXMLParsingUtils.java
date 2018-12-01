package org.yeastrc.limelight.xml.cometptm.utils;

import net.systemsbiology.regis_web.pepxml.*;
import net.systemsbiology.regis_web.pepxml.ModInfoDataType.ModAminoacidMass;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SearchSummary;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit;
import org.yeastrc.limelight.xml.cometptm.constants.MassConstants;
import org.yeastrc.limelight.xml.cometptm.objects.CometPTMParameters;
import org.yeastrc.limelight.xml.cometptm.objects.CometPSM;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.toIntExact;

public class PepXMLParsingUtils {

	/**
	 * Attempt to get the comet version from the pepXML file. Returns "Unknown" if not found.
	 * 
	 * @param msAnalysis
	 * @return
	 */
	public static String getCometVersionFromXML(MsmsPipelineAnalysis msAnalysis ) {
		
		for( MsmsRunSummary runSummary : msAnalysis.getMsmsRunSummary() ) {
			for( SearchSummary searchSummary : runSummary.getSearchSummary() ) {

				if( 	searchSummary.getSearchEngine() != null &&
						searchSummary.getSearchEngine().value() != null &&
						searchSummary.getSearchEngine().value().equals( "Comet" ) ) {	// interesting, they report as X! Tandem?

					return searchSummary.getSearchEngineVersion();
				}
			
			}
		}
		
		return "Unknown";
	}

	/**
	 * Return true if this searchHit is a decoy. This means that it only matches
	 * decoy proteins.
	 * 
	 * @param searchHit
	 * @return
	 */
	public static boolean searchHitIsDecoy( SearchHit searchHit, String decoyPrefix ) {
		
		String protein = searchHit.getProtein();
		if( protein.startsWith( decoyPrefix ) ) {
			
			if( searchHit.getAlternativeProtein() != null ) {
				for( AltProteinDataType ap : searchHit.getAlternativeProtein() ) {
					if( !ap.getProtein().startsWith( decoyPrefix ) ) {
						return false;
					}
				}
			}
			
			return true;			
		}
		
		return false;
	}
	
	/**
	 * Return the top-most parent element of the pepXML file as a JAXB object.
	 * 
	 * @param file
	 * @return
	 * @throws Throwable
	 */
	public static MsmsPipelineAnalysis getMSmsPipelineAnalysis( File file ) throws Throwable {
		
		JAXBContext jaxbContext = JAXBContext.newInstance(MsmsPipelineAnalysis.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		MsmsPipelineAnalysis msAnalysis = (MsmsPipelineAnalysis)jaxbUnmarshaller.unmarshal( file );
		
		return msAnalysis;
	}
	
	/**
	 * Get the retention time from the spectrumQuery JAXB object
	 * 
	 * @param spectrumQuery
	 * @return
	 */
	public static BigDecimal getRetentionTimeFromSpectrumQuery( SpectrumQuery spectrumQuery ) {
		return spectrumQuery.getRetentionTimeSec();
	}
	
	/**
	 * Get the neutral mass from the spectrumQuery JAXB object
	 * 
	 * @param spectrumQuery
	 * @return
	 */
	public static BigDecimal getNeutralMassFromSpectrumQuery( SpectrumQuery spectrumQuery ) {
		return spectrumQuery.getPrecursorNeutralMass();
	}
	
	/**
	 * Get the scan number from the spectrumQuery JAXB object
	 * 
	 * @param spectrumQuery
	 * @return
	 */
	public static int getScanNumberFromSpectrumQuery( SpectrumQuery spectrumQuery ) {
		return toIntExact( spectrumQuery.getStartScan() );
	}
	
	/**
	 * Get the charge from the spectrumQuery JAXB object
	 * 
	 * @param spectrumQuery
	 * @return
	 */
	public static int getChargeFromSpectrumQuery( SpectrumQuery spectrumQuery ) {
		return spectrumQuery.getAssumedCharge().intValue();
	}

	/**
	 * Get a CometPSM (psm object) from the supplied searchHit JAXB object.
	 *
	 * @param searchHit
	 * @param charge
	 * @param scanNumber
	 * @param obsMass
	 * @param retentionTime
	 * @return
	 * @throws Throwable
	 */
	public static CometPSM getPsmFromSearchHit(
			SearchHit searchHit,
			int charge,
			int scanNumber,
			BigDecimal obsMass,
			BigDecimal retentionTime,
			CometPTMParameters params,
			String decoyPrefix) throws Throwable {
				
		CometPSM psm = new CometPSM();
		
		psm.setCharge( charge );
		psm.setScanNumber( scanNumber );
		psm.setPrecursorNeutralMass( obsMass );
		psm.setRetentionTime( retentionTime );
		
		psm.setPeptideSequence( searchHit.getPeptide() );

		psm.setEvalue( getScoreForType( searchHit, "expect" ) );
		psm.setDeltaCn( getScoreForType( searchHit, "deltacn" ) );
		psm.setDeltaCnStar( getScoreForType( searchHit, "deltacnstar" ) );
		psm.setXcorr( getScoreForType( searchHit, "xcorr" ) );
		psm.setSpscore( getScoreForType( searchHit, "spscore" ) );
		psm.setSprank( getScoreForType( searchHit, "sprank" ) );

		psm.setMassDiff( getMassDiffForSearchHit( searchHit ) );

		if( searchHitIsDecoy( searchHit, decoyPrefix ) ) {
			psm.setDecoy( true );
		} else {
			psm.setDecoy( false );
		}

		try {
			psm.setModifications( getModificationsForSearchHit( searchHit, params ) );
		} catch( Throwable t ) {
			
			System.err.println( "Error getting mods for PSM. Error was: " + t.getMessage() );
			throw t;
		}
		
		return psm;
	}


	/**
	 * Get the requested score from the searchHit JAXB object
	 *
	 * @param searchHit
	 * @param type
	 * @return
	 * @throws Throwable
	 */
	public static BigDecimal getScoreForType( SearchHit searchHit, String type ) throws Throwable {
		
		for( NameValueType searchScore : searchHit.getSearchScore() ) {
			if( searchScore.getName().equals( type ) ) {
				
				return new BigDecimal( searchScore.getValueAttribute() );
			}
		}
		
		throw new Exception( "Could not find a score of name: " + type + " for PSM..." );		
	}

	/**
	 * Get the variable modifications from the supplied searchHit JAXB object
	 *
	 * @param searchHit
	 * @return
	 * @throws Throwable
	 */
	public static Map<Integer, BigDecimal> getModificationsForSearchHit( SearchHit searchHit, CometPTMParameters params ) throws Throwable {
		
		Map<Integer, BigDecimal> modMap = new HashMap<>();

		String peptide = searchHit.getPeptide();
		if( peptide == null || peptide.length() < 1 ) {
			throw new Exception( "searchHit had no peptide: " + searchHit );
		}

		ModInfoDataType mofo = searchHit.getModificationInfo();
		if( mofo != null ) {
			for( ModAminoacidMass mod : mofo.getModAminoacidMass() ) {

				// todo: check if this is a static mod and don't include it if so

				int position = mod.getPosition().intValueExact();
				String aminoAcid = String.valueOf( peptide.charAt( position - 1 ) );

				if( !MassConstants.AMINO_ACID_MASSES.containsKey( aminoAcid ) ) {
					throw new Exception( "Could not find mass for amino acid: " + aminoAcid );
				}

				BigDecimal modMass = BigDecimal.valueOf( mod.getMass() ).subtract( MassConstants.AMINO_ACID_MASSES.get( aminoAcid ) );

				modMass = modMass.setScale( 4, RoundingMode.HALF_UP );	// round the mod mass to 4 decimal places

				if( !isModStaticMod( aminoAcid, modMass, params ) ) {
					modMap.put(position, modMass);
				}
			}
		}
		
		return modMap;
	}

	private static boolean isModStaticMod(String aminoAcid, BigDecimal modMass, CometPTMParameters params ) {

		if( params.getStaticMods() == null || params.getStaticMods().size() < 1 ) {
			return false;
		}

		if( !params.getStaticMods().containsKey( aminoAcid.charAt( 0 ) ) ) {
			return false;
		}

		// round to two decimal places and compare
		BigDecimal testMass = modMass.setScale( 2, RoundingMode.HALF_UP );
		BigDecimal paramMass = BigDecimal.valueOf( params.getStaticMods().get( aminoAcid.charAt( 0 ) ) ).setScale( 2, RoundingMode.HALF_UP );

		return testMass.equals( paramMass );
	}


	/**
	 * Get the mass diff reported for the search hit
	 *
	 * @param searchHit
	 * @return
	 * @throws Throwable
	 */
	public static BigDecimal getMassDiffForSearchHit( SearchHit searchHit ) throws Throwable {

		return searchHit.getMassdiff();

	}

	
	
}
