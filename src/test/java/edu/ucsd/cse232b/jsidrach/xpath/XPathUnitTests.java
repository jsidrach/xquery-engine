package edu.ucsd.cse232b.jsidrach.xpath;


import org.junit.Test;

/**
 * XPathUnitTests - Unit tests for XPath
 */
public class XPathUnitTests extends XPathTests {

    /**
     * Unit tests for the XPathEvaluator methods
     */
    @Test
    public void EvaluatorTests() {
        String[] methods = {
                "attrib",
                "children",
                "parent",
                "tag",
                "txt",
                "unique"
        };
        String resourcesDir = "unit-tests-eval/";
        int numTestCases = 3;
        for (String m : methods) {
            runTestSuite(resourcesDir + m, numTestCases);
        }
    }

    /**
     * Unit tests for the XPathVisitor methods related to absolute path rules
     */
    @Test
    public void VisitorAbsolutePathTests() {
        String[] methods = {
                "children",
                "all"
        };
        String resourcesDir = "unit-tests-absolute-path/";
        int numTestCases = 2;
        for (String m : methods) {
            runTestSuite(resourcesDir + m, numTestCases);
        }
    }

    /**
     * Unit tests for the XPathVisitor methods related to relative path rules
     */
    @Test
    public void VisitorRelativePathTests() {
        // TODO: Change numTestCases to 2
        // TODO: fill test resource files
        String[] methods = {
                "tag",
                "wildcard",
                "current",
                "parent",
                "text",
                "attribute",
                "parentheses",
                "children",
                "all",
                "filter",
                "pair"
        };
        String resourcesDir = "unit-tests-relative-path/";
        int numTestCases = 0;
        for (String m : methods) {
            runTestSuite(resourcesDir + m, numTestCases);
        }
    }

    /**
     * Unit tests for the XPathVisitor methods related to filter rules
     */
    @Test
    public void VisitorFilterTests() {
        // TODO: Change numTestCases to 2
        // TODO: fill test resource files
        String[] methods = {
                "relative-path",
                "value-equality",
                "identity-equality",
                "parentheses",
                "and",
                "or",
                "not"
        };
        String resourcesDir = "unit-tests-filter/";
        int numTestCases = 0;
        for (String m : methods) {
            runTestSuite(resourcesDir + m, numTestCases);
        }
    }
}
