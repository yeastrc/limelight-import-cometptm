package org.yeastrc.limelight.xml.cometptm.builder;

import org.yeastrc.limelight.limelight_import.api.xml_dto.*;
import org.yeastrc.limelight.limelight_import.api.xml_dto.ReportedPeptide.ReportedPeptideAnnotations;
import org.yeastrc.limelight.limelight_import.api.xml_dto.SearchProgram.PsmAnnotationTypes;
import org.yeastrc.limelight.limelight_import.create_import_file_from_java_objects.main.CreateImportFileFromJavaObjectsMain;
import org.yeastrc.limelight.xml.cometptm.annotation.PSMAnnotationTypeSortOrder;
import org.yeastrc.limelight.xml.cometptm.annotation.PSMAnnotationTypes;
import org.yeastrc.limelight.xml.cometptm.annotation.PSMDefaultVisibleAnnotationTypes;
import org.yeastrc.limelight.xml.cometptm.constants.Constants;
import org.yeastrc.limelight.xml.cometptm.objects.*;
import org.yeastrc.limelight.xml.cometptm.reader.TPPErrorAnalysis;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class XMLBuilder {

	public void buildAndSaveXML( ConversionParameters conversionParameters,
			                     CometResults cometResults,
			                     CometPTMParameters cometTPPParameters,
			                     TPPErrorAnalysis ppErrorAnalysis,
			                     TPPErrorAnalysis ipErrorAnalysis )
    throws Exception {

		LimelightInput limelightInputRoot = new LimelightInput();

		limelightInputRoot.setFastaFilename( conversionParameters.getFastaFile().getName() );
		
		// add in the conversion program (this program) information
		ConversionProgramBuilder.createInstance().buildConversionProgramSection( limelightInputRoot, conversionParameters);
		
		SearchProgramInfo searchProgramInfo = new SearchProgramInfo();
		limelightInputRoot.setSearchProgramInfo( searchProgramInfo );
		
		SearchPrograms searchPrograms = new SearchPrograms();
		searchProgramInfo.setSearchPrograms( searchPrograms );
		
		if( cometResults.isHasIProphetResults() ) {
			SearchProgram searchProgram = new SearchProgram();
			searchPrograms.getSearchProgram().add( searchProgram );
				
			searchProgram.setName( Constants.PROGRAM_NAME_INTERPROPHET );
			searchProgram.setDisplayName( Constants.PROGRAM_NAME_INTERPROPHET );
			searchProgram.setVersion( cometResults.getTppVersion() );
			
			
			//
			// Define the annotation types present in peptideprophet data
			//
			PsmAnnotationTypes psmAnnotationTypes = new PsmAnnotationTypes();
			searchProgram.setPsmAnnotationTypes( psmAnnotationTypes );
			
			FilterablePsmAnnotationTypes filterablePsmAnnotationTypes = new FilterablePsmAnnotationTypes();
			psmAnnotationTypes.setFilterablePsmAnnotationTypes( filterablePsmAnnotationTypes );
			
			for( FilterablePsmAnnotationType annoType : PSMAnnotationTypes.getFilterablePsmAnnotationTypes( Constants.PROGRAM_NAME_INTERPROPHET ) ) {
				filterablePsmAnnotationTypes.getFilterablePsmAnnotationType().add( annoType );
			}
		}
		
		{
			SearchProgram searchProgram = new SearchProgram();
			searchPrograms.getSearchProgram().add( searchProgram );
				
			searchProgram.setName( Constants.PROGRAM_NAME_PEPTIDEPROPHET );
			searchProgram.setDisplayName( Constants.PROGRAM_NAME_PEPTIDEPROPHET );
			searchProgram.setVersion( cometResults.getTppVersion() );
			
			
			//
			// Define the annotation types present in peptideprophet data
			//
			PsmAnnotationTypes psmAnnotationTypes = new PsmAnnotationTypes();
			searchProgram.setPsmAnnotationTypes( psmAnnotationTypes );
			
			FilterablePsmAnnotationTypes filterablePsmAnnotationTypes = new FilterablePsmAnnotationTypes();
			psmAnnotationTypes.setFilterablePsmAnnotationTypes( filterablePsmAnnotationTypes );
			
			for( FilterablePsmAnnotationType annoType : PSMAnnotationTypes.getFilterablePsmAnnotationTypes( Constants.PROGRAM_NAME_PEPTIDEPROPHET ) ) {
				
				// disable default filter on ppro FDR if ipro data are available
				if( annoType.getName().equals( PSMAnnotationTypes.PPROPHET_ANNOTATION_TYPE_FDR ) && cometResults.isHasIProphetResults() ) {
					annoType.setDefaultFilterValue( null );
				}
				
				filterablePsmAnnotationTypes.getFilterablePsmAnnotationType().add( annoType );
			}

		}

		{
			SearchProgram searchProgram = new SearchProgram();
			searchPrograms.getSearchProgram().add( searchProgram );

			searchProgram.setName( Constants.PROGRAM_NAME_PTMPROPHET );
			searchProgram.setDisplayName( Constants.PROGRAM_NAME_PTMPROPHET );
			searchProgram.setVersion( cometResults.getTppVersion() );


			//
			// Define the annotation types present in peptideprophet data
			//
			PsmAnnotationTypes psmAnnotationTypes = new PsmAnnotationTypes();
			searchProgram.setPsmAnnotationTypes( psmAnnotationTypes );

		}

		{
			SearchProgram searchProgram = new SearchProgram();
			searchPrograms.getSearchProgram().add( searchProgram );
				
			searchProgram.setName( Constants.PROGRAM_NAME_COMET_PTM );
			searchProgram.setDisplayName( Constants.PROGRAM_NAME_COMET_PTM );
			searchProgram.setVersion( cometResults.getCometVersion() );
			
			
			//
			// Define the annotation types present in magnum data
			//
			PsmAnnotationTypes psmAnnotationTypes = new PsmAnnotationTypes();
			searchProgram.setPsmAnnotationTypes( psmAnnotationTypes );
			
			FilterablePsmAnnotationTypes filterablePsmAnnotationTypes = new FilterablePsmAnnotationTypes();
			psmAnnotationTypes.setFilterablePsmAnnotationTypes( filterablePsmAnnotationTypes );
			
			for( FilterablePsmAnnotationType annoType : PSMAnnotationTypes.getFilterablePsmAnnotationTypes( Constants.PROGRAM_NAME_COMET_PTM ) ) {
				filterablePsmAnnotationTypes.getFilterablePsmAnnotationType().add( annoType );
			}
			
			//todo: add in non-filterable annotations
		}
		
		
		//
		// Define which annotation types are visible by default
		//
		DefaultVisibleAnnotations xmlDefaultVisibleAnnotations = new DefaultVisibleAnnotations();
		searchProgramInfo.setDefaultVisibleAnnotations( xmlDefaultVisibleAnnotations );
		
		VisiblePsmAnnotations xmlVisiblePsmAnnotations = new VisiblePsmAnnotations();
		xmlDefaultVisibleAnnotations.setVisiblePsmAnnotations( xmlVisiblePsmAnnotations );

		for( SearchAnnotation sa : PSMDefaultVisibleAnnotationTypes.getDefaultVisibleAnnotationTypes( cometResults.isHasIProphetResults() ) ) {
			xmlVisiblePsmAnnotations.getSearchAnnotation().add( sa );
		}
		
		//
		// Define the default display order in proxl
		//
		AnnotationSortOrder xmlAnnotationSortOrder = new AnnotationSortOrder();
		searchProgramInfo.setAnnotationSortOrder( xmlAnnotationSortOrder );
		
		PsmAnnotationSortOrder xmlPsmAnnotationSortOrder = new PsmAnnotationSortOrder();
		xmlAnnotationSortOrder.setPsmAnnotationSortOrder( xmlPsmAnnotationSortOrder );
		
		for( SearchAnnotation xmlSearchAnnotation : PSMAnnotationTypeSortOrder.getPSMAnnotationTypeSortOrder( cometResults.isHasIProphetResults() ) ) {
			xmlPsmAnnotationSortOrder.getSearchAnnotation().add( xmlSearchAnnotation );
		}
		
		//
		// Define the static mods
		//
		if( cometTPPParameters.getStaticMods() != null && cometTPPParameters.getStaticMods().keySet().size() > 0 ) {
			StaticModifications smods = new StaticModifications();
			limelightInputRoot.setStaticModifications( smods );
			
			
			for( char residue : cometTPPParameters.getStaticMods().keySet() ) {
				
				StaticModification xmlSmod = new StaticModification();
				xmlSmod.setAminoAcid( String.valueOf( residue ) );
				xmlSmod.setMassChange( BigDecimal.valueOf( cometTPPParameters.getStaticMods().get( residue ) ) );
				
				smods.getStaticModification().add( xmlSmod );
			}
		}
		
		// cache of FDRs calculated for specific PSM probabilities
		Map<BigDecimal, BigDecimal> ppFDRCache = new HashMap<>();
		Map<BigDecimal, BigDecimal> ipFDRCache = new HashMap<>();
		
		//
		// Define the peptide and PSM data
		//
		ReportedPeptides reportedPeptides = new ReportedPeptides();
		limelightInputRoot.setReportedPeptides( reportedPeptides );
		
		// iterate over each distinct reported peptide
		for( CometReportedPeptide cometReportedPeptide : cometResults.getPeptidePSMMap().keySet() ) {
						
			ReportedPeptide xmlReportedPeptide = new ReportedPeptide();
			reportedPeptides.getReportedPeptide().add( xmlReportedPeptide );
			
			xmlReportedPeptide.setReportedPeptideString( cometReportedPeptide.getReportedPeptideString() );
			xmlReportedPeptide.setSequence( cometReportedPeptide.getNakedPeptide() );
			
			// add in the filterable peptide annotations (e.g., q-value)
			ReportedPeptideAnnotations xmlReportedPeptideAnnotations = new ReportedPeptideAnnotations();
			xmlReportedPeptide.setReportedPeptideAnnotations( xmlReportedPeptideAnnotations );

			// add in the mods for this peptide
			if( cometReportedPeptide.getMods() != null && cometReportedPeptide.getMods().keySet().size() > 0 ) {

				PeptideModifications xmlModifications = new PeptideModifications();
				xmlReportedPeptide.setPeptideModifications( xmlModifications );

				for( int position : cometReportedPeptide.getMods().keySet() ) {
					PeptideModification xmlModification = new PeptideModification();
					xmlModifications.getPeptideModification().add( xmlModification );

					xmlModification.setMass( cometReportedPeptide.getMods().get( position ).stripTrailingZeros().setScale( 0, RoundingMode.HALF_UP ) );
					xmlModification.setPosition( BigInteger.valueOf( position ) );
				}
			}

			
			// add in the PSMs and annotations
			Psms xmlPsms = new Psms();
			xmlReportedPeptide.setPsms( xmlPsms );

			// iterate over all PSMs for this reported peptide

			for( int scanNumber : cometResults.getPeptidePSMMap().get(cometReportedPeptide).keySet() ) {

				CometPSM psm = cometResults.getPeptidePSMMap().get(cometReportedPeptide).get( scanNumber );

				Psm xmlPsm = new Psm();
				xmlPsms.getPsm().add( xmlPsm );

				xmlPsm.setScanNumber( new BigInteger( String.valueOf( scanNumber ) ) );
				xmlPsm.setPrecursorCharge( new BigInteger( String.valueOf( psm.getCharge() ) ) );

				// add in the filterable PSM annotations (e.g., score)
				FilterablePsmAnnotations xmlFilterablePsmAnnotations = new FilterablePsmAnnotations();
				xmlPsm.setFilterablePsmAnnotations( xmlFilterablePsmAnnotations );

				// handle msfragger scores
				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.COMET_ANNOTATION_TYPE_EVALUE );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_COMET_PTM );
					xmlFilterablePsmAnnotation.setValue( psm.getEvalue() );
				}

				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.COMET_ANNOTATION_TYPE_DELTACN );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_COMET_PTM );
					xmlFilterablePsmAnnotation.setValue( psm.getDeltaCn() );
				}

				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.COMET_ANNOTATION_TYPE_DELTACNSTAR );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_COMET_PTM );
					xmlFilterablePsmAnnotation.setValue( psm.getDeltaCnStar() );
				}

				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.COMET_ANNOTATION_TYPE_MASSDIFF );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_COMET_PTM );
					xmlFilterablePsmAnnotation.setValue( psm.getMassDiff() );
				}

				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.COMET_ANNOTATION_TYPE_SPRANK );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_COMET_PTM );
					xmlFilterablePsmAnnotation.setValue( psm.getSprank() );
				}

				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.COMET_ANNOTATION_TYPE_SPSCORE );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_COMET_PTM );
					xmlFilterablePsmAnnotation.setValue( psm.getSpscore() );
				}

				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.COMET_ANNOTATION_TYPE_XCORR );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_COMET_PTM );
					xmlFilterablePsmAnnotation.setValue( psm.getXcorr() );
				}



				// handle peptide prophet scores
				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.PPROPHET_ANNOTATION_TYPE_SCORE );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_PEPTIDEPROPHET );
					xmlFilterablePsmAnnotation.setValue( psm.getPeptideProphetProbability() );
				}
				
				
				{
					FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
					xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

					xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.PPROPHET_ANNOTATION_TYPE_FDR );
					xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_PEPTIDEPROPHET );
					
					BigDecimal error = null;
					if( ppFDRCache.containsKey( psm.getPeptideProphetProbability() ) ) {
						error = ppFDRCache.get( psm.getPeptideProphetProbability() );
					} else {
						error = ppErrorAnalysis.getError( psm.getPeptideProphetProbability() );
						ppFDRCache.put( psm.getPeptideProphetProbability(), error );
					}
					
					xmlFilterablePsmAnnotation.setValue( error );
				}
				
				// handle interprophet scores
				if( cometResults.isHasIProphetResults() ) {
					
					{
						FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
						xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

						xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.IPROPHET_ANNOTATION_TYPE_SCORE );
						xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_INTERPROPHET );
						xmlFilterablePsmAnnotation.setValue( psm.getInterProphetProbability() );
					}
					
					
					{
						FilterablePsmAnnotation xmlFilterablePsmAnnotation = new FilterablePsmAnnotation();
						xmlFilterablePsmAnnotations.getFilterablePsmAnnotation().add( xmlFilterablePsmAnnotation );

						xmlFilterablePsmAnnotation.setAnnotationName( PSMAnnotationTypes.IPROPHET_ANNOTATION_TYPE_FDR );
						xmlFilterablePsmAnnotation.setSearchProgram( Constants.PROGRAM_NAME_INTERPROPHET );
						
						BigDecimal error = null;
						if( ipFDRCache.containsKey( psm.getInterProphetProbability() ) ) {
							error = ipFDRCache.get( psm.getInterProphetProbability() );
						} else {
							error = ipErrorAnalysis.getError( psm.getInterProphetProbability() );
							ipFDRCache.put( psm.getInterProphetProbability(), error );
						}
						
						xmlFilterablePsmAnnotation.setValue( error );
					}
					
				}

				// add in the mods for this psm
				if( psm.getModifications() != null && psm.getModifications().keySet().size() > 0 ) {

					PsmModifications xmlPSMModifications = new PsmModifications();
					xmlPsm.setPsmModifications( xmlPSMModifications );

					for( int position : psm.getModifications().keySet() ) {
						PsmModification xmlPSMModification = new PsmModification();
						xmlPSMModifications.getPsmModification().add( xmlPSMModification );

						xmlPSMModification.setMass( psm.getModifications().get( position ) );
						xmlPSMModification.setPosition( new BigInteger( String.valueOf( position ) ) );
					}
				}
				
				
			}// end iterating over psms for a reported peptide
		
		}//end iterating over reported peptides


		
		
		// add in the matched proteins section
		MatchedProteinsBuilder.getInstance().buildMatchedProteins(
				                                                   limelightInputRoot,
				                                                   conversionParameters.getFastaFile(),
				                                                   cometResults.getPeptidePSMMap().keySet()
				                                                  );
		
		
		// add in the config file(s)
		ConfigurationFiles xmlConfigurationFiles = new ConfigurationFiles();
		limelightInputRoot.setConfigurationFiles( xmlConfigurationFiles );
		
		ConfigurationFile xmlConfigurationFile = new ConfigurationFile();
		xmlConfigurationFiles.getConfigurationFile().add( xmlConfigurationFile );
		
		xmlConfigurationFile.setSearchProgram( Constants.PROGRAM_NAME_COMET_PTM );
		xmlConfigurationFile.setFileName( conversionParameters.getFonfFile().getName() );
		xmlConfigurationFile.setFileContent( Files.readAllBytes( FileSystems.getDefault().getPath( conversionParameters.getFonfFile().getAbsolutePath() ) ) );
		
		
		//make the xml file
		CreateImportFileFromJavaObjectsMain.getInstance().createImportFileFromJavaObjectsMain( conversionParameters.getLimelightXMLOutputFile(), limelightInputRoot);
		
	}
	
	
}
