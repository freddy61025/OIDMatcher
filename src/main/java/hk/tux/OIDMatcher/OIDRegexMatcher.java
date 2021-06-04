package hk.tux.OIDMatcher;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * very simple matcher using regex
 * @author freddyc
 *
 */
public class OIDRegexMatcher extends OIDMatcher {
  private static final Logger LOG = LoggerFactory.getLogger(OIDRegexMatcher.class);
  private List<String> prefixes;
  private Pattern filterPattern;

  public OIDRegexMatcher(InputStream in) {
    prefixes = this.parseYaml(in);
    StringBuffer sb = new StringBuffer();
    Iterator<String> ite = prefixes.iterator();
    sb.append("(");

    while (ite.hasNext()) {
      String tmp = ite.next();
      if (!OID_PATTERN.matcher(tmp).matches())
        return;
      String expression = tmp.replaceAll("\\.", "\\\\.");
      sb.append("^");
      sb.append(expression);
      sb.append("$|^");
      sb.append(expression);
      sb.append("\\..*");
      if (ite.hasNext()) {
        sb.append("|");
      }
    }
    sb.append(")");
    filterPattern = Pattern.compile(sb.toString());
    try {
      in.close();
    } catch (Exception ex) {
      LOG.warn("FAIL to close input stream.");
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean matchPrefix(String oid) {
    return filterPattern.matcher(oid).matches();
  }

  @Override
  public void dumpTree() {
    LOG.debug(filterPattern.toString());
  }
}