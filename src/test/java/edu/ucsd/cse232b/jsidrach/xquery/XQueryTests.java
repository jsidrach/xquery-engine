package edu.ucsd.cse232b.jsidrach.xquery;

import edu.ucsd.cse232b.jsidrach.utils.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.Writer;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.fail;

/**
 * XQueryTests - Base class for XQuery tests
 */
abstract class XQueryTests {

    /**
     * Returns a FileInputStream with the contents of a resource
     *
     * @param name Name of the input resource (without path)
     * @return FileInputStream with the contents of the resource
     * @throws Exception Exception if the resource is not found
     */
    FileInputStream getResource(String name) throws Exception {
        String filePath = "edu/ucsd/cse232b/jsidrach/xquery/" + name;
        ClassLoader classLoader = getClass().getClassLoader();
        URI uri = new URI(classLoader.getResource(filePath).getFile());
        return new FileInputStream(uri.getPath());
    }

    /**
     * Returns a resource writer
     *
     * @param name Name of the output resource (without path)
     * @return Writer to the output resource
     * @throws Exception Exception if any output folder is missing
     */
    Writer getResourceWriter(String name) throws Exception {
        String filePath = "src/test/resources/edu/ucsd/cse232b/jsidrach/xquery/" + name;
        return new BufferedWriter(new FileWriter(filePath));
    }

    /**
     * Loads a XML
     *
     * @param name Name of the resource (without path)
     * @return List of nodes corresponding to the children of the root of the loaded document
     * @throws Exception Exception if input resource is not found or it has invalid format
     */
    LinkedList<Node> loadResourceAsXML(String name) throws Exception {
        FileInputStream xmlFile = getResource(name);
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        // Ignore non-relevant whitespace
        docFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(xmlFile);
        // Normalize document
        doc.getDocumentElement().normalize();
        // Add the children (XML resource should have a root which is ignored)
        LinkedList<Node> nodes = new LinkedList<>();
        NodeList nl = doc.getDocumentElement().getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            nodes.add(nl.item(i));
        }
        return nodes;
    }

    /**
     * Loads a resource into a string
     *
     * @param name Name of the input resource (without path)
     * @return Resource as a string
     * @throws Exception Exception if input resource is not found or it has invalid format
     */
    String loadResourceAsString(String name) throws Exception {
        FileInputStream file = getResource(name);
        Scanner s = new Scanner(file).useDelimiter("\\A");
        return s.hasNext() ? s.next().trim() : "";
    }

    /**
     * Compares a list of nodes with an string resource
     *
     * @param nodes List of nodes to compare with the resource
     * @param name  Name of the resource to compare to (without path)
     * @return true if the list of nodes is equal to the loaded resource, false otherwise
     * @throws Exception Exception if the resource is not found or has invalid format
     */
    Boolean nodesEqualToResource(List<Node> nodes, String name) throws Exception {
        String s1 = IO.NodesToString(nodes, false).trim();
        String s2 = loadResourceAsString(name);
        boolean equal = s1.equals(s2);
        if (!equal) {
            System.err.println("[Result]");
            System.err.println(s1);
            System.err.println("[Expected]");
            System.err.println(s2);
        }
        return equal;
    }

    /**
     * Evaluates a test suite, by executing for every resource the input XQuery query,
     * comparing the results to the output XML
     *
     * @param resourcesPrefix Resources prefix, relative to the test folder
     *                        (src/main/test/java/edu/ucsd/cse232b/jsidrach/xquery)
     * @param numTests        Number of resources contained in the test suite
     *                        The names of the resources must be resourcePrefix-input-$i.txt and resourcePrefix-output.xml,
     *                        for $i from 1 to numTests
     */
    void runTestSuite(String resourcesPrefix, int numTests) {
        for (int i = 1; i <= numTests; ++i) {
            try {
                String input = resourcesPrefix + "-input-" + i + ".txt";
                String output = resourcesPrefix + "-output-" + i + ".xml";
                LinkedList<Node> nodes = XQueryEngine.Query(getResource(input), false);
                // Compare using standard engine
                if (!nodesEqualToResource(nodes, output)) {
                    fail("Failed (assertion) " + resourcesPrefix + "-" + i);
                }
                // Compare with formatted query
                String formattedQuery = XQueryFormatterEngine.Format(getResource(input));
                nodes = XQueryEngine.Query(formattedQuery, false);
                if (!nodesEqualToResource(nodes, output)) {
                    fail("Failed (formatted, assertion) " + resourcesPrefix + "-" + i);
                }
                // Check that the query formatter is idempotent
                if (!formattedQuery.equals(XQueryFormatterEngine.Format(formattedQuery))) {
                    fail("Failed (formatted, idempotent) " + resourcesPrefix + "-" + i);
                }
                // Compare with renamed variables query
                String renamedQuery = XQueryVarsRenamerEngine.RenameVars(getResource(input));
                nodes = XQueryEngine.Query(renamedQuery, false);
                if (!nodesEqualToResource(nodes, output)) {
                    fail("Failed (renamed, assertion) " + resourcesPrefix + "-" + i);
                }
                // Check that the query variable renamer is idempotent
                if (!renamedQuery.equals(XQueryVarsRenamerEngine.RenameVars(renamedQuery))) {
                    fail("Failed (renamed, idempotent) " + resourcesPrefix + "-" + i);
                }
                // Compare using optimized engine
                String optimizedQuery = XQueryOptimizerEngine.Optimize(getResource(input), false);
                nodes = XQueryEngine.Query(optimizedQuery, false);
                if (!nodesEqualToResource(nodes, output)) {
                    fail("Failed (optimized, assertion) " + resourcesPrefix + "-" + i);
                }
                // Check that the query optimizer is idempotent
                if (!optimizedQuery.equals(XQueryOptimizerEngine.Optimize(optimizedQuery, false))) {
                    fail("Failed (optimized, idempotent) " + resourcesPrefix + "-" + i);
                }
            } catch (Exception e) {
                e.printStackTrace();
                fail("Failed (exception) " + resourcesPrefix + "-" + i);
            }
        }
    }
}
