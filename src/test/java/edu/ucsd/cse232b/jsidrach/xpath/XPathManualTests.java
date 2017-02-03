package edu.ucsd.cse232b.jsidrach.xpath;

import edu.ucsd.cse232b.jsidrach.utils.IO;
import org.junit.Test;
import org.w3c.dom.Node;

import java.io.Writer;
import java.util.List;

import static org.junit.Assert.fail;

/**
 * XPathManualTests - Generates test XMLs, to be checked manually
 */
public class XPathManualTests extends XPathTests {

    /**
     * Generates the output of the provided test cases
     */
    @Test
    public void ProvidedTests() {
        String baseFolder = "manual-tests-provided/";
        String baseNameInput = baseFolder + "m-input-";
        String baseNameOutput = baseFolder + "m-output-";
        int numProvidedQueries = 5;
        for (int i = 1; i <= numProvidedQueries; ++i) {
            try {
                String fileNameInput = baseNameInput + i + ".txt";
                String fileNameOutput = baseNameOutput + i + ".xml";
                List<Node> nodes = IO.XPathQuery(getResource(fileNameInput));
                Writer output = getResourceWriter(fileNameOutput);
                output.write(IO.NodesToString(nodes));
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
                fail("Failed (exception) " + baseNameInput + i);
            }
        }
    }
}
