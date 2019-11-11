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
    public $title = "BA 10 leiligheter & næring til leie";
    public $title_rewrite = "homepage";
    public $subpageof = "";
    public $hiddenFromMenu = false;
    public $menuSequence = -1;
    
    public $menuEntries = array('en' => "Home", "no" => "Hjem");
}
