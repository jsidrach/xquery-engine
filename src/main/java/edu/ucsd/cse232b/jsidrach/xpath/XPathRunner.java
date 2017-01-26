package edu.ucsd.cse232b.jsidrach.xpath;

import edu.ucsd.cse232b.jsidrach.utils.IO;

/**
 * XPathRunner - Command line utility for XPath
 * <p>
 * The executable takes one argument only,
 * the name of the file containing the XPath query
 * </p>
 */
public class XPathRunner {
    public static void main(String[] args) {
        // Check number of arguments
        if (args.length != 1) {
            System.out.println("Invalid number of arguments");
            System.out.println("java -jar <jar_file> <xpath_query>");
            return;
        }

        // Print the result of executing the xpath query
        try {
            System.out.println(IO.NodesToString(IO.XPathQuery(args[0])));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
