<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of ModulePageMenuItem
 *
 * @author ktonder
 */
class ModulePageMenuItem {
    private $name;
    private $pageId;
    private $icon;
    
    function __construct($name, $pageId, $icon) {
        $this->name = $name;
        $this->pageId = $pageId;
        $this->icon = $icon;
    }

    function getName() {
        return $this->name;
    }

    function getPageId() {
        return $this->pageId;
    }

    function getIcon() {
        return $this->icon;
    }


}
