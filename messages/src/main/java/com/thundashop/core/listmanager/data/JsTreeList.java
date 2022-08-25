package com.thundashop.core.listmanager.data;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.GetShopRemoteObject;
import java.util.ArrayList;
import java.util.List;

@GetShopRemoteObject
public class JsTreeList extends DataCommon {
    public String treeName = "";
    public List<TreeNode> nodes = new ArrayList<>();

    public TreeNode getNode(String nodeId) {
        for (TreeNode node : nodes) {
            if (node.id.equals(nodeId))
                return node;
            
            TreeNode subNode = node.getNode(nodeId);
            if (subNode != null)
                return subNode;
        }
        
        return null;
    }
}
