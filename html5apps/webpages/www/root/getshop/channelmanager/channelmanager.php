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
class channelmanager extends PageCommon {
    public $title = "GetShop - Channel Manager";
    public $title_rewrite = "pms";
    public $subpageof = "products";
    public $menuSequence = 3;
    
    /** Menu settings */
    public $menuEntries = array('en' => "Channel Manager", "no" => "Online booking");
    public $menuLargeDesc = array('en' => "Get yourself connected to the OTA, GDS, etc", "no" => "kommer snart...");
}
