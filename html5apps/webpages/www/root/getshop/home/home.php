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
class home extends PageCommon {
    public $title = "GetShop - The automated hotel system which enables you to automate your check in";
    public $title_rewrite = "homepage";
    public $subpageof = "";
    public $hiddenFromMenu = true;
    public $description = array("en" => "Over the years GetShop has become a leader in hotel automation, automated check in, automated payment handling, message automation to your guests, and much more.");
    
    public $menuEntries = array('en' => "Home", "no" => "Hjem");
}
