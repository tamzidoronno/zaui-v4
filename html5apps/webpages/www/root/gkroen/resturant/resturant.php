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
class resturant extends PageCommon {
    public $title = "Resturant";
    public $title_rewrite = "resturant";
    public $subpageof = "";
    public $hiddenFromMenu = false;
    public $canNavigateTo = false;
    
    public $menuEntries = array('en' => "Dining", "no" => "Restaurant");
}
