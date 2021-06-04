# OIDMatcher

It is a small util to help check the OID input match one of the prefixes in the YAML file with key trap-type-oid-prefix

## Package
```
mvn clean package
```

## Run
java -jar OIDMatcher-1.0-SNAPSHOT.jar <YAML_FILENAME>
```
java -jar target/OIDMatcher-1.0-SNAPSHOT.jar  ./src/test/resources/prefixes.yaml
```

example
```
java -jar target/OIDMatcher-1.0-SNAPSHOT.jar  ./src/test/resources/prefixesBig.yaml
CTRL-C / CTRL-D to exit!
.1.3.6.1.6.3.1.123.4.10
.1.3.6.1.6.3.1.123.4.10: true
.1.3.6.1.6.3.1.123.4.10.12
.1.3.6.1.6.3.1.123.4.10.12: true
.1.3.6.1.6.3.1.123.4.10.123454
.1.3.6.1.6.3.1.123.4.10.123454: true
.1.3.6.1.4.1.9.9.117.2.0.1
.1.3.6.1.4.1.9.9.117.2.0.1: true
.1.3.6.1.4.1.9.9.117
.1.3.6.1.4.1.9.9.117: false
.1.3.6.1.4.1.9.9.118.2.0.1
.1.3.6.1.4.1.9.9.118.2.0.1: false
```

## Performance testing
Prefixes size: 1006
Query: 5000 matching and 5000 non-matching
```
OIDPrefixTreeMatcher takes:    12ms match: true non-match: false
OIDCharTreeMatcher takes:       3ms match: true non-match: false
OIDStringMatcher takes:       444ms match: true non-match: false
OIDRegexMatcher takes:        694ms match: true non-match: false
```
Base on the performance result, the OIDMatcherCmd class is implement with OIDCharTreeMatcher
