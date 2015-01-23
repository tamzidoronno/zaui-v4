<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

namespace ns_3cdcd5e5_b3e8_4fa1_9125_bef44fa6a082;

/**
 * Description of ReleatedProducts
 *
 * @author ktonder
 */
class RelatedProducts extends \WebshopApplication implements \Application {
    public function getDescription() {
        return $this->__("Add this application to add related products to a product page");
    }

    public function getName() {
        return $this->__f("Related Products");
    }

    public function render() {
        if (!count($this->getRelatedProducts())) {
            $this->includefile("norelatedproducts");
        } else {
            $this->includefile("relatedProducts");
        }
        
        if ($this->hasWriteAccess()) {
            $this->includefile("administration");
        }
    }
    
    public function searchForProducts() {
        $_SESSION['add_related_products_seachword'] = $_POST['data']['searchWord'];
        $this->includefile("searchresult");
    }
    
    public function getRelatedProducts() {
        $prodArray = [];
        $products = $this->getConfigurationSetting("products");
        if ($products) {
            $prodArray = json_decode($products);
        } 
        
        return $prodArray;
    }
    
    public function addProductAsRelated() {
        $prodArray = $this->getRelatedProducts();
        $prodArray[] = $_POST['data']['productId'];
        $this->setConfigurationSetting("products", json_encode($prodArray));
    }

    public function removeRelatedProduct() {
        $saveArray = [];
        
        $prodArray = $this->getRelatedProducts();
        foreach ($prodArray as $productId) {
            if ($productId != $_POST['data']['productId']) {
                $saveArray[] = $productId;
            }
        }
        
        $this->setConfigurationSetting("products", json_encode($saveArray));
    }
}