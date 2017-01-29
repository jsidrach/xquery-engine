package edu.ucsd.cse232b.jsidrach.xpath;

import edu.ucsd.cse232b.jsidrach.utils.IO;
import org.junit.Test;
import org.w3c.dom.Node;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.Writer;
import java.net.URI;
import java.util.List;

/**
 * XPathManualTests - Generates test XMLs, to be checked manually
 */
public class XPathManualTests {

    /**
     * Returns a FileInputStream with the contents of an input resource
     *
     * @param name Name of the input resource (without path, assuming .txt extension)
     * @return FileInputStream with the contents of the input resource
     * @throws Exception Exception if input resource is not found
     */
    private FileInputStream getResourceInput(String name) throws Exception {
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
    private Writer getResourceOutput(String name) throws Exception {
        String filePath = "src/test/resources/edu/ucsd/cse232b/jsidrach/xpath/" + name + ".xml";
        return new BufferedWriter(new FileWriter(filePath));
    }

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
