<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of Home
 *
 * @author ktonder
 */
class products extends PageCommon {
    public $title = "GetShop - Products";
    public $title_rewrite = "products";
    public $subpageof = "";
    public $canNavigateTo = false;
    
    public $menuEntries = array('en' => "Products", "no" => "Produkter");
}
