<?php
namespace ns_1ee40ca9_27ac_4b97_944a_0eb02506d5e9;

class ProductCategories extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ProductCategories";
    }

    public function render() {
        $this->includefile("categorylist");
    }
        
    public function addCategory() {
        $category = new \core_productmanager_data_ProductCategory();
        $category->name = $_POST['categoryname'];
        $this->getApi()->getProductManager()->saveCategory($category);
    }
    
    public function setFilter() {
        $pageId = $this->getPage()->javapage->id;
        $_SESSION['categoryfilter'][$pageId] = $_POST['data']['id'];
    }
    
    public function isThisFilter($id) {
        $pageId = $this->getPage()->javapage->id;
        if(isset($_SESSION['categoryfilter'][$pageId])) {
            if($_SESSION['categoryfilter'][$pageId] == $id) {
                return true;
            }
        }
        return false;
    }
    
    public function isFilterActive() {
        $pageId = $this->getPage()->javapage->id;
        if(isset($_SESSION['categoryfilter'][$pageId])) {
            return $_SESSION['categoryfilter'][$pageId];
        }
        return false;
        
    }
    
    public function deleteCategory() {
        $this->getApi()->getProductManager()->deleteCategory($_POST['value']);
    }

    /**
     * @param \core_productmanager_data_Product[] $products
     */
    public function filterProducts($products) {
        if(!$this->isFilterActive()) {
            return $products;
        }
        
        $curFilter = $this->isFilterActive();
        $nodesSelected = $this->getAllSelectedNodes();
        $res = array();
        foreach($products as $prod) {
            if($this->hasCategory($nodesSelected, $prod->categories)) {
                $res[] = $prod;
            }
        }
        return $res;
    }

    /**
     * @param \core_listmanager_data_TreeNode[] $nodes
     */
    public function printCategoryList($nodes, $level) {
        echo "<ul>";
        if($level == 0) {
            echo "<li method='setFilter' gstype='clicksubmit' gsname='id' gsvalue=''>".$this->__w("All products")."</li>";
        }
        foreach($nodes as $node) {
            $selected = "";
            if($node->id == $this->isFilterActive()) {
                $selected = "selected";
            }
            
            echo "<li><span class='attrselect $selected' method='setFilter' gstype='clicksubmit' gsname='id' gsvalue='".$node->id."'>" . $node->text . "</span>";
            if(sizeof($node->children) > 0) {
                $this->printCategoryList($node->children, $level+1);
            }
            echo "</li>";
        }
        echo "</ul>";
    }

    public function getAllSelectedNodes() {
        $filter = $this->getApi()->getListManager()->getJsTree("categories");
        $ids = $this->findNodes($filter->nodes, false, 0);
        print_r($ids);
        return $ids;
    }

    /**
     * 
     * @param \core_listmanager_data_TreeNode[] $nodes
     * @param type $param1
     * @param type $param2 
     */
    public function findNodes($nodes, $found, $level) {
        $id = $this->isFilterActive();

        $ids = array();
        foreach($nodes as $node) {
            if($node->id == $id) {
                $found = true;
            }
            if($found) {
                $ids[] = $node->id;
            }
            if(sizeof($node->children) > 0) {
                $ids = array_merge($ids, $this->findNodes($node->children, $found, $level+1));
            }
        }
        return $ids;
    }
    
    public function hasCategory($nodesSelected, $categoryOnProduct) {
        foreach($nodesSelected as $node) {
            if(in_array($node, $categoryOnProduct)) {
                return true;
            }
        }
        return false;
    }

}
?>
