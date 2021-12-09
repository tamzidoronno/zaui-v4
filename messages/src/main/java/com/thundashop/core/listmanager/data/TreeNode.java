
package com.thundashop.core.listmanager.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TreeNode implements Serializable {
    public String id;
    public String text;
    public String parent;
    public String type;
    public List<TreeNode> children = new ArrayList<>();

    public TreeNode getNode(String nodeId) {
        if (id.equals(nodeId))
            return this;
        
        for (TreeNode node : children) {
            TreeNode subNode = node.getNode(nodeId);
            if (subNode != null)
                return subNode;
        }
        
        return null;
    }
}
