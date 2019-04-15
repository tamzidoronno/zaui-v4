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
    public $title = "Products";
    public $title_rewrite = "homepage";
    public $subpageof = "";
    public $hiddenFromMenu = true;
    
    public $menuEntries = array('en' => "Home", "no" => "Hjem");
}
