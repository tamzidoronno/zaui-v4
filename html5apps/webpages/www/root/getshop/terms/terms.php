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
class onlinebooking extends PageCommon {
    public $title = "GetShop - Embeddable Booking Engine";
    public $title_rewrite = "pms";
    public $subpageof = "products";
    public $menuSequence = 2;
    public $description = array("en" => "GetShop comes with a simple to use but yet powerful booking engine that can be embed into any website with less than 10 lines of code.");
    
    /** Menu settings */
    public $menuEntries = array('en' => "Online Booking", "no" => "Online booking");
    public $menuLargeDesc = array('en' => "Embed our bookingengine directly at your webpage.", "no" => "kommer snart...");
}
