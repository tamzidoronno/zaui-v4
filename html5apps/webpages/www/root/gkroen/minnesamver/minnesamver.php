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
class minnesamver extends PageCommon {
    public $title = "Minnesamvær";
    public $title_rewrite = "minnesamver";
    public $subpageof = "resturant";
    public $hiddenFromMenu = false;
    
    public $menuEntries = array('en' => "Minnesamvær", "no" => "Minnesamvær");
    public $menuLargeDesc = array('en' => "Våre lokaler egner seg for minnesamvær. Vi tilrettelegger for deg.", "no" => "Våre lokaler egner seg for minnesamvær. Vi tilrettelegger for deg.");
}
