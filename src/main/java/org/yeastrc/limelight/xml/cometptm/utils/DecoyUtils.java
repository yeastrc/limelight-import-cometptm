package org.yeastrc.limelight.xml.cometptm.utils;

import org.yeastrc.limelight.xml.cometptm.objects.CometPTMParameters;
import org.yeastrc.limelight.xml.cometptm.objects.ConversionParameters;

public class DecoyUtils {

    public static String getDecoyPrefixToUse(CometPTMParameters cometParams, ConversionParameters conversionParameters ) throws Exception {

        String decoyPrefix = cometParams.getDecoyPrefix();

        if( decoyPrefix == null || decoyPrefix.length() < 1 ) {
            throw new Exception( "Could not determine a decoy prefix. It could not be found in the comet.params and none was supplied on command line." );
        }

        return decoyPrefix;
    }

}
