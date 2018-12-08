/*
 * Original author: Michael Riffle <mriffle .at. uw.edu>
 *                  
 * Copyright 2018 University of Washington - Seattle, WA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.yeastrc.limelight.xml.cometptm.reader;

import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult;
import net.systemsbiology.regis_web.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SpectrumQuery.SearchResult.SearchHit;
import org.yeastrc.limelight.xml.cometptm.objects.CometPTMParameters;
import org.yeastrc.limelight.xml.cometptm.objects.CometPSM;
import org.yeastrc.limelight.xml.cometptm.objects.CometReportedPeptide;
import org.yeastrc.limelight.xml.cometptm.objects.CometResults;
import org.yeastrc.limelight.xml.cometptm.utils.ReportedPeptideUtils;
import org.yeastrc.limelight.xml.cometptm.utils.PepXMLParsingUtils;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael Riffle
 * @date Feb 21, 2018
 *
 */
public class PepXMLResultsParser {

	public static CometResults getTPPResults(File pepXMLFile, CometPTMParameters params, String decoyPrefixOverride ) throws Throwable {

		Map<CometReportedPeptide,Map<Integer, CometPSM>> resultMap = new HashMap<>();
				
		MsmsPipelineAnalysis msAnalysis = null;
		try {
			msAnalysis = PepXMLParsingUtils.getMSmsPipelineAnalysis( pepXMLFile );
		} catch( Throwable t ) {
			System.err.println( "Got an error parsing the pep XML file. Error: " + t.getMessage() );
			throw t;
		}

		String decoyPrefix = decoyPrefixOverride;
		if( decoyPrefix == null || decoyPrefix.length() < 1 ) {
			decoyPrefix = params.getDecoyPrefix();
		}
		if( decoyPrefix == null || decoyPrefix.length() < 1 ) {
			throw new Exception( "Could not determine a decoy prefix. It could not be found in the comet.params and none was supplied on command line." );
		}

		
		CometResults results = new CometResults();
		results.setPeptidePSMMap( resultMap );
		
		results.setCometVersion( PepXMLParsingUtils.getCometVersionFromXML( msAnalysis ) );
		
		for( MsmsRunSummary runSummary : msAnalysis.getMsmsRunSummary() ) {
			for( SpectrumQuery spectrumQuery : runSummary.getSpectrumQuery() ) {
				
				int charge = PepXMLParsingUtils.getChargeFromSpectrumQuery( spectrumQuery );
				int scanNumber = PepXMLParsingUtils.getScanNumberFromSpectrumQuery( spectrumQuery );
				BigDecimal neutralMass = PepXMLParsingUtils.getNeutralMassFromSpectrumQuery( spectrumQuery );
				BigDecimal retentionTime = PepXMLParsingUtils.getRetentionTimeFromSpectrumQuery( spectrumQuery );
				
				for( SearchResult searchResult : spectrumQuery.getSearchResult() ) {
					for( SearchHit searchHit : searchResult.getSearchHit() ) {
						
						CometPSM psm = null;
						
						try {
							
							psm = PepXMLParsingUtils.getPsmFromSearchHit( searchHit, charge, scanNumber, neutralMass, retentionTime, params, decoyPrefix );
							
						} catch( Throwable t) {
							
							System.err.println( "Error reading PSM from pepXML. Error: " + t.getMessage() );
							throw t;
							
						}
						
						if( psm != null ) {
							CometReportedPeptide cometReportedPeptide = ReportedPeptideUtils.getCometReportedPeptideForCometPSM( psm );
							
							if( !results.getPeptidePSMMap().containsKey( cometReportedPeptide ) )
								results.getPeptidePSMMap().put( cometReportedPeptide, new HashMap<>() );
							
							results.getPeptidePSMMap().get( cometReportedPeptide ).put( psm.getScanNumber(), psm );
						}
					}
				}
			}
		}
		
		return results;
	}
	
}
