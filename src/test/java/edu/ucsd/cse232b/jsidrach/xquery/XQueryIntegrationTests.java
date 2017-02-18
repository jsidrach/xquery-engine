package edu.ucsd.cse232b.jsidrach.xquery;

import org.junit.Test;

/**
 * XQueryIntegrationTests - Integration tests for XQuery
 */
public class XQueryIntegrationTests extends XQueryTests {

    /**
     * Integration tests for XQuery
     */
    @Test
    public void IntegrationTests() {
        String resourcesDir = "integration-tests/it";
        int numTestCases = 5;
        // TODO
        runTestSuite(resourcesDir, numTestCases);
    }
}
