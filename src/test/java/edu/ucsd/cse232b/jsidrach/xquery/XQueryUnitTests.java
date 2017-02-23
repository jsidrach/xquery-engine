package edu.ucsd.cse232b.jsidrach.xquery;


import org.junit.Test;

/**
 * XQueryUnitTests - Unit tests for XQuery
 */
public class XQueryUnitTests extends XQueryTests {

    /**
     * Unit tests for the XQueryVisitor methods related to XQuery root rules
     */
    @Test
    public void VisitorXQueryTests() {
        String[] methods = {
                "variable",
                "string-constant",
                "parentheses",
                "pair",
                "children",
                "all",
                "tag",
                "let"
        };
        String resourcesDir = "unit-tests-xquery/";
        int numTestCases = 3;
        for (String m : methods) {
            runTestSuite(resourcesDir + m, numTestCases);
        }
    }

    /**
     * Unit tests for the XQueryVisitor methods related to FLWR rules
     */
    @Test
    public void VisitorFLWRTests() {
        String resourcesDir = "unit-tests-flwr/flwr";
        int numTestCases = 6;
        runTestSuite(resourcesDir, numTestCases);
    }

    /**
     * Unit tests for the XQueryVisitor methods related to condition rules
     */
    @Test
    public void VisitorConditionTests() {
        String[] methods = {
                "value-equality",
                "identity-equality",
                "empty",
                "some",
                "parentheses",
                "and",
                "or",
                "not"
        };
        String resourcesDir = "unit-tests-condition/";
        int numTestCases = 3;
        for (String m : methods) {
            runTestSuite(resourcesDir + m, numTestCases);
        }
    }

    /**
     * Unit tests for the XQueryVisitor methods related to FLWR rules
     */
    @Test
    public void VisitorJoinTests() {
        String resourcesDir = "unit-tests-join/join";
        int numTestCases = 6;
        runTestSuite(resourcesDir, numTestCases);
    }
}
