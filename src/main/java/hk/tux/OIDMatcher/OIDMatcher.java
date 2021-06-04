package hk.tux.OIDMatcher;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public abstract class OIDMatcher {
  protected static final Logger LOG = LoggerFactory.getLogger(OIDMatcher.class);
  protected static final Pattern OID_PATTERN = Pattern.compile("^\\.*((0)|([1-9][0-9]*))((\\.0)|(\\.[1-9][0-9]*))*$");
  protected static final String PREFIX_TAG = "trap-type-oid-prefix";
  private int filterSize = 0;

  abstract public void dumpTree();

  /**
   * check is target against build in prefix list
   * 
   * @param target
   * @return boolean
   */
  abstract public boolean matchPrefix(String target);

  /**
   * read input stream, get and validate the prefix lists
   * @param in
   * @return filter list
   */
  protected List<String> parseYaml(InputStream in) {
    if (in == null) {
      return null;
    }
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    // do not use HashMap<String, List<String>> for the possibility adding new
    // keys in the yaml file later
    TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
    };
    try {
      HashMap<String, Object> map = mapper.readValue(in, typeRef);
      Object objects = map.get(PREFIX_TAG);
      if (objects != null && objects instanceof ArrayList) {
        this.filterSize = 0;
        ArrayList<?> objectsList = (ArrayList<?>) objects;
        ArrayList<String> prefixes = new ArrayList<>(objectsList.size());
        objectsList.forEach((object) -> {
          if (object != null && object instanceof String) {
            String prefix = ((String) object).trim();
            if (prefix.length() > 0 && OID_PATTERN.matcher(prefix).matches()) {
              this.filterSize++;
              prefixes.add(prefix);
            }
          }
        });
        return prefixes;
      }
    } catch (JsonParseException e) {
      LOG.warn("FAIL to parse yaml");
    } catch (JsonMappingException e) {
      LOG.warn("FAIL to yaml content");
    } catch (IOException e) {
      LOG.warn("FAIL to read yaml");
    }
    return null;
  }

  /**
   * it return number of filters parsed correctly
   * @return number of filters
   */
  public int getFilterSize() {
    return this.filterSize;
  }
}
