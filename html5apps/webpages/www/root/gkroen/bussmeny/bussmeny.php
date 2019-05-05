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
class bussmeny extends PageCommon {
    public $title = "Bussmeny";
    public $title_rewrite = "Bussmeny";
    public $subpageof = "resturant";
    public $hiddenFromMenu = false;
    
    public $menuEntries = array('en' => "Bussmeny", "no" => "Bussmeny");
    public $menuLargeDesc = array('en' => "Vi kan sette sammen en meny til ditt reisefølge.", "no" => "Vi kan sette sammen en meny til ditt reisefølge.");
}
