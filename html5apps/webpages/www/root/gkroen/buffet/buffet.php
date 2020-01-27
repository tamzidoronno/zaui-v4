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
class buffet extends PageCommon {
    public $title = "Buffet";
    public $title_rewrite = "fastmeny";
    public $subpageof = "resturant";
    public $hiddenFromMenu = false;
    
    public $menuEntries = array('en' => "Buffet/ Koldtbord", "no" => "Buffet/ Koldtbord");
    public $menuLargeDesc = array('en' => "", "no" => "På G – Kroen kan man velge form og innhold på bevertningen etter eget ønske.");
}
