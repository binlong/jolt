package com.bazaarvoice.jolt;

import org.apache.commons.lang.StringUtils;
import org.testng.AssertJUnit;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SortrTest {

    @DataProvider
    public Object[][] getTestCaseNames() {
        return new Object[][] {
            {"simple", null, null, null}
        };
    }

    @Test(dataProvider = "getTestCaseNames")
    public void runTestCases(String testCaseName, String inputPath, String specPath, String outputPath)
            throws IOException, JoltException {

        if ("".equals( testCaseName )) {
            return;
        }

        String testPath = "/json/sortr/"+testCaseName;
        Map<String, Object> input = (Map<String, Object>) JsonUtils.jsonToObject( Shiftr.class.getResourceAsStream( inputPath == null ? testPath + "/input.json" : inputPath ) );
        Object spec = JsonUtils.jsonToObject( Shiftr.class.getResourceAsStream( specPath == null ? testPath + "/spec.json" : specPath ) );
        Map<String, Object> expected = (Map<String, Object>) JsonUtils.jsonToObject( Shiftr.class.getResourceAsStream( outputPath == null ? testPath + "/output.json" : outputPath ) );

        Sortr sortr = new Sortr();
        Map<String, Object> actual = (Map<String, Object>) sortr.sort( input, spec );

        JoltTestUtil.runDiffy( "Make sure it is still the same object : " + testPath, expected, actual );

        String orderErrorMessage = verifyOrder( actual, expected );
        AssertJUnit.assertNull( orderErrorMessage, orderErrorMessage );
    }

    public static String verifyOrder( Object actual, Object expected ) {
        if ( actual instanceof Map && expected instanceof Map ) {
            return verifyMapOrder( (Map<String, Object>) actual, (Map<String, Object>) expected );
        } else if ( actual instanceof List && expected instanceof List ) {
            return verifyListOrder( (List<Object>) actual, (List<Object>) expected ) ;
        } else {
            return null;
        }
    }

    private static String verifyMapOrder( Map<String,Object> actual, Map<String,Object> expected ) {

        Iterator<String> actualIter = actual.keySet().iterator();
        Iterator<String> expectedIter = expected.keySet().iterator();

        for( int index = 0; index < actual.size(); index++ ) {
            String actualKey = actualIter.next();
            String expectedKey = expectedIter.next();

            if ( ! StringUtils.equals( actualKey, expectedKey ) ) {
                return "Found out of order keys '" + actualKey + "' and '" + expectedKey + "'";
            }

            verifyOrder( actual.get( actualKey), expected.get(expectedKey) );
        }

        return null;
    }

    private static String verifyListOrder( List<Object> actual, List<Object> expected ) {

        for( int index = 0; index < actual.size(); index++ ) {
            verifyOrder( actual.get( index ), expected.get(index) );
        }

        return null;
    }
}