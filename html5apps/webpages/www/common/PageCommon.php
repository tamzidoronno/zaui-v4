<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of PageCommon
 *
 * @author ktonder
 */
class PageCommon {
    public $hiddenFromMenu = false;
    
    private $currentLanguage = "no";
    
    public $canNavigateTo = true;
    
    public $menuSequence = 0;
    
    public function getMenuName() {
        return $this->menuEntries[$this->currentLanguage];
    }
    
    public function getLargeMenuText() {
        if (isset($this->menuLargeDesc)) {
            return $this->menuLargeDesc[$this->currentLanguage];
        } 
        
        return "";
    }
}
