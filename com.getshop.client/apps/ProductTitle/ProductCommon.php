<?php

namespace ns_e4e2508c_acf8_4064_9e94_93744881ff00;

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

class ProductCommon extends \WebshopApplication {
    private $product = null;
    
    /**
     * 
     * @return \core_productmanager_data_Product
     */
    public function getProduct() {
        if (!$this->product) {
            $this->product = $this->getApi()->getProductManager()->getProductByPage($this->getPage()->getId());
        }
        
        if (!$this->product) {
            $this->product = $this->getApi()->getProductManager()->getRandomProducts(1, "");
            $this->product = $this->product[0];
        }
        
        return $this->product;
    }
}