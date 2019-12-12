<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of Resturant
 *
 * @author ktonder
 */
class Resturant {
    private $jsonData = false;
    
    public function render() {
        echo '<link rel="stylesheet" href="resturant/resturant.css">';
        echo '<script src="resturant/Resturant.js"></script>';
        
        if (isset($_GET['list'])) {
            include 'products.phtml';
        } else {
            include 'header.phtml';
            include 'categories.phtml';
        }
        
        include 'shoppingcart.phtml';
    }
    
    /**
     * 
     * @return GetShopApi
     */
    public function getApi() {
        $factory = IocContainer::getFactorySingelton(false);
        return $factory->getApi();
    }
    
    public function getLang($code) {
        if (!$this->jsonData) {
            $string = file_get_contents("scripts/pga/resturant/baselang.json");
            $this->jsonData = json_decode($string,true);
        }
        
        
        $ret = $this->jsonData[$code];
        
        if (!$ret) {
            echo "Warning: not found code $code";
        }
        
        return $ret;
    }
}
