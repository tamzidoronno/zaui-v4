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
class pga extends PageCommon {
    public $title = "GetShop - Personal Guest Assistant";
    public $title_rewrite = "pms";
    public $subpageof = "products";
    public $menuSequence = 4;
    public $description = array("en" => "A personal guest assistant helps you automate communication with your guests without you being involved, weather that is buying additional features, late check out, extra cleaning, etc.");
    
    /** Menu settings */
    public $menuEntries = array('en' => "Personal Guest Assistant", "no" => "Gjestehjelperen");
    public $menuLargeDesc = array('en' => "Let you guests do some changes to their stay, without reception interfering", "no" => "kommer snart...");
}
