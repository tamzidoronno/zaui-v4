<?php
namespace ns_626ff5c4_60d4_4faf_ac2e_d0f21ffa9e87;

class Search extends \SystemApplication implements \Application{
    
    //put your code here
    public function getDescription() {
        return $this->__("");
    }
    
    public function getName() {
        return $this->__("Search");
    }
    
    public function postProcess() {
        
    }
    
    public function preProcess() {
        
    }
    
    public function searchForProducts() {
        $value = $_POST['data']['value'];
        $criteria =  $this->getApiObject()->core_productmanager_data_ProductCriteria();
        $criteria->search = $value;
        $result = (array)$this->getApi()->getProductManager()->getProducts($criteria);
        foreach($result as $product) {
            /* @var $product core_productmanager_data_Product */
            if(sizeof(trim($product->name)) == 0) {
                continue;
            }
            echo "<a href='?page=".$product->page->id."'>";
            echo "<div class='search_result'>";
            echo "<span style='float:right;'>" . $product->price .",-</span>";
            echo strip_tags($product->name);
            echo "</div>";
            echo "</a>";
        }
        
        if(sizeof($result) == 0) {
            echo $this->__f("No products where found.");
        }
    }
    
    public function render() {
        $this->includefile("searcharea");
    }
}

?>
