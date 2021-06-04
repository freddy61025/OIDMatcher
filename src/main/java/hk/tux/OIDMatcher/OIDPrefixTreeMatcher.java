package hk.tux.OIDMatcher;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * It simply regard everything as char and build a tree for
 * 
 * @author freddyc
 *
 */
public class OIDPrefixTreeMatcher extends OIDMatcher {
  private static final Logger LOG = LoggerFactory.getLogger(OIDPrefixTreeMatcher.class);
  private TreeNode root;

  public OIDPrefixTreeMatcher(InputStream in) {
    List<String> prefixes = this.parseYaml(in);
    buildTree(prefixes);

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
    TreeNode currentNode = root;
    int pos = 0;
    int end;
    oid = oid + ".";
    while ((end = oid.indexOf('.', pos)) >= 0) {
      String nodeId = oid.substring(pos, end);
      pos = end + 1;

      Map<String, TreeNode> children = currentNode.getChildren();
      if (children != null) {
        currentNode = children.get(nodeId);
        if (currentNode == null) {
          return false;
        } else if (currentNode.children == null) {
          return true;
        }
      }
    }
    return false;

  }

  private class TreeNode {
    private String id;
    private HashMap<String, TreeNode> children;
    private boolean isEnding;

    TreeNode(String id) {
      this.id = id;
    }

    String getId() {
      return id;
    }

    Map<String, TreeNode> getChildren() {
      return this.children;
    }

    /**
     * insert new layer to corrent node
     * 
     * @param nodeId
     * @param isEnding
     * @return childrenNode, if it return null it means no need to handle
     *         remaining oids
     */
    private TreeNode insertChildrenAndReturnNewCurrentNode(String newNodeId, boolean isEnding) {
      if (this.isEnding)
        return null;
      TreeNode newCurrentNode = null;
      if (this.getChildren() == null) {
        this.children = new HashMap<>();
        newCurrentNode = new TreeNode(newNodeId);
        newCurrentNode.isEnding = isEnding;
        this.children.put(newNodeId, newCurrentNode);
      } else {
        newCurrentNode = this.getChildren().get(newNodeId);
        if (newCurrentNode == null) {
          newCurrentNode = new TreeNode(newNodeId);
          newCurrentNode.isEnding = isEnding;
          this.children.put(newNodeId, newCurrentNode);
        } else if (isEnding) {
          newCurrentNode.children = null;
        }
      }
      return newCurrentNode;
    }
  }

  /**
   * build the prefix tree
   * 
   * @param prefixes
   */
  private synchronized void buildTree(List<String> prefixes) {
    root = new TreeNode("R");
    prefixes.forEach((prefix) -> {
      if (!OID_PATTERN.matcher(prefix).matches())
        return;
      TreeNode currentNode = root;
      int pos = 0;
      int end;
      prefix = prefix + ".";
      while ((end = prefix.indexOf('.', pos)) >= 0) {
        String nodeId = prefix.substring(pos, end);
        pos = end + 1;

        currentNode = currentNode.insertChildrenAndReturnNewCurrentNode(nodeId, (end + 1 == prefix.length()));
        if (currentNode == null)
          break;
      }
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
   *          ( make it easier to read, first time you can pass empty string)
   */
  private void dumpTree(TreeNode node, String indent) {
    if (node.getId() != null)
      LOG.debug(indent + node.getId() + ((node.isEnding) ? "<" : ""));
    if (node.getChildren() == null)
      return;
    for (TreeNode children : node.getChildren().values()) {
      if (children != null)
        dumpTree(children, indent + node.getId() + ".");
    }
  }
}