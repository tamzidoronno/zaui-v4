<?php
namespace ns_57db782b_5fe7_478f_956a_ab9eb3575855;

class SalesPointNewSale extends SalesPointCommon implements \Application {
    public $errorMsg = "";
    public $userChanged = false;
    
    public function getDescription() {
        
    }

    public function getExtraOption($options, $optioid) {
        foreach ($options as $option) {
            if ($option->id == $optioid || $option->optionsubid == $optioid) {
                return $option;
            }
        }
        
        return null;
    }
    
    public function getName() {
        return "SalesPointNewSale";
    }

    public function render() {
        if ($this->preRender()) {
            return;
        }
        
        if ($this->userChanged) {
            echo "<script>document.location = document.location</script>";
            return;
        }
        
        $this->includefile("taboperationoverlay");
        
        echo "<div class='leftmenu'>";
            $this->includefile("leftmenu");
        echo "</div>";
        
        echo "<div style='display: inline-block; width: calc(100% - 100px)'>";
            $this->includefile("topmenu");
            echo "<div class='productlistouter'>";
                echo "<div class='productlistinner'>";
                    $this->renderActivatedView();
                echo "</div>";
                echo "<div class='multitaxsupport'></div>";
            echo "</div>";

            echo "<div class='rightmenu'>";
                $this->includefile("tab");
            echo "</div>";
        echo "</div>";
        
        $this->showPaymentIfInProgress();
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
    
    public function addProductToCurrentTab() {
        $product = $this->getApi()->getProductManager()->getProduct($_POST['data']['productid']);
        
        if (is_array($product->extras) && count($product->extras) && !isset($_POST['data']['extras'])) {
            echo "productconfig";
            die();
        }
        
        if (isset($_POST['data']['taxgroupid'])) {
            $product = $this->getApi()->getProductManager()->changeTaxCode($product, $_POST['data']['taxgroupid']);
        }
        
        $tab = $this->getCurrentTab();
        
        if (!$tab) {
            $this->createNewTab();
            $tab = $this->getCurrentTab();
        }
        
        $cartItem = new \core_cartmanager_data_CartItem();
        $cartItem->count = $_POST['data']['count'];
        $cartItem->product = $product;
        
        if (isset($_POST['data']['extras'])) {
            $cartItem->product->selectedExtras = array();
            foreach ($_POST['data']['extras'] as $arr) {
                $cartItem->product->selectedExtras[$arr['id']] = $arr['selectedVals'];
            }
        }
        
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
        $item = $this->getApi()->getPosManager()->setNewProductPrice($tab->id, $_POST['data']['cartitemid'], $_POST['data']['price']);
        echo $this->getPriceHtml($item).";".$this->getApi()->getPosManager()->getTotal($tab->id);
        die();
    }
    
    public function changeDiscount() {
        $tab = $this->getCurrentTab();
        $item = $this->getApi()->getPosManager()->setDiscountToCartItem($tab->id, $_POST['data']['cartitemid'], $_POST['data']['discountValue']);
        $priceToUse = $item->overridePriceIncTaxes ? $item->overridePriceIncTaxes : $item->product->price;
        echo $this->getPriceHtml($item).";".$this->getApi()->getPosManager()->getTotal($tab->id).";".$priceToUse;
        die();
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
        $tab = $this->getCurrentTab();
        $receiptPrinterId = $this->getSelectedReceiptPrinter();
        $this->getApi()->getPosManager()->printOverview($tab->id, $receiptPrinterId);
    }
    
    
    public function movelist() {
        $this->getApi()->getPosManager()->moveList($this->getSelectedViewId(), $_POST['data']['listid'], $_POST['data']['type'] === "up");
    }
    
    public function showMultiTaxes() {
        $this->includefile("multitaxselection");
    }
    
    public function showProductConfig() {
        $this->includefile("productconfig");
    }
    
    public function activateView() {
        if (isset($_SESSION['ns_57db782b_5fe7_478f_956a_ab9eb3575855_activeview']) && $_SESSION['ns_57db782b_5fe7_478f_956a_ab9eb3575855_activeview'] == $_POST['data']['view']) {
            unset($_SESSION['ns_57db782b_5fe7_478f_956a_ab9eb3575855_activeview']);
        } else {
            $_SESSION['ns_57db782b_5fe7_478f_956a_ab9eb3575855_activeview'] = $_POST['data']['view'];
        }
    }

    public function renderActivatedView() {
        if (!isset($_SESSION['ns_57db782b_5fe7_478f_956a_ab9eb3575855_activeview'])) {
            $this->includefile("listview");
            return;
        }
        
        if ($_SESSION['ns_57db782b_5fe7_478f_956a_ab9eb3575855_activeview'] == "cashwithdrawal") {
            $this->includefile("cashwithdrawal");
        } elseif ($_SESSION['ns_57db782b_5fe7_478f_956a_ab9eb3575855_activeview'] == "tables") {
            $this->includefile("tables");
        } elseif ($_SESSION['ns_57db782b_5fe7_478f_956a_ab9eb3575855_activeview'] == "changeuserview") {
            $this->includefile("changeuserview");
        } else {
            $this->includefile("listview");
        }
    }

    public function addCashWithdrawal() {
        $tab = $this->getCurrentTab();
        $this->getApi()->getPosManager()->addCashWithDrawalToTab($tab->id, $_POST['data']['amount']);
        $_POST['data']['view'] = "cashwithdrawal";
        $this->activateView();
    }
    
    public function selectTable() {
        $tabId = $this->getApi()->getPosManager()->getCurrentTabIdForTableId($_POST['data']['tableid']);
        $_POST['data']['tabid'] = $tabId;
        $_POST['data']['view'] = "tables";
        $this->activateView();
        $this->activateTab();
    }
    
    public function printCurrentToKitchen() {
        $tab = $this->getCurrentTab();
        $kitchenPrinterId = $this->getSelectedKitchenPrinter();
        $this->getApi()->getPosManager()->printKitchen($tab->id, $kitchenPrinterId);
    }
    
    public function getDistinctAdditionalTaxCodes($tab) {
        $cartItems = $tab->cartItems;
        $taxesToSelectBetween = new \stdClass();

        foreach ($cartItems as $item) {
            foreach ($item->product->additionalTaxGroupObjects as $taxGroup) {
                $taxesToSelectBetween->{$taxGroup->groupNumber} = $taxGroup;
            }
        }
        
        return $taxesToSelectBetween;
    }
    
    public function changeTabTax() {
        $tab = $this->getCurrentTab();
        if (!$tab)
            return;
        
        $this->getApi()->getPosManager()->changeTaxRate($tab->id, $_POST['data']['taxgroupnumber']);
    }
    
    public function cancelCurrentTaxSelection() {
        $tab = $this->getCurrentTab();
        if (!$tab)
            return;
        
        $this->getApi()->getPosManager()->changeTaxRate($tab->id, "");
    }

    public function getPriceHtml($item) {
        $priceToUse = $item->product->price;
        $oldPrice = "";

        if ($item->overridePriceIncTaxes) {
            $priceToUse = $item->overridePriceIncTaxes;
            $oldPrice = "<span class='oldprice'>".$item->product->price."</span>";
        }
        
        return $oldPrice.$priceToUse;
    }
    
    public function setTabDiscount() {
        $tab = $this->getCurrentTab();
        $this->getApi()->getPosManager()->setTabDiscount($tab->id, $_POST['data']['discount']);
    }
    
    public function changeUser() {
        $user = $this->getApi()->getUserManager()->changeUserByUsingPinCode($_POST['data']['userid'], $_POST['data']['value']);
        if (!$user) {
            $this->errorMsg = $this->__f("Access Denied"); 
            return;
        }
        
        $this->userChanged = true;
        \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::setLoggedOn($user);
    }

    public function addGiftCart() {
        $tab = $this->getCurrentTab();
        
        if (!$tab) {
            $this->createNewTab();
            $tab = $this->getCurrentTab();
        }
        
        $this->getApi()->getPosManager()->addGiftCardToTab($tab->id, $_POST['data']['value']);
    }
    
    public function showPaymentIfInProgress() {
        if (isset($_SESSION['ns_11234b3f_452e_42ce_ab52_88426fc48f8d_complete_payment']) && $_SESSION['ns_11234b3f_452e_42ce_ab52_88426fc48f8d_complete_payment']) {
            $_SESSION['gs_error_message_payment'] = $this->__f("Please complete the payment or cancel it");
            echo "<script>$('.startpaymentbutton').click();</script>";
        }
    }
}
?>