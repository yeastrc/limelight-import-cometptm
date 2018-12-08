package org.yeastrc.limelight.xml.cometptm.reader;

import java.math.BigDecimal;
import java.util.Map;

public class TargetDecoyAnalysis {

    private Map<BigDecimal, Integer> totalTargetCountsAtScoreOrBetter;
    private Map<BigDecimal, Integer> totalDecoyCountsAtScoreOrBetter;


    protected TargetDecoyAnalysis(Map<BigDecimal, Integer> totalTargetCountsAtScoreOrBetter, Map<BigDecimal, Integer> totalDecoyCountsAtScoreOrBetter) {

        this.totalTargetCountsAtScoreOrBetter = totalTargetCountsAtScoreOrBetter;
        this.totalDecoyCountsAtScoreOrBetter = totalDecoyCountsAtScoreOrBetter;

    }

    public Map<BigDecimal, Integer> getTotalTargetCountsAtScoreOrBetter() {
        return totalTargetCountsAtScoreOrBetter;
    }

    public Map<BigDecimal, Integer> getTotalDecoyCountsAtScoreOrBetter() {
        return totalDecoyCountsAtScoreOrBetter;
    }

    public double getEstimatedFDR(BigDecimal score) throws Exception {

        if (!this.totalDecoyCountsAtScoreOrBetter.containsKey(score) ||
            !this.totalTargetCountsAtScoreOrBetter.containsKey( score ) ) {

            throw new Exception( "Supplied score was not found in the data used to train this target/decoy analysis." );
        }

        double decoyCount = this.totalDecoyCountsAtScoreOrBetter.get( score ).doubleValue();
        double targetCount = this.totalTargetCountsAtScoreOrBetter.get( score ).doubleValue();

        return decoyCount / ( decoyCount + targetCount );
    }
}
