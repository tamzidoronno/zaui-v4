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
class terminals extends PageCommon {
    public $title = "GetShop - Self Checkin Terminals";
    public $title_rewrite = "pms";
    public $subpageof = "products";
    public $menuSequence = 4;
    public $description = array("en" => "A self check in terminal can be set up in your lobby to give the guests a possibility to book and check in automatically without you bein involved.");
    
    /** Menu settings */
    public $menuEntries = array('en' => "Self Check-in Terminals", "no" => "Gjestehjelperen");
    public $menuLargeDesc = array('en' => "Divert some of the queue to terminals so that you are able to handle more guests pr employee", "no" => "kommer snart...");
}
