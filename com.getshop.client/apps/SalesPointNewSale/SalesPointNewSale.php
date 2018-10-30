<?php
namespace ns_57db782b_5fe7_478f_956a_ab9eb3575855;

class SalesPointNewSale extends SalesPointCommon implements \Application {
    
    public function getDescription() {
        
    }

    public function getName() {
        return "SalesPointNewSale";
    }

    public function render() {
        if ($this->preRender()) {
            return;
        }
        
        $this->includefile("taboperationoverlay");
        
        echo "<div class='leftmenu'>";
            $this->includefile("leftmenu");
        echo "</div>";
        
        echo "<div class='productlistouter'>";
            echo "<div class='productlistinner'>";
                $this->includefile("listview");
            echo "</div>";
            echo "<div class='multitaxsupport'></div>";
        echo "</div>";
        
        echo "<div class='rightmenu'>";
            $this->includefile("tab");
        echo "</div>";
        
    }

    /**
     * 
     * @param type $products
     * @param type $productId
     * @return \core_productmanager_data_Product
     */
    public function getProduct($products, $productId) {
        foreach ($products as $product) {
            if ($product->id == $productId) {
                return $product;
            }
        }
        
        return null;
    }

    public function getCurrentTab() {
        if (!isset($_SESSION['ns_57db782b_5fe7_478f_956a_ab9eb3575855_tabid'])) {
            return null;
        }
        
        return $this->getApi()->getPosManager()->getTab($_SESSION['ns_57db782b_5fe7_478f_956a_ab9eb3575855_tabid']);
    }
    
    public function showTabOperationContent() {
        if ($_POST['data']['action'] == "newtab")
            $this->includefile("createnewtab");
        
        if ($_POST['data']['action'] == "listtab")
            $this->includefile("tablist");
    }
    
    public function createNewTab() {
        $referenceName = isset($_POST['data']['tabreferencename']) ? $_POST['data']['tabreferencename'] : "";
        $tabId = $this->getApi()->getPosManager()->createNewTab($referenceName);
        
        $_SESSION['ns_57db782b_5fe7_478f_956a_ab9eb3575855_tabid'] = $tabId;
    }
    
    public function activateTab() {
        $_SESSION['ns_57db782b_5fe7_478f_956a_ab9eb3575855_tabid'] = $_POST['data']['tabid'];
    }
    
    public function addProductToCurrentTabWithTaxCode() {
        $product = $this->getApi()->getProductManager()->getProduct($_POST['data']['productid']);
        $tab = $this->getCurrentTab();
        if (!$tab) {
            $this->createNewTab();
            $tab = $this->getCurrentTab();
        }
        
        $product = $this->getApi()->getProductManager()->changeTaxCode($product, $_POST['data']['taxgroupid']);
        $cartItem = new \core_cartmanager_data_CartItem();
        $cartItem->count = 1;
        $cartItem->product = $product;
        $this->getApi()->getPosManager()->addToTab($tab->id, $cartItem);
        $this->includefile("tab");
        die();
    }
    
    public function addProductToCurrentTab() {
        $product = $this->getApi()->getProductManager()->getProduct($_POST['data']['productid']);
        
        if ($product->additionalTaxGroupObjects && count($product->additionalTaxGroupObjects)) {
            echo "multitaxessupport";
            die();
        }
        
        $tab = $this->getCurrentTab();
        if (!$tab) {
            $this->createNewTab();
            $tab = $this->getCurrentTab();
        }
        
        $cartItem = new \core_cartmanager_data_CartItem();
        $cartItem->count = 1;
        $cartItem->product = $product;
        $this->getApi()->getPosManager()->addToTab($tab->id, $cartItem);
        $this->includefile("tab");
        die();
    }
    
    public function getCurrentTabId() {
        echo $_SESSION['ns_57db782b_5fe7_478f_956a_ab9eb3575855_tabid'];
        die();
    }
    
    public function changeCount() {
        $tab = $this->getCurrentTab();
        foreach ($tab->cartItems as $item) {
            if ($item->cartItemId == $_POST['data']['cartitemid']) {
                $item->count = $_POST['data']['count'];
                $this->getApi()->getPosManager()->addToTab($tab->id, $item);
                echo $_POST['data']['count'].";".$this->getApi()->getPosManager()->getTotal($tab->id);
                die();
            }
        }
    }
    
    public function changePrice() {
        $tab = $this->getCurrentTab();
        foreach ($tab->cartItems as $item) {
            if ($item->cartItemId == $_POST['data']['cartitemid']) {
                $item->product->price = $_POST['data']['price'];
                $this->getApi()->getPosManager()->addToTab($tab->id, $item);
                echo $_POST['data']['price'].";".$this->getApi()->getPosManager()->getTotal($tab->id);
                die();
            }
        }
    }
    
    public function removeItemFromTab() {
        $tab = $this->getCurrentTab();
        $this->getApi()->getPosManager()->removeFromTab($_POST['data']['cartitemid'], $tab->id);
        echo $this->getApi()->getPosManager()->getTotal($tab->id);
        die();
    }
    
    public function deleteCurrentTab() {
        $tab = $this->getCurrentTab();
        if ($tab) {
            $this->getApi()->getPosManager()->deleteTab($tab->id);
            unset($_SESSION['ns_57db782b_5fe7_478f_956a_ab9eb3575855_tabid']);
        }
    }
    
    public function printCurrentTab() {
        $tabPayment = new \ns_11234b3f_452e_42ce_ab52_88426fc48f8d\SalesPointTabPayment();
        $tab = $this->getCurrentTab();
        $gdsDevice = $tabPayment->getCurrentGdsDevice();
        $this->getApi()->getPosManager()->printOverview($tab->id, $gdsDevice->id);
    }
    
    
    public function movelist() {
        $this->getApi()->getPosManager()->moveList($this->getSelectedCashPointId(), $_POST['data']['listid'], $_POST['data']['type'] === "up");
    }
    
    public function showMultiTaxes() {
        $this->includefile("multitaxselection");
    }
}
?>