<?php

namespace ns_f245b8ae_f3ba_454e_beb4_ecff5ec328d6;

class ProductLists extends \ApplicationBase implements \Application {
    var $currentProduct;
    
    public function getDescription() {
        return $this->__w("Display product lists in your webshop");
    }

    public function getName() {
        return $this->__w("Product List");
    }
    
    private function showLists() {
        if (!$this->getConfigurationSetting("productlist") && !$this->isSearchResultList()) {
            if ($this->isEditorMode()) {
                $this->includefile("showLists");
            }
            return;
        } 
        
        $productList = $this->getApi()->getProductManager()->getProductList($this->getConfigurationSetting("productlist"));
        if (!$productList && !$this->isSearchResultList()) {
            if ($this->isEditorMode()) {
                $this->includefile("showLists");
            }
            
            return;
        }
        
        $this->showProducts();
    }
    
    public function showProducts($showProductIds=false, $columnSize=false, $showRemoveButton = false) {
        $this->showProductIds = $showProductIds;
        $this->showColumnSize = $columnSize;
        $this->showRemoveButton = $showRemoveButton;
        if ($this->isSlideView()) {
            $this->includefile("slideView");
        } else {
            $this->includefile("showProducts");
        }
        
    }
    
    private function isSlideView() {
        return $this->getConfigurationSetting("isSlideView") == "true";
    }
    
    public function showRemoveButton() {
        return $this->showRemoveButton;
    }
    
    public function setSlideView() {
        $this->setConfigurationSetting("isSlideView", "true");
    }
    
    public function toggleDecimals() {
        $display = $this->getDisplayDecimals();
        if($display) {
            $this->setConfigurationSetting("hidedecimals", "true");
        } else {
            $this->setConfigurationSetting("hidedecimals", "false");
        }
    }
    
    public function getAllProducts() {
        if ($this->isSearchResultList()) {
            $searchWord = isset($_GET['searchWord']) ? $_GET['searchWord'] : "";
            $searchWord = isset($_POST['data']['searchWord']) ? $_POST['data']['searchWord'] : $searchWord;
            $products = $this->getApi()->getProductManager()->search($searchWord, 50, 1);
            $products = $products->products;
        } else {
            if ($this->showProductIds) {
                $criteria = new \core_productmanager_data_ProductCriteria();
                $criteria->ids = $this->showProductIds;    
            } else {
                $listId = $this->getConfigurationSetting("productlist");
                $productList = $this->getApi()->getProductManager()->getProductList($listId);
                $criteria = new \core_productmanager_data_ProductCriteria();
                $criteria->ids = $productList->productIds;    
            }
            
            $products = $this->getApi()->getProductManager()->getProducts($criteria);
        }
        
        
        $catFilter = new \ns_1ee40ca9_27ac_4b97_944a_0eb02506d5e9\ProductCategories();
        $products = $catFilter->filterProducts($products);
        
        return $products;
    }

    public function render() {
        $this->showLists();
        
        $settings = $this->getFactory()->getApplicationPool()->getApplicationSetting("d755efca-9e02-4e88-92c2-37a3413f3f41");
        $settingsInstance = $this->getFactory()->getApplicationPool()->createInstace($settings);
        echo "<script>";
        if($settingsInstance->getConfigurationSetting("autonavigatetocart") == "true") {
            echo "autonavigatetocart=true;";
        } else {
            echo "autonavigatetocart=false;";
        }
        echo "</script>";
    }
    
    public function renderConfig() {
        $this->includefile("overview");
    }

    public function createList() {
        $this->getApi()->getProductManager()->createProductList($_POST['name']);
    }
    
    public function setList() {
        $this->setConfigurationSetting("productlist", $_POST['data']);
    }
    
    public function getProductList($id) {
        return $this->getApi()->getProductManager()->getProductList($id);
    }
    
    public function search() {
        
    }
    
    public function removeFromProductList() {
        $listId = $_POST['listId'];
        $productId = $_POST['productId'];
        
        $list = $this->getApi()->getProductManager()->getProductList($listId);
        $keep = array();
        foreach ($list->productIds as $id) {
            if ($id != $productId) {
                $keep[] = $id;
            }
        }
        
        $list->productIds = $keep;
        $this->getApi()->getProductManager()->saveProductList($list);    
    }

    public function addProductToList() {
        $listId = $_POST['listId'];
        $productId = $_POST['productId'];
        $list = $this->getApi()->getProductManager()->getProductList($listId);
        if (!in_array($productId, $list->productIds)) {
            $list->productIds[] = $productId;
            $this->getApi()->getProductManager()->saveProductList($list);
        }
    }
    
    public function getProductsFromList($productList) {
        $search = new \core_productmanager_data_ProductCriteria();
        $search->ids = $productList->productIds;
        $products = $this->getApi()->getProductManager()->getProducts($search);
        return $products;
    }
    
    public function searchForProducts() {
        if (!isset($_POST['searchword']) || $_POST['searchword'] == "") {
            return $this->getApi()->getProductManager()->getLatestProducts(10);
        }
        
        $search = new \core_productmanager_data_ProductCriteria();
        $search->search = $_POST['searchword'];
        $products = $this->getApi()->getProductManager()->getProducts($search);
        return $products;
    }
    
    public function addProductToCart() {
        $productId = $_POST['data']['productId'];
        $this->getApi()->getCartManager()->addProduct($productId, 1, []);
    }
    
    public function isRowView() {
        return $this->getColumnSize() == 1;
    }

    public function isGridView() {
        return $this->getColumnSize() > 1;
    }
    
    public function getColumnSize() {
        if (isset($this->showColumnSize) && $this->showColumnSize) {
            return $this->showColumnSize;
        }
        
        $value = $this->getConfigurationSetting("column_size");
        if (!$value) {
            return 1;
        }
        return $value;
    }
    
    public function setColumns() {
        $this->setConfigurationSetting("isSlideView", "false");
        $this->setConfigurationSetting("column_size", $_POST['data']);
    }

    public function setAsSearchResultList() {
        $this->setConfigurationSetting("searchResultList", "true");
    }
    
    public function isSearchResultList() {
        return $this->getConfigurationSetting("searchResultList") == "true";
    }

    public function deleteList() {
        $this->getApi()->getProductManager()->deleteProductList($_POST['value']);
    }

    public function getDisplayDecimals() {
        if(!$this->getConfigurationSetting("hidedecimals")) {
            return true;
        }
        
        return $this->getConfigurationSetting("hidedecimals") != "true";
    }

    public function setCurrentProduct($product) {
        $this->currentProduct = $product;
    }
    
    /**
     * @return \core_productmanager_data_Product
     */
    public function getCurrentProduct() {
        return $this->currentProduct;
    }

    public function getProductTemplate() {
        $template = "defaultproductbox";
        $key = $this->getConfigurationSetting("productlisttemplate");
        if ($this->getCurrentProduct()->isGroupedProduct) {
            return "groupedproduct";
        }
        if($key) {
            $template = "productemplate_" . $key;
        }
        return $template;
    }
    
    public function getDefaultImage($product) {
        $mainImage = false;
        if ($product->mainImage && in_array($product->mainImage, $product->imagesAdded)) {
            $mainImage = $product->mainImage;
        }

        if (!$mainImage && count($product->imagesAdded)) {
            $mainImage = $product->imagesAdded[0];
        }
        return $mainImage;
    }
    
    public function getPriceForGroupedProduct($render=true) {
        if ($render) 
            $product = $this->getApi()->getProductManager()->getProduct($_POST['data']['productid']);
        else 
            $product = $this->getCurrentProduct();
        
        $price = 0;
        
        foreach ($product->subProducts as $subProduct) {
            if (@$_POST['data'][$subProduct->id]) {
                $quantity = (int)$_POST['data'][$subProduct->id];
                $price += $subProduct->price * $quantity;
            }
        }
        
        if($this->getDisplayDecimals()) {
            $price = \ns_9de54ce1_f7a0_4729_b128_b062dc70dcce\ECommerceSettings::formatPrice($price, 2, true);
        } else {
            $price = \ns_9de54ce1_f7a0_4729_b128_b062dc70dcce\ECommerceSettings::formatPrice($price, 0, true);
        }
        
        
        if ($render) {
            echo $price;
            die();
        } else {
            return $price;
        }
    }
    
    public function purchaseGroupedProduct() {
        $this->getApi()->getCartManager()->clear();
        foreach ($_POST['data']['products'] as $product) {
            $qty = $product['qty'] ? $product['qty'] : 0;
            $productId = $product['productId'];
            $this->getApi()->getCartManager()->addProduct($productId, $qty, []);
        }
        
        $loggedInUser = \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject();
        
        if ($loggedInUser) {
            $address = $loggedInUser->address;
            $address->fullName = $loggedInUser->fullName;
            $this->getApi()->getCartManager()->setAddress($address);
            $order = $this->getApi()->getOrderManager()->createOrder($address);
            $order->userId = $loggedInUser->id;
            $this->getApi()->getOrderManager()->saveOrder($order);
        }
        
        echo $order->id;
    }

}
?>