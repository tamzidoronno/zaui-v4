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
        
    public function renderConfig() {
        $this->includefile("categoryconfig");
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
        $res = array();
        foreach($products as $prod) {
            if($prod->category == $curFilter) {
                $res[] = $prod;
            }
        }
        return $res;
    }

}
?>
