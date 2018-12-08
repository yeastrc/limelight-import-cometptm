package org.yeastrc.limelight.xml.cometptm.reader;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class TargetDecoyAnalysisFactory {

    public static final int HIGHER_IS_BETTER = 0;
    public static final int LOWER_IS_BETTER = 1;


    /**
     * Create a target decoy analysis given the supplied target decoy counts (essentially a map of
     * the numbers of targets and decoys have each score).
     *
     * @param counts The target decoy counts indexed by score
     * @param direction The direction in which better scores can be found (higher or lower), see TargetDecoyAnalysis.HIGHER_IS_BETTER and TargetDecoyAnalysis.LOWER_IS_BETTER
     * @return
     */
    public static TargetDecoyAnalysis createTargetDecoyAnalysis( TargetDecoyCounts counts, int direction ) {

        Map<BigDecimal, Integer> totalTargetCountsAtScoreOrBetter = buildCountSums( counts.getTargetCounts(), direction );
        Map<BigDecimal, Integer> totalDecoyCountsAtScoreOrBetter = buildCountSums( counts.getDecoyCounts(), direction );

        return new TargetDecoyAnalysis( totalTargetCountsAtScoreOrBetter, totalDecoyCountsAtScoreOrBetter );
    }

    private static Map<BigDecimal, Integer> buildCountSums( Map<BigDecimal, Integer> scoreCounts, int direction ) {
        Map<BigDecimal, Integer> scoreSums = new HashMap<>();

        for( BigDecimal score : scoreCounts.keySet() ) {
            int count = scoreCounts.get( score );

            scoreSums.put( score, count );
            addCountToAllBetterScores( scoreSums, score, count, direction );
        }

        return scoreSums;
    }

    private static void addCountToAllBetterScores( Map<BigDecimal, Integer> scoreSums, BigDecimal score, int scoreCount, int direction ) {

        for( BigDecimal testScore : scoreSums.keySet() ) {

            if( direction == HIGHER_IS_BETTER && testScore.compareTo( score ) > 0 ) {
                scoreSums.put( score, scoreSums.get( score ) + scoreCount );
            } else if( direction == LOWER_IS_BETTER && testScore.compareTo( score ) < 0 ) {
                scoreSums.put( score, scoreSums.get( score ) + scoreCount );
            }
        }
    }

}
