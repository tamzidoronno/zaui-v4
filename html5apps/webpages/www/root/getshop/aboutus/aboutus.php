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
    public $title = "GetShop - Property Management System";
    public $description = array("en" => "GetShop started in 2013 and specializing in hotel automation and keyless check in.");
    public $title_rewrite = "pms";
    public $subpageof = "";
    
    public $menuSequence = 1;
    
    /** Menu settings */
    public $menuEntries = array('en' => "About us", "no" => "Om oss");
}
