package edu.ucsd.cse232b.jsidrach.xpath;

import edu.ucsd.cse232b.jsidrach.utils.IO;
import org.junit.Test;
import org.w3c.dom.Node;

import java.io.Writer;
import java.util.List;

/**
 * XPathManualTests - Generates test XMLs, to be checked manually
 */
public class XPathManualTests extends XPathTests {

    /**
     * Generates the output of the provided test cases
     *
     * @throws Exception Exception if any input resource is not found
     */
    @Test
    public void provided() throws Exception {
        String baseNameInput = "provided-input-";
        String baseNameOutput = "provided-output-";
        int numProvidedQueries = 5;
        for (int i = 1; i <= numProvidedQueries; ++i) {
            String fileNameInput = baseNameInput + i;
            String fileNameOutput = baseNameOutput + i;
            List<Node> nodes = IO.XPathQuery(getResourceInput(fileNameInput));
            Writer output = getResourceOutput(fileNameOutput);
            output.write(IO.NodesToString(nodes));
            output.close();
        }
    }

    // TODO: Add more manual tests
}
