# Simplified XQuery Engine [Archived]
### UCSD CSE 232B Project
#### [Final Report](docs/report/report.pdf)

## Build

Requires gradle:
```bash
gradle build
```

This will run all tests, compile and place the fat jars into sub-folders of ```demo/```, and generate the *javadoc* documentation

## Run

#### XPath

* Navigate to the directory:
  ```bash
  cd demo/xpath/
  ```
  Save the XPath query in ```input.txt```

  To run the XPath query, execute
  ```bash
  ./xpath.sh
  ```
  The resulting list of nodes will be stored in ```output.xml``` and displayed in the terminal

#### XQuery

* Navigate to the directory:
  ```bash
  cd demo/xquery/
  ```
  Save the XQuery query in ```input.txt```

  To run the XQuery query, execute
  ```bash
  ./xquery.sh
  ```
  The resulting document will be stored in ```output.xml``` and displayed in the terminal

#### Optimized XQuery

* Run optimized XQuery:
  ```bash
  cd demo/optimized-xquery/
  ```
  Save the XQuery query in ```input.txt```

  To run the XQuery query, execute
  ```bash
  ./optimized-xquery.sh
  ```
  The intermediate rewritten queries will be stored in ```rewritten.txt```

  The resulting document will be stored in ```output.xml``` and displayed in the terminal

#### XQuery Formatter

* Run XQuery formatter:
  ```bash
  cd demo/formatter/
  ```
  Save the XQuery query in ```input.txt```

  To format the XQuery query, execute
  ```bash
  ./formatter.sh
  ```
  The formatted query will be stored in ```output.txt``` and displayed in the terminal

## Project Structure

```
demo/                             - Executables
docs/                             - Project documentation
  javadoc/                        -   Code documentation
  references/                     -   Simplified XQuery references
  report/                         -   Final project report
src/                              - Source code
  main/                           -   Implementation
    antlr4/                       -     XQuery and XPath grammar definitions
    java/edu/cse232b/jsidrach/    -     Java code
      apps/                       -       Command-line executables
      utils/                      -       IO auxiliary classes
      xpath/                      -       XPath engine
      xquery/                     -       XQuery engine
        optimized/                -         XQuery join optimizator
  test/                           -   Unit and integration tests
    java/                         -     Tests code
    resources/                    -     Input (XQuery) and expected output (XML)
```

## License
[MIT](LICENSE)

