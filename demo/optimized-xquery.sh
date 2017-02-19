#!/bin/bash

echo "Executing XQuery optimized query contained in optimized-xquery-input.txt"
echo "---"
cat "optimized-xquery-input.txt"
echo "---"
echo "Storing results in optimized-xquery-output.xml"
echo "---"
java -jar optimized-xquery.jar optimized-xquery-input.txt > optimized-xquery-output.xml 2> optimized-xquery-rewritten.txt
cat optimized-xquery-output.xml
