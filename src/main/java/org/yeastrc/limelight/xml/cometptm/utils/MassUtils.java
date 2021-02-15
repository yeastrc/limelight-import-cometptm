package org.yeastrc.limelight.xml.cometptm.utils;

import org.yeastrc.limelight.xml.cometptm.constants.MassConstants;
import org.yeastrc.limelight.xml.cometptm.objects.CometPSM;

import java.math.BigDecimal;

public class MassUtils {

    /**
     * Get the calculated m/z for a precursor for a psm that is (neutral mass + charge * hydrogen mass) / charge
     *
     * @param psm
     * @return
     */
    public static BigDecimal getObservedMoverZForPsm(CometPSM psm) {

        final double charge = psm.getCharge();
        final double neutralMass = psm.getPrecursorNeutralMass().doubleValue();
        final double observedMoverZ = (MassConstants.MASS_HYDROGEN_MONO.doubleValue() * charge + neutralMass) / charge;

        return BigDecimal.valueOf(observedMoverZ);
    }

}
