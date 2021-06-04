package hk.tux.OIDMatcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Main class for interactive query from STDIN
 * @author freddyc
 *
 */
public class OIDMatcherCmd {

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      System.out.println(
          "Please execute with yaml file path: java -jar OIDMatcher-1.0-SNAPSHOT.jar <YAML_FILENAME>");
      System.exit(-1);
    }
    File file = new File(args[0]);
    if (!(file.exists() && file.canRead())) {
      System.out.println("Fila to read file!");
    }
    System.out.println("CTRL-C / CTRL-D to exit!");
    FileInputStream in = new FileInputStream(file);
    OIDMatcher matcher = new OIDCharTreeMatcher(in);

    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    String query = null;

    while (true) {
      query = br.readLine();
      if(query == null) {
        System.exit(0);
      }
      if(!"".equals(query.trim()))
        System.out.println(query + ": " + matcher.matchPrefix(query.trim()));
    }
  }

}
