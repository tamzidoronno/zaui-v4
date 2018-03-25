<?php
namespace ns_3480a8d0_955e_4ad8_8c51_0eb88997fe6b;

class SrsFoodList extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SrsFoodList";
    }

    public function render() {
        if (isset($_POST['event']) && $_POST['event'] == "showModal") {
            $this->cacheProductList();
            $this->resetSessionForTable();
        }

        echo "<div class='foodlist'>";
        $this->includefile("foodlist");
        echo "</div>";

        echo "<div class='summary'>";
        $this->includefile("summary");
        echo "</div>";
    }
    
    public function addProductToTable() {
        $tableId = $this->getModalVariable("tableid");
        $cartItem = new \core_resturantmanager_ResturantCartItem();
        $cartItem->id = uniqid();
        $cartItem->productId = $_POST['data']['productid'];
        $cartItem->tablePersonNumber = $_POST['data']['personnumber'];
        
        $cartItemsForTable = $this->getItems();
        $cartItemsForTable[] = $cartItem;
        $_SESSION['SrsFoodList_tablelist_'.$tableId] = $cartItemsForTable;
        $this->includefile("summary");
        die();
    }

    public function getCurrentPersonNumber() {
        return $_SESSION['SrsFoodList_currentpersonnumber'];
    }

    /**
     * 
     * @return \core_resturantmanager_ResturantCartItem[]
     */
    public function getItemsForCurrentPerson() {
        $items = $this->getItems();
        $person = $this->getCurrentPersonNumber();
        $ret = array();
        
        foreach ($items as $item) {
            if ($item->tablePersonNumber != $this->getCurrentPersonNumber()) {
                continue;
            }
            
            $ret[] = $item;
        }
        
        return $ret;
    }
    
    /**
     * 
     * @return \core_resturantmanager_ResturantCartItem[]
     */
    public function getItems() {
        $tableid = $this->getModalVariable("tableid");
        if (isset($_SESSION['SrsFoodList_tablelist_'.$tableid])) {
            return $_SESSION['SrsFoodList_tablelist_'.$tableid];
        } else {
            return array();
        }
    }

    public function getProductList() {
        return $_SESSION['SrsFoodList_cachedlist_productlist'];
    }

    public function cacheProductList() {
        $products = $this->getApi()->getProductManager()->getAllProducts();
        foreach ($products as $product) {
            $_SESSION['SrsFoodList_cachedlist_'.$product->id] = $product;
        }
        $_SESSION['SrsFoodList_cachedlist_productlist'] = $this->getApi()->getProductManager()->getProductLists();
    }
    
    public function getProduct($productId) {
        return $_SESSION['SrsFoodList_cachedlist_'.$productId];
    }

    public function resetSessionForTable() {
        $tableId = $this->getModalVariable("tableid");
        $data = $this->getApi()->getResturantManager()->getCurrentTableData($tableId);
        $_SESSION['SrsFoodList_tablelist_'.$tableId] = $data->cartItems;
        $_SESSION['SrsFoodList_currentpersonnumber'] = 0;
    }
    
    public function decrementPerson() {
        $number = $this->getCurrentPersonNumber();
        $number--;
        if ($number < 0) {
            $number = 0;
        }
        $_SESSION['SrsFoodList_currentpersonnumber'] = $number;
        $this->showListAndSummary();
        die();
    }
    
    public function incrementPerson() {
        $number = $this->getCurrentPersonNumber();
        $number++;
        $_SESSION['SrsFoodList_currentpersonnumber'] = $number;
        $this->showListAndSummary();
        die();
    }

    public function showListAndSummary() {
        echo "<div class='srs_product_list'>";
        $this->includefile("foodlist");
        echo "</div>";
        echo "<div class='srs_summary_list'>";
        $this->includefile("summary");
        echo "</div>";
    }
    
    public function saveCurrentTable() {
        $tableId = $this->getModalVariable("tableid");
        $this->getApi()->getResturantManager()->addCartItems($this->getItems(), $tableId);
    }

}
?>
