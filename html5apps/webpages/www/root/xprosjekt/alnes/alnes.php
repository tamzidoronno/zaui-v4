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
class alnes extends PageCommon {
    public $title = "Alnes Gård";
    public $title_rewrite = "alnesgaard";
    public $subpageof = "";
    public $hiddenFromMenu = false;
    
    public $menuEntries = array('en' => "Alnes farm", "no" => "Alnes Gård");
}
