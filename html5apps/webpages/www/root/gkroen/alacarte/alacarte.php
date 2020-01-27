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
class alacarte extends PageCommon {
    public $title = "A la carte";
    public $title_rewrite = "alacarte";
    public $subpageof = "resturant";
    public $hiddenFromMenu = false;
    
    public $menuEntries = array('en' => "A La Carte", "no" => "A La Carte");
    public $menuLargeDesc = array('en' => "We offer take-away and catering. Please contact us for more information.", "no" => "Vi kan levere alt av mat til ditt selskap. Ta kontakt for tilbud.");
}
