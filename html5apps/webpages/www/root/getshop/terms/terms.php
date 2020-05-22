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
class terms extends PageCommon {
    public $title = "GetShop - Terms & Conditions";
    public $title_rewrite = "pms";
    public $subpageof = "products";
    public $menuSequence = 2;
    public $description = array("en" => "GetShop General terms & conditions.");
    
    /** Menu settings */
    public $menuEntries = array('en' => "Terms & Conditions", "no" => "Terms & Conditions");
    public $menuLargeDesc = array('en' => "Embed our bookingengine directly at your webpage.", "no" => "Kjøpsvilkår");
}
