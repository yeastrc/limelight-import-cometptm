package org.yeastrc.limelight.xml.cometptm.reader;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Test_CometPTMParamsReader_getStaticMods {

    private InputStream _IS = null;

    @Before
    public void setUp() {
        StringBuilder sb = new StringBuilder();

        sb.append( "# additional modifications\n" );
        sb.append( "#\n" );
        sb.append( "\n" );
        sb.append( "add_Cterm_peptide = 0.0\n" );
        sb.append( "add_Nterm_peptide = 0.0\n" );
        sb.append( "add_Cterm_protein = 0.0\n" );
        sb.append( "add_Nterm_protein = 0.0\n" );
        sb.append( "\n" );

        sb.append( "add_G_glycine = 0.0000                 # added to G - avg.  57.0513, mono.  57.02146\n" );
        sb.append( "add_A_alanine = 0.0000                 # added to A - avg.  71.0779, mono.  71.03711\n" );
        sb.append( "add_S_serine = 57.021464               # added to S - avg.  87.0773, mono.  87.03203\n" );
        sb.append( "add_P_proline = 0.0000                 # added to P - avg.  97.1152, mono.  97.05276\n" );
        sb.append( "add_V_valine = 36.3993\n" );
        sb.append( "add_T_threonine = 0.0000               # added to T - avg. 101.1038, mono. 101.04768\n" );
        sb.append( "\n" );

        _IS = IOUtils.toInputStream( sb.toString(), Charset.defaultCharset() );

    }


    @Test
    public void testGetStaticMods() throws IOException {

        Map<Character, Double> staticMods = CometPTMParamsReader.getStaticModsFromParamsFile( _IS );

        Map<Character, Double> testStaticMods = new HashMap<>();
        testStaticMods.put( 'S', 57.021464 );
        testStaticMods.put( 'V', 36.3993 );

        assertEquals( testStaticMods, staticMods );


    }

}
