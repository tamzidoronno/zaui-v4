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
    private $subEntries = array();
    private $pluginPage;
    private $shouldPluginPageBeVisibleForGetShopAdminsWhenDeactived = false;
    
    function __construct($name, $pageId, $icon, $pluginPage=null, $shouldPluginPageBeVisibleForGetShopAdminsWhenDeactived=false) {
        $this->name = $name;
        $this->pageId = $pageId;
        $this->icon = $icon;
        $this->pluginPage = $pluginPage;
        $this->shouldPluginPageBeVisibleForGetShopAdminsWhenDeactived = $shouldPluginPageBeVisibleForGetShopAdminsWhenDeactived;
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


    function addSubEntry($entry) {
        $this->subEntries[] = $entry;
    }
    
    function getSubEntries() {
        return $this->subEntries;
    }
    
    function getPluginPageName() {
        return $this->pluginPage;
    }
    
    function shouldPluginPageBeVisibleForGetShopAdminsWhenDeactived() {
        return $this->shouldPluginPageBeVisibleForGetShopAdminsWhenDeactived;
    }
}
