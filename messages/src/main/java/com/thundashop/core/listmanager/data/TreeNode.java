
package com.thundashop.core.listmanager.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TreeNode implements Serializable {
    public String id;
    public String text;
    public String parent;
    public List<TreeNode> children = new ArrayList();
}
