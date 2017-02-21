#!/bin/bash

echo "Executing XQuery optimized query contained in input.txt"
echo "---"
cat "input.txt"
echo "---"
echo "Storing optimized query in rewritten.txt, results in output.xml"
echo "---"
java -jar optimized-xquery.jar input.txt > output.xml 2> rewritten.txt
cat output.xml
