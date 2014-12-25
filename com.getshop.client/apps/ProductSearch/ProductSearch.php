<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

namespace ns_2ff5d77f_4d47_4fbf_8186_c7fbc33cb478;

class ProductSearch extends \ApplicationBase implements \Application {
    public function getDescription() {
        return $this->__f("Add this to the page and you can allow your customers to search for products.");
    }

    public function getName() {
        return $this->__f("Product Search");
    }

    public function render() {
        $this->includeFile("productSearch");
    }
    
    public function search() {
        $_GET['page'] = "productsearch";
    }

}