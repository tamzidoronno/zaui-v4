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
class rooms extends PageCommon {
    public $title = "Rooms";
    public $title_rewrite = "rooms";
    public $subpageof = "";
    public $hiddenFromMenu = false;
    
    public $menuEntries = array('en' => "Rooms", "no" => "Våre Rom");
}
