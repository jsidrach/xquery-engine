package edu.ucsd.cse232b.jsidrach.apps;

import edu.ucsd.cse232b.jsidrach.utils.IO;
import edu.ucsd.cse232b.jsidrach.utils.XPathEngine;

import java.io.FileInputStream;

/**
 * XPath - Command line utility for XPath
 * <p>
 * The executable takes one argument only,
 * the name of the file containing the XPath query
 * </p>
 */
public class XPath {
    public static void main(String[] args) {
        // Check number of arguments
        if (args.length != 1) {
            System.out.println("Invalid number of arguments");
            System.out.println("java -jar <jar_file> <xpath_query>");
            return;
        }
        // Print the result of executing the xpath query
        try {
            FileInputStream input = new FileInputStream(args[0]);
            System.out.print(IO.NodesToString(XPathEngine.Query(input), true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
