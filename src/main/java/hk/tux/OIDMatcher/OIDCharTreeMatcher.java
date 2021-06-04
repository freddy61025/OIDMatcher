package hk.tux.OIDMatcher;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * It simply regard everything as char and build a prefix tree for searching
 * 
 * @author freddyc
 *
 */
public class OIDCharTreeMatcher extends OIDMatcher {
  private static final Logger LOG = LoggerFactory.getLogger(OIDCharTreeMatcher.class);

  private TreeNode root;

  public OIDCharTreeMatcher(InputStream in) {
    List<String> prefixes = this.parseYaml(in);
    // very important to build tree, don't remove !!!
    Collections.reverse(prefixes);
    buildTree(prefixes);
    try {
      in.close();
    } catch (Exception ex) {
      LOG.warn("FAIL to close input stream.");
    }
  }

  class TreeNode {
    TreeNode[] arr;
    boolean isEnd = false;

    public TreeNode() {
      // we only store [0-9.]
      this.arr = new TreeNode[11];
    }
  }

  /**
   * Inserts oid into the tree
   * 
   * @param word
   */
  private void insert(String oid) {
    TreeNode currentNode = root;
    for (int i = 0; i < oid.length(); i++) {
      char c = oid.charAt(i);
      int index = convertCharToIdx(c);
      if (currentNode.isEnd && oid.length() == i + 1) {
        break;
      }
      if (currentNode.arr[index] == null) {
        TreeNode tempNode = new TreeNode();
        currentNode.arr[index] = tempNode;
        currentNode = tempNode;
      } else {
        currentNode = currentNode.arr[index];
      }
    }
    currentNode.isEnd = true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean matchPrefix(String target) {
    TreeNode node = searchLastPrefixNode(target);
    return (node != null && node.isEnd);
  }

  /**
   * convert char to array index by finding the distance between char 0 - 9 and
   * hard code . > 11
   * 
   * @param c
   * @return index
   */
  private int convertCharToIdx(char c) {
    int index = c - '0';
    if (c == '.') {
      index = 10;
    }
    return index;
  }

  /**
   * convert index back to char for display use
   * 
   * @param idx
   * @return
   */
  private char convertIdxToChar(int idx) {
    if (idx == 10)
      return '.';
    else
      return (char) ('0' + idx);
  }

  /**
   * Find the last node of in the prefix tree
   * 
   * @param s
   * @return the last TreeNode
   */
  public TreeNode searchLastPrefixNode(String s) {
    TreeNode p = root;
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      int index = convertCharToIdx(c);
      if (p.isEnd && c == '.') {
        return p;
      }
      if (index < p.arr.length && p.arr[index] != null) {
        p = p.arr[index];
      } else {
        return null;
      }
    }

    if (p == root)
      return null;

    return p;
  }

  private void buildTree(List<String> prefixes) {
    root = new TreeNode();
    prefixes.forEach((prefix) -> {
      if (!OID_PATTERN.matcher(prefix).matches())
        return;
      this.insert(prefix);
    });
  }

  /**
   * DEBUG ONLY: dump tree info
   */
  public void dumpTree() {
    this.dumpTree(root, "");
  }

  /**
   * DEBUG ONLY: dump tree info
   *
   * @param node
   *          (starting node to print)
   * @param indent
   *          (make it easier to read, first time you can pass empty string)
   */
  private void dumpTree(TreeNode node, String indent) {
    if (node.arr == null)
      return;
    for (int i = 0; i < node.arr.length; i++) {
      TreeNode nextNode = node.arr[i];
      if (nextNode != null && LOG.isDebugEnabled()) {
        LOG.debug(indent + this.convertIdxToChar(i) + ((nextNode.isEnd) ? "<" : ""));
        this.dumpTree(nextNode, indent + this.convertIdxToChar(i));
      }
    }
  }
}