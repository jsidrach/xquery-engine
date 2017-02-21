#!/bin/bash

echo "Formatting XQuery query contained in input.txt"
echo "---"
cat "input.txt"
echo "---"
echo "Storing formatted query in output.txt"
echo "---"
java -jar formatter.jar input.txt > output.txt
cat output.txt
