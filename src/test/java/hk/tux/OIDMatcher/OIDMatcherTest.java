package hk.tux.OIDMatcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.junit.Test;

import hk.tux.OIDMatcher.OIDCharTreeMatcher;
import hk.tux.OIDMatcher.OIDMatcher;
import hk.tux.OIDMatcher.OIDPrefixTreeMatcher;
import hk.tux.OIDMatcher.OIDRegexMatcher;
import hk.tux.OIDMatcher.OIDStringMatcher;

public class OIDMatcherTest {

  @Test
  public void test() {
    InputStream in = this.getClass().getClassLoader().getResourceAsStream("prefixes.yaml");
    OIDRegexMatcher regexMatcher = new OIDRegexMatcher(in);
    this.testSingleMatcher(regexMatcher);
    
    in = this.getClass().getClassLoader().getResourceAsStream("prefixes.yaml");
    OIDCharTreeMatcher charTreeMatcher = new OIDCharTreeMatcher(in);
    this.testSingleMatcher(charTreeMatcher);
    
    in = this.getClass().getClassLoader().getResourceAsStream("prefixes.yaml");
    OIDPrefixTreeMatcher prefixTreeMatcher = new OIDPrefixTreeMatcher(in);
    this.testSingleMatcher(prefixTreeMatcher);
    
    in = this.getClass().getClassLoader().getResourceAsStream("prefixes.yaml");
    OIDStringMatcher stringMatcher = new OIDStringMatcher(in);
    this.testSingleMatcher(stringMatcher);
    
    
    InputStream inFail = this.getClass().getClassLoader().getResourceAsStream("prefixesFail.yaml");
    OIDPrefixTreeMatcher matcherFail = new OIDPrefixTreeMatcher(inFail);
    assertEquals(stringMatcher.getClass() + " should able to skip invalid data", 6, matcherFail.getFilterSize());
  }

  public void testSingleMatcher(OIDMatcher matcher) {
    assertEquals(matcher.getClass() + " is fail to initialize", 6, matcher.getFilterSize());

    final String shouldMatch = ".1.3.6.1.2.1.14.16.2.2.5.6";
    final String shouldMatchEqual = ".1.3.6.1.4.1.9.9.117.2";
    final String shouldNotMatch = ".1.3.6.1.4.1.9.9.117";
    final String shouldNotMatchPrefixWithoutSeparator = ".1.3.6.1.4.1.9.9.117.20";
    final String shouldNotMatchWithoutDot = "1.3.6.1.2.1.14.16.2.2.5.6";
    final String errorData = ".1.a.6.3.6.3.1.1.5.1";

    assertTrue(matcher.getClass() + " should match " + shouldMatch, matcher.matchPrefix(shouldMatch));
    assertTrue(matcher.getClass() + " should match " + shouldMatchEqual, matcher.matchPrefix(shouldMatchEqual));
    assertFalse(matcher.getClass() + " should fail to match " + shouldNotMatch, matcher.matchPrefix(shouldNotMatch));
    assertFalse(matcher.getClass() + " should fail to match " + shouldNotMatchPrefixWithoutSeparator,
        matcher.matchPrefix(shouldNotMatchPrefixWithoutSeparator));

    assertFalse(matcher.getClass() + " should not match (dot sensitive)" + shouldNotMatchWithoutDot,
        matcher.matchPrefix(shouldNotMatchWithoutDot));

    assertFalse(matcher.getClass() + " fail to handle error data " + errorData, matcher.matchPrefix(errorData));

    matcher.dumpTree();
  }
}
