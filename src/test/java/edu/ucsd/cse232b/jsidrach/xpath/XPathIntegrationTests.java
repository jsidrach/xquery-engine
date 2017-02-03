package edu.ucsd.cse232b.jsidrach.xpath;

import org.junit.Test;

/**
 * XPathIntegrationTests - Integration tests for XPath
 */
public class XPathIntegrationTests extends XPathTests {

    /**
     * Integration tests for XPath
     */
    @Test
    public void IntegrationTests() {
        String resourcesDir = "integration-tests/it";
        int numTestCases = 5;
        runTestSuite(resourcesDir, numTestCases);
    }
}
