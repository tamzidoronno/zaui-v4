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
    
    public $menuEntries = array('en' => "Bus menu", "no" => "Bussmeny");
    public $menuLargeDesc = array('en' => "Traveling as a group, or having a function? We can put together a menu if you would like something thats not currently on our menu.", "no" => "Vi kan sette sammen en meny til ditt reisefølge.");
}
