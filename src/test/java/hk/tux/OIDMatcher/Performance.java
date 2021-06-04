package hk.tux.OIDMatcher;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Performance {

  public static void main(String[] args) {
    List<OIDMatcher> matchers = new ArrayList<>();
    InputStream in = OIDPrefixTreeMatcher.class.getClassLoader().getResourceAsStream("prefixesBig.yaml");
    matchers.add(new OIDPrefixTreeMatcher(in));

    in = OIDCharTreeMatcher.class.getClassLoader().getResourceAsStream("prefixesBig.yaml");
    matchers.add(new OIDCharTreeMatcher(in));

    in = OIDPrefixTreeMatcher.class.getClassLoader().getResourceAsStream("prefixesBig.yaml");
    matchers.add(new OIDStringMatcher(in));

    in = OIDPrefixTreeMatcher.class.getClassLoader().getResourceAsStream("prefixesBig.yaml");
    matchers.add(new OIDRegexMatcher(in));

    int times = 5000;
    final String targetOid = ".1.3.6.1.6.3.1.123.4.999.19.200";
    final String failOid = ".1.3.6.1.6.3.1.123.4.1001.19.200";

    matchers.stream().forEach(matcher -> {
      long startTime = System.currentTimeMillis();
      IntStream.range(0, times).forEach($ -> {
        matcher.matchPrefix(targetOid);
        matcher.matchPrefix(failOid);
      });
      long endTime = System.currentTimeMillis();
      System.out.println(matcher.getClass().getSimpleName() + " takes:\t\t\t" + (endTime - startTime) + "ms\t"
          + "match: " + matcher.matchPrefix(targetOid) + "\tnon-match: " + matcher.matchPrefix(failOid));
    });
  }
}
