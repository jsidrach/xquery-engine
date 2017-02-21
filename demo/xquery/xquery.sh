#!/bin/bash

echo "Executing XQuery query contained in input.txt"
echo "---"
cat "input.txt"
echo "---"
echo "Storing results in output.xml"
echo "---"
java -jar xquery.jar input.txt > output.xml
cat output.xml
