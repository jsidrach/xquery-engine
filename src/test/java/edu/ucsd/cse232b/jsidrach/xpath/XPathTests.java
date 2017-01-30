package edu.ucsd.cse232b.jsidrach.xpath;

import edu.ucsd.cse232b.jsidrach.utils.IO;
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

/**
 * XPathTests - Base class for XPath tests
 */
abstract class XPathTests {

    /**
     * Returns a FileInputStream with the contents of an input resource
     *
     * @param name Name of the input resource (without path, assuming .txt extension)
     * @return FileInputStream with the contents of the input resource
     * @throws Exception Exception if input resource is not found
     */
    FileInputStream getResourceInput(String name) throws Exception {
        String filePath = "edu/ucsd/cse232b/jsidrach/xpath/" + name + ".txt";
        ClassLoader classLoader = getClass().getClassLoader();
        URI uri = new URI(classLoader.getResource(filePath).getFile());
        return new FileInputStream(uri.getPath());
    }

    /**
     * Returns a Writer
     *
     * @param name Name of the output resource (without path, assuming .xml extension)
     * @return Writer to the output resource
     * @throws Exception Exception if any output folder is missing
     */
    Writer getResourceOutput(String name) throws Exception {
        String filePath = "src/test/resources/edu/ucsd/cse232b/jsidrach/xpath/" + name + ".xml";
        return new BufferedWriter(new FileWriter(filePath));
    }

    /**
     * Loads an XML
     *
     * @param name Name of the input resource (without path, assuming .xml extension)
     * @return List of nodes corresponding to the children of the root of the loaded document
     * @throws Exception Exception if input resource is not found or it has invalid format
     */
    List<Node> loadXMLResource(String name) throws Exception {
        FileInputStream xmlFile = getResourceInput(name);
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        // Ignore non-relevant whitespace
        docFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(xmlFile);
        // Normalize document
        doc.getDocumentElement().normalize();
        // Add the children (XML resource should have a root which is ignored)
        List<Node> nodes = new LinkedList<>();
        NodeList nl = doc.getDocumentElement().getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            nodes.add(nl.item(i));
        }
        return nodes;
    }

    /**
     * Compares a list of nodes with an XML resource
     * - the XML resource should have a root node and then all the result nodes
     *
     * @param nodes List of nodes to compare with the resource
     * @param name  Name of the resource to compare to (without path, assuming .xml extension)
     * @return true if the list of nodes is equal to the loaded resource, false otherwise
     * @throws Exception Exception if the resource is not found or has invalid format
     */
    Boolean nodesEqualToResource(List<Node> nodes, String name) throws Exception {
        return IO.NodesToString(nodes).equals(IO.NodesToString(loadXMLResource(name)));
    }
}
