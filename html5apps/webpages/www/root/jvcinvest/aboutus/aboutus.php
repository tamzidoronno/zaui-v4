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

class aboutus extends PageCommon {
    public $title = "About the hotel";
    public $title_rewrite = "pms";
    public $subpageof = "";
    
    public $menuSequence = 4;
    
    /** Menu settings */
    public $menuEntries = array('en' => "About the hotel", "no" => "Om hotellet");
}
