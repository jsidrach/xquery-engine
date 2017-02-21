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
        // TODO: Add test cases PDF, think of new integration test cases -> 6 to 12
        runTestSuite(resourcesDir, numTestCases);
    }

    /**
     * Integration tests for XPath
     */
    @Test
    public void IntegrationTestsXPath() {
        String resourcesDir = "integration-tests/xpath/";

        // Integration tests from XPath, as we integrated it into XQuery
        runTestSuite(resourcesDir + "integration-tests/it", 5);

        // Unit tests from XPath, as we integrated it into XQuery
        String[] methodsThree = {
                "unit-tests-eval/attrib",
                "unit-tests-eval/children",
                "unit-tests-eval/descendants-or-self",
                "unit-tests-eval/parent",
                "unit-tests-eval/tag",
                "unit-tests-eval/txt",
                "unit-tests-eval/unique"
        };
        String[] methodsTwo = {
                "unit-tests-absolute-path/children",
                "unit-tests-absolute-path/all",
                "unit-tests-relative-path/tag",
                "unit-tests-relative-path/wildcard",
                "unit-tests-relative-path/current",
                "unit-tests-relative-path/parent",
                "unit-tests-relative-path/text",
                "unit-tests-relative-path/attribute",
                "unit-tests-relative-path/parentheses",
                "unit-tests-relative-path/children",
                "unit-tests-relative-path/all",
                "unit-tests-relative-path/filter",
                "unit-tests-relative-path/pair",
                "unit-tests-filter/relative-path",
                "unit-tests-filter/value-equality",
                "unit-tests-filter/identity-equality",
                "unit-tests-filter/parentheses",
                "unit-tests-filter/and",
                "unit-tests-filter/or",
                "unit-tests-filter/not"
        };
        for (String m : methodsThree) {
            runTestSuite(resourcesDir + m, 3);
        }
        for (String m : methodsTwo) {
            runTestSuite(resourcesDir + m, 2);
        }
    }
}