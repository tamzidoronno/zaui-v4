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
class pms extends PageCommon {
    public $title = "GetShop - Property Management System";
    public $title_rewrite = "pms";
    public $subpageof = "products";
    
    /** Menu settings */
    public $menuEntries = array('en' => "Property Management System", "no" => "Bookingsystem");
    public $menuLargeDesc = array('en' => "GetShop PMS is the first in the world that brings a floating feature for bookings. All our systems are cloud-based and integrated with OTAs (booking.com, expedia.com).", "no" => "Først i verden...");
}
