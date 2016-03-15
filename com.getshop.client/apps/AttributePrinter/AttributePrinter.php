<?php
namespace ns_50dc8615_10d3_4ed9_8cdf_09858bd76015;

class AttributePrinter  extends \ns_e4e2508c_acf8_4064_9e94_93744881ff00\ProductCommon implements \Application {
    var $list = "";
    public function getDescription() {
        
    }

    public function getName() {
        return "AttributePrinter";
    }

    public function render() {
        $this->includefile("attributes");
    }

    public function getAttributeList() {
        if(!$this->list) {
            $this->list = $this->getApi()->getListManager()->getJsTree("attributes");
        }
        return $this->list;
    }
    
    /**
     * @return \core_listmanager_data_TreeNode
     */
    public function getAttr($id) {
        $list = $this->getAttributeList()->nodes[0]->children;
        $node = $this->findNode($list, $id);
        return $node;
    }

    /**
     * 
     * @param \core_listmanager_data_TreeNode[] $list
     * @param type $id
     */
    public function findNode($list, $id) {
        $nodeFound = "";
        foreach($list as $node) {
            if($node->id == $id) {
                $nodeFound = $node;
            } else if(sizeof($node->children) > 0) {
                $nodeFound = $this->findNode($node->children, $id);
            }
        }
        return $nodeFound;
    }

}
?>
