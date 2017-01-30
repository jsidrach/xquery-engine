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
        String baseNameInput = baseFolder + "input-";
        String baseNameOutput = baseFolder + "output-";
        int numProvidedQueries = 5;
        for (int i = 1; i <= numProvidedQueries; ++i) {
            try {
                String fileNameInput = baseNameInput + i;
                String fileNameOutput = baseNameOutput + i;
                List<Node> nodes = IO.XPathQuery(getResourceInput(fileNameInput));
                Writer output = getResourceOutput(fileNameOutput);
                output.write(IO.NodesToString(nodes));
                output.close();
            } catch (Exception e) {
                fail("Failed (exception) " + baseNameInput + i);
            }
        }
    }
}
