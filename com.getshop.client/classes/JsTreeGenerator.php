<?php
class JsTreeGenerator {
    public function JsTreeGenerator() {
        
    }
    
    public function generateTreeFromPostedData() {
        $tree = array();
        $tree = $this->generateNodeList($_POST['data']['entries']);
        return $tree;
    }

    public function generateNodeList($list) {
        $result = array();
        foreach($list as $entry) {
            $node = new core_listmanager_data_TreeNode();
            $node->text = $entry['text'];
            $node->id = $entry['id'];
            $node->children = array();
            if(isset($entry['children'])) {
                $node->children = $this->generateNodeList($entry['children']);
            }
            $result[] = $node;
        }
        return $result;
    }

    /**
     * @param core_listmanager_data_JsTreeList $list
     */
    public function printTree($list) {
        $nodes = $list->nodes;
        if(!$nodes) {
            echo "<ul><li>Tree</li></ul>";
        } else {
            $this->printNodes($nodes);
        }
    }

    /**
     * @param core_listmanager_data_TreeNode[] $nodes
     */
    public function printNodes($nodes) {
        echo "<ul>";
        foreach($nodes as $node) {
            echo "<li id='" . $node->id ."'>" . $node->text;
            if(sizeof($node->children) > 0) {
                $this->printNodes($node->children);
            }
            echo "</li>";
        }
        echo "</ul>";
    }

}
?>