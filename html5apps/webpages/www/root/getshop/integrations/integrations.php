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
class integrations extends PageCommon {
    public $title = "Integrations suppported by GetShop";
    public $title_rewrite = "integrations";
    public $subpageof = "products";
    public $menuSequence = 7;
    public $hiddenFromMenu = false;
    
    /** Menu settings */
    public $menuEntries = array('en' => "Integrations to the GetShop PMS system", "no" => "Integrasjoner i GetShop PMS systemet");
    public $menuLargeDesc = array('en' => "We have a wide range of integrations to help you forfill your automation dream", "no" => "kommer snart...");
}
