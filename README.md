# OIDMatcher

It is a small util to help check the OID input match one of the prefixes in the YAML file with key trap-type-oid-prefix

## Compile / package
```
mvn clean package
```

## Run
java -jar OIDMatcher-1.0-SNAPSHOT.jar <YAML_FILENAME>
```
java -jar target/OIDMatcher-1.0-SNAPSHOT.jar  ./src/test/resources/prefixes.yaml
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
