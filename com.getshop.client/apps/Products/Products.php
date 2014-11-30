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
        return $this->__f("Setup and manage your products");
    }

    public function getName() {
        return $this->__f("Products");
    }

    public function renderConfig() {
        $this->includefile("frontpage");
    }
    
    public function render() {
        
    }

    public function updateProduct() {
        $product = $this->getApi()->getProductManager()->getProduct($_POST['productid']);
        $product->name = $_POST['title'];
        $product->shortDescription = $_POST['lisviewdescription'];
        $product->price = $_POST['price'];
        $product->sku = $_POST['sku'];
        
        
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
        $this->getApi()->getProductManager()->saveProduct($product);
    }
    
    public function createProduct() {
        $product = $this->getApi()->getProductManager()->createProduct();
        $product->name = $_POST['title'];
        $product->price = $_POST['price'];
        $product->sku = $_POST['sku'];
        $this->getApi()->getProductManager()->saveProduct($product);
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

}
