package hk.tux.OIDMatcher;

import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A very simple matcher using startWith
 * @author freddyc
 *
 */
public class OIDStringMatcher extends OIDMatcher{
  private static final Logger LOG = LoggerFactory.getLogger(OIDStringMatcher.class);
  private List<String> prefixes;

  public OIDStringMatcher(InputStream in) {
    prefixes = this.parseYaml(in);

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
    for(String prefix: prefixes) {
      if(oid.startsWith(prefix+".") || oid.equals(prefix))
        return true;
    }
    return false;
    // not use stream due to poor performance
    // return prefixes.stream().anyMatch(prefix -> oid.startsWith(prefix+".") || oid.equals(prefix));
  }

  @Override
  public void dumpTree() {
    prefixes.forEach(prefix->{LOG.debug(prefix);});
  }
}