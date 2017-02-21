#!/bin/bash

echo "Executing XPath query contained in input.txt"
echo "---"
cat "input.txt"
echo "---"
echo "Storing results in output.txt"
echo "---"
java -jar xpath.jar input.txt > output.xml
cat output.xml
