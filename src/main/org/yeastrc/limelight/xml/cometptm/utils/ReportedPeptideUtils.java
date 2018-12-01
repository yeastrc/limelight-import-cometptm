package org.yeastrc.limelight.xml.cometptm.utils;

import org.yeastrc.limelight.xml.cometptm.objects.CometPSM;
import org.yeastrc.limelight.xml.cometptm.objects.CometReportedPeptide;

public class ReportedPeptideUtils {

	public static CometReportedPeptide getCometReportedPeptideForCometPSM(CometPSM psm ) throws Exception {
		
		CometReportedPeptide rp = new CometReportedPeptide();
		
		rp.setNakedPeptide( psm.getPeptideSequence() );
		rp.setMods( psm.getModifications() );
		rp.setReportedPeptideString( ModParsingUtils.getRoundedReportedPeptideString( psm.getPeptideSequence(), psm.getModifications() ));

		return rp;
	}

}
