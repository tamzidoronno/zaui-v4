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
class selskapsmeny extends PageCommon {
    public $title = "Selskaps Menu";
    public $title_rewrite = "fastmeny";
    public $subpageof = "resturant";
    public $hiddenFromMenu = false;
    
    public $menuEntries = array('en' => "Menu", "no" => "Selskaps Meny");
    public $menuLargeDesc = array('en' => "A pos suitable to sell more then just rooms.", "no" => "Alle hovedretter serveres med ferske grønnsaker. Du velger potetvariant.");
}
