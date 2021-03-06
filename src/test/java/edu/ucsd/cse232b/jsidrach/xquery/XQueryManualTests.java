package edu.ucsd.cse232b.jsidrach.xquery;

import edu.ucsd.cse232b.jsidrach.utils.IO;
import edu.ucsd.cse232b.jsidrach.utils.XQueryEngine;
import org.junit.Test;
import org.w3c.dom.Node;

import java.io.Writer;
import java.util.LinkedList;

import static org.junit.Assert.fail;

/**
 * XQueryManualTests - Generates test XMLs, to be checked manually
 */
public class XQueryManualTests extends XQueryTests {

    /**
     * Generates the output of the provided test cases
     */
    @Test
    public void ProvidedTests() {
        String baseFolder = "manual-tests-provided/";
        String baseNameInput = baseFolder + "m-input-";
        String baseNameOutput = baseFolder + "m-output-";
        int numProvidedQueries = 6;
        for (int i = 1; i <= numProvidedQueries; ++i) {
            try {
                String fileNameInput = baseNameInput + i + ".txt";
                String fileNameOutput = baseNameOutput + i + ".xml";
                LinkedList<Node> nodes = XQueryEngine.Query(getResource(fileNameInput), false);
                Writer output = getResourceWriter(fileNameOutput);
                output.write(IO.NodesToString(nodes, false));
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
                fail("Failed (exception) " + baseNameInput + i);
            }
        }
    }
}
