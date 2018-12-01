package org.yeastrc.limelight.xml.cometptm.reader;

import org.yeastrc.limelight.xml.cometptm.objects.CometPSM;
import org.yeastrc.limelight.xml.cometptm.objects.CometReportedPeptide;
import org.yeastrc.limelight.xml.cometptm.objects.CometResults;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * PeptideProphet does not report a score with inherent meaning, at least towards the end of providing
 * a filter for its probability score that is applicable to every search performed. This utility class provides
 * a means for analyzing then determining the FDR associated with the probability score from a given search.
 * 
 * @author mriffle
 *
 */
public class TPPErrorAnalyzer {

	public static final int TYPE_PEPTIDE_PROPHET = 0;
	public static final int TYPE_INTER_PROPHET = 1;
	
	/**
	 * Analyze the target/decoy counts in the analysis
	 * 
	 * @throws Exception
	 */
	public static TPPErrorAnalysis performPeptideProphetAnalysis(CometResults results, int type ) throws Exception {
		
		Map<BigDecimal, ProbabilitySumCounter> probabilitySums = new HashMap<BigDecimal, ProbabilitySumCounter>();
		
		/*
		 * First, compile a count for targets and decoys for each score reported for all PSMs
		 */
		
		for( CometReportedPeptide tppRp : results.getPeptidePSMMap().keySet() ) {
			for( int scanNumber : results.getPeptidePSMMap().get( tppRp ).keySet() ) {
							
				CometPSM psm = results.getPeptidePSMMap().get( tppRp ).get( scanNumber );
				
				BigDecimal score;
				
				if( type == TYPE_PEPTIDE_PROPHET ) {
					score = psm.getPeptideProphetProbability();
				
				} else if( type == TYPE_INTER_PROPHET ) {
					score = psm.getInterProphetProbability();
					
				} else {
					throw new RuntimeException( "Got an invalid type." );
				}
				
				
				ProbabilitySumCounter psc = null;
				
				if( probabilitySums.containsKey( score ) ) {
					psc = probabilitySums.get( score );
				} else {
					psc = new ProbabilitySumCounter();
					probabilitySums.put( score,  psc );
				}
				
				psc.setpCount( psc.getpCount() + score.doubleValue() );
				psc.setOneMinusPCount( psc.getOneMinusPCount() + ( 1.0 - score.doubleValue() ) );
				psc.setTotalCount( psc.getTotalCount() + 1 );
			}
		}
		
		TPPErrorAnalysis ppa = new TPPErrorAnalysis();
		ppa.setProbabilitySums( probabilitySums );
		
		return ppa;
	}
}
