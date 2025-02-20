<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of Products
 *
 * @author ktonder
 */
namespace ns_e073a75a_87c9_4d92_a73a_bc54feb7317f;

class Products extends \WebshopApplication implements \Application {
    public function getDescription() {
        return $this->__w("Setup and manage your products");
    }

    public function getName() {
        return $this->__w("Products");
    }

    public function renderConfig() {
        $this->includefile("frontpage");
    }
    
    public function render() {
        
    }
    
    public function getDashboardChart($year=false) {       
        if ($year) {
            $this->createGoogleChartResultList($year);
        }
        
        return ['fa-shopping-cart', 'app.Products.drawChart', $this->__f("Products")];
    }
    
    private function createGoogleChartResultList($year) {
        $products = $this->getApi()->getOrderManager()->getMostSoldProducts(5);
        $productCount = array();
        
        foreach ($products as $productId => $list) {
            $product = $this->getApi()->getProductManager()->getProduct($productId);
            if (!$product) {
                continue;
            }
            
            if (!isset($productCount[$productId])) {
                $productCount[$productId] = array();
            }
            
            foreach ($list as $statistic) {
                if ($statistic->year != $year) {
                    continue;
                }
                
                $productCount[$productId][$statistic->month] = $statistic->count;
            }
            
            $productCount[$productId][13] = $product->name;
        }
        
        echo "<script>";
        echo "app.Products.googleChartsData = ".json_encode($productCount).";";
        echo "</script>";
        
    }

    public function updateProduct() {
        $product = $this->getApi()->getProductManager()->getProduct($_POST['productid']);
        $product->name = $_POST['title'];
        $product->shortDescription = $_POST['lisviewdescription'];
        $product->price = $_POST['price'];
        if(!\HelperCart::hasVariations($product)) {
            $product->sku = $_POST['sku'];
        }
        $product->taxgroup = $_POST['taxgroup'];
        $product->stockQuantity = $_POST['stockQuantity'];
        $product->progressivePriceModel = $_POST['isProgressive'];
        $product->dynamicPriceInPercent = $_POST['dynamicPriceInPercent'];
        $product->accountingSystemId = $_POST['accountingSystemId'];
        $product->discountedPrice = $_POST['discountedPrice'];
        $product->selectedProductTemplate = $_POST['producttemplate'];
        $product->categories = $_POST['categories'];
        $product->minPeriode = (int)$_POST['minPeriode'];
        $product->isFood = $_POST['isfood'];
        
        $groups = $this->getApi()->getUserManager()->getAllGroups();
        foreach ($groups as $group) {
            $product->groupPrice->{$group->id} = $_POST['price_'.$group->id];
        }
        
        $product->addedAttributes = array();
        foreach($_POST['attributes'] as $attrid => $object) {
            $obj = new \core_productmanager_data_AttributeItem();
            $obj->text = $object['text'];
            $product->addedAttributes[$attrid] = $obj;
        }
        
        
        foreach ($this->getApi()->getProductManager()->getProductLists() as $list) {
            if ($_POST['productlist_'.$list->id] == "true") {
                if (!in_array($product->id, $list->productIds)) {
                    $list->productIds[] = $product->id;
                    $this->getApi()->getProductManager()->saveProductList($list);
                } 
            }
            
            if ($_POST['productlist_'.$list->id] == "false") {
                if (in_array($product->id, $list->productIds)) {
                    $this->removeProductFromList($list, $product->id);
                }
            }
        }
        echo $product->shortDescription;
        
        
        $prices = array();
        foreach ($product->prices as $dynamicPrice) {
            $dynamicPrice->from = $_POST['price_from_'.$dynamicPrice->id];
            $dynamicPrice->to = $_POST['price_to_'.$dynamicPrice->id];
            $dynamicPrice->price = $_POST['price_price_'.$dynamicPrice->id];
            $prices[] = $dynamicPrice;
        }
        
        $product->prices = $prices;
        $this->getApi()->getProductManager()->saveProduct($product);
    }
    
    public function createProduct() {
        $product = $this->getProductFromData();
        $createdProduct = $this->getApi()->getProductManager()->saveProduct($product);
        echo json_encode($createdProduct->id);
        die();
    }
    
    public function deleteProduct() {
        $this->getApi()->getProductManager()->removeProduct($_POST['value']);
    }
    
    public function saveImage() {
        $content = strstr($_POST['fileBase64'], "base64,");
        $content = str_replace("base64,", "", $content);
        $content = base64_decode($content);
        $imgId = \FileUpload::storeFile($content);

        $product = $this->getApi()->getProductManager()->getProduct($_POST['productId']);
        $product->imagesAdded[] = $imgId;
        $this->getApi()->getProductManager()->saveProduct($product);
    }
    
    public function removeImage() {
        $product = $this->getApi()->getProductManager()->getProduct($_POST['value']);
        $keepImages = array();
        foreach ($product->imagesAdded as $image) {
            if ($image != $_POST['value2']) {
                $keepImages[] = $image;
            }
        }
        
        $product->images = null;
        $product->imagesAdded = $keepImages;
        $this->getApi()->getProductManager()->saveProduct($product);
    }
    
    public function renderDashBoardWidget() {
        $this->includefile("dashboardwidget");
    }

    public function removeProductFromList($list, $productId) {
        $keep = array();
        foreach ($list->productIds as $id) {
            if ($id != $productId) {
                $keep[] = $id;
            }
        }
        
        $list->productIds = $keep;
        $this->getApi()->getProductManager()->saveProductList($list);
    }
    
    public function setFilterProducts() {
        $_SESSION['products_admin_search_word_page'] = 1;
        $_SESSION['products_admin_search_word'] = $_POST['filterCriteria'];
    }

    public function getSearchWord() {
        if (isset($_SESSION['products_admin_search_word'])) {
            return $_SESSION['products_admin_search_word'];
        }
        
        return "";
    }
    
    public function getGssPageNumber() {
        if (isset($_SESSION['products_admin_search_word_page'])) {
            return $_SESSION['products_admin_search_word_page'];
        }
        
        return 1;
    }
    
    public function setGssNextPage() {
        $pageNumber = $this->getGssPageNumber();
        $pageNumber++;
        $_SESSION['products_admin_search_word_page'] = $pageNumber;
    }
    
    public function setGssPrevPage() {
        $pageNumber = $this->getGssPageNumber();
        $pageNumber--;
        if ($pageNumber < 1) {
            $pageNumber = 1;
        }
        $_SESSION['products_admin_search_word_page'] = $pageNumber;
    }
    
    public function setImageAsMainImage() {
        $product = $this->getApi()->getProductManager()->getProduct($_POST['value']);
        $product->mainImage =  $_POST['value2'];
        $this->getApi()->getProductManager()->saveProduct($product);
    }

    public function changeProductPriceModel() {
        $this->getApi()->getProductManager()->setProductDynamicPrice($_POST['value'], $_POST['dynamicPrices']);
    }
    
    public function createGroupProduct() {
        $product = $this->getProductFromData();
        $product->isGroupedProduct = true;
        $createdProduct = $this->getApi()->getProductManager()->saveProduct($product);
        echo json_encode($createdProduct->id);
        die();
    }

    public function getProductFromData() {
        $product = $this->getApi()->getProductManager()->createProduct();
        $product->name = $_POST['title'];
        $price = str_replace(",", ".", $_POST['price']);
        $product->price = $price;
        $product->sku = $_POST['sku'];
        return $product;
    }
    
    public function saveGroupedProduct() {
        $product = $this->getApi()->getProductManager()->getProduct($_POST['productid']);
        $product->name = $_POST['title'];
        
        $products = $this->getApi()->getProductManager()->getAllProducts();
        $subProducts = [];
        
        foreach ($products as $iProduct) {
            if ($_POST['subproduct_'.$iProduct->id] == "true") {
                $subProducts[] = $iProduct->id;
            }
        }
        
        $product->subProductIds = $subProducts;
        $this->getApi()->getProductManager()->saveProduct($product);
        
    }
    
    public function copyProduct() {
        $product = $this->getApi()->getProductManager()->copyProduct($_POST['fromProductId'], $_POST['newName']);
        $_POST['value'] = $product->id;
    }

    public function saveCombinationSkus() {
        $product = $this->getApi()->getProductManager()->getProduct($_POST['value']);
        foreach($_POST['combinations'] as $combination) {
            $product->variationCombinations->{$combination["id"]} = $combination["sku"];
        }
        $this->getApi()->getProductManager()->saveProduct($product);
    }
}
