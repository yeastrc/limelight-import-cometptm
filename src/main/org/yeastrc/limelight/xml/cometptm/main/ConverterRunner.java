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

package org.yeastrc.limelight.xml.cometptm.main;

import org.yeastrc.limelight.xml.cometptm.builder.XMLBuilder;
import org.yeastrc.limelight.xml.cometptm.objects.ConversionParameters;
import org.yeastrc.limelight.xml.cometptm.objects.CometPTMParameters;
import org.yeastrc.limelight.xml.cometptm.objects.CometResults;
import org.yeastrc.limelight.xml.cometptm.reader.*;

public class ConverterRunner {

	// quickly get a new instance of this class
	public static ConverterRunner createInstance() { return new ConverterRunner(); }
	
	
	public void convertCometPTMTPPToLimelightXML(ConversionParameters conversionParameters ) throws Throwable {
	
		System.err.print( "Reading conf file into memory..." );
		CometPTMParameters cometParams = CometPTMParamsReader.getMSFraggerParameters( conversionParameters.getFonfFile() );
		System.err.println( " Done." );
		
		System.err.print( "Reading pepXML data into memory..." );
		CometResults cometResults = PepXMLResultsParser.getTPPResults( conversionParameters.getPepXMLFile(), cometParams, conversionParameters.getDecoyPrefixOverride() );
		System.err.println( " Done." );
		
		System.err.print( "Performing FDR analysis of Comet E-values..." );
		TargetDecoyCounts tdCounts = TargetDecoyCountFactory.getTargetDecoyCountsByEvalue( cometResults );
		TargetDecoyAnalysis tdAnalysis = TargetDecoyAnalysisFactory.createTargetDecoyAnalysis( tdCounts, TargetDecoyAnalysisFactory.LOWER_IS_BETTER );
		System.err.println( " Done." );


		System.err.print( "Writing out XML..." );
		(new XMLBuilder()).buildAndSaveXML( conversionParameters, cometResults, cometParams, tdAnalysis );
		System.err.println( " Done." );
		
	}
}
