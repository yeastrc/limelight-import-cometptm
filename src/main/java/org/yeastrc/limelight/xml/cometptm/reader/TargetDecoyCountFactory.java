package org.yeastrc.limelight.xml.cometptm.reader;

import org.yeastrc.limelight.xml.cometptm.objects.CometPSM;
import org.yeastrc.limelight.xml.cometptm.objects.CometReportedPeptide;
import org.yeastrc.limelight.xml.cometptm.objects.CometResults;

public class TargetDecoyCountFactory {

    public static TargetDecoyCounts getTargetDecoyCountsByEvalue(CometResults cometResults) {

        TargetDecoyCounts tdCounts = new TargetDecoyCounts();

        for (CometReportedPeptide crp : cometResults.getPeptidePSMMap().keySet()) {
            for (int scanNumber : cometResults.getPeptidePSMMap().get(crp).keySet()) {
                CometPSM psm = cometResults.getPeptidePSMMap().get(crp).get(scanNumber);

                if (psm.getDecoy())
                    tdCounts.addDecoy(psm.getEvalue());
                else
                    tdCounts.addTarget(psm.getEvalue());

            }
        }

        return tdCounts;
    }
}
