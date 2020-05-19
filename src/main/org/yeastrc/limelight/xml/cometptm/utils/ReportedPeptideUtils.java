package org.yeastrc.limelight.xml.cometptm.utils;

import org.yeastrc.limelight.xml.cometptm.objects.CometPSM;
import org.yeastrc.limelight.xml.cometptm.objects.CometReportedPeptide;
import org.yeastrc.limelight.xml.cometptm.objects.CometResults;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ReportedPeptideUtils {

	public static CometReportedPeptide getCometReportedPeptideForCometPSM(CometPSM psm ) throws Exception {
		
		CometReportedPeptide rp = new CometReportedPeptide();
		
		rp.setNakedPeptide( psm.getPeptideSequence() );
		rp.setMods( psm.getModifications() );
		rp.setOpenModMass(psm.getMassDiff().setScale(0, RoundingMode.HALF_UP));
		rp.setReportedPeptideString( ModParsingUtils.getRoundedReportedPeptideString( psm.getPeptideSequence(), psm.getModifications(), psm.getMassDiff() ));
		return rp;
	}

	/**
	 * Return true if the given cometReportedPeptide only contains decoys in the given set of comet results.
	 *
	 * @param cometResults
	 * @param cometReportedPeptide
	 * @return
	 */
	public static boolean reportedPeptideOnlyContainsDecoys( CometResults cometResults, CometReportedPeptide cometReportedPeptide ) {

		for( int scanNumber : cometResults.getPeptidePSMMap().get( cometReportedPeptide ).keySet() ) {

			CometPSM psm = cometResults.getPeptidePSMMap().get( cometReportedPeptide ).get( scanNumber );
			if( !psm.getDecoy() ) {
				return false;
			}
		}

		return true;
	}

}
