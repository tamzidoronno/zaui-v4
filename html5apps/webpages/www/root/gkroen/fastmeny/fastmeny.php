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
class fastmeny extends PageCommon {
    public $title = "Normal Menu";
    public $title_rewrite = "fastmeny";
    public $subpageof = "resturant";
    public $hiddenFromMenu = false;
    public $menuSequence = -1;
    
    public $menuEntries = array('en' => "Menu", "no" => "Fast Meny");
    public $menuLargeDesc = array('en' => "Here you find our current menu, this may change during the year.", "no" => "Her finner du informasjon om hva som «står på tavla» akkurat nå. Menyen varierer gjennom året.");
}
