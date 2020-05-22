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
    
    private $languagesSupported = ["en","no"];
    
    public $canNavigateTo = true;
    
    public $menuSequence = 0;
    
    public function getMenuName() {
        return $this->menuEntries[$this->getCurrentLanguage()];
    }
    
    public function getLargeMenuText() {
        if (isset($this->menuLargeDesc)) {
            return $this->menuLargeDesc[$this->getCurrentLanguage()];
        } 
        
        return "";
    }
    
    public function getCurrentLanguage() {
        if(isset($_SESSION['language'])) {
            return $_SESSION['language'];
        }
        
        $origLanguage = DomainConfig::$defaultLang;
        if ($origLanguage) {
            return $origLanguage;
        }
        
        return $this->currentLanguage;
    }
    
    public function getDescription() {
        if (isset($this->description)) {
            return $this->description[$this->getCurrentLanguage()];
        } 
        return "";
    }
}
