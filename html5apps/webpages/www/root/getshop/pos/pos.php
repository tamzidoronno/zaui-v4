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
class pos extends PageCommon {
    public $title = "GetShop - Point Of Sales";
    public $title_rewrite = "pms";
    public $subpageof = "products";
    public $menuSequence = 4;
    public $description = array("en" => "GetShop pos extends your possibilites by adding a salepoint to your product, if you want to sell extras, this is a perfect tool for you.");
    
    /** Menu settings */
    public $menuEntries = array('en' => "Point of sales", "no" => "Kassesystem");
    public $menuLargeDesc = array('en' => "A pos suitable to sell more then just rooms.", "no" => "kommer snart...");
}
