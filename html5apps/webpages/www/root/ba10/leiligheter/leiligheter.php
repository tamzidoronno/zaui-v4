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
class leiligheter extends PageCommon {
    public $title = "Utleie leiligheter";
    public $title_rewrite = "rooms";
    public $subpageof = "";
    public $hiddenFromMenu = false;
    
    public $menuEntries = array('en' => "Rooms", "no" => "Bolig");
}
