<?php

/*
 * This application is a system application, that means it is required to 
 * be added to everything.
 * 
 * A Menu editor is used for editing menulists in the system. 
 */

/**
 * @author ktonder
 */

namespace ns_a11ac190_4f9a_11e3_8f96_0800200c9a66;

class Menu extends \SystemApplication implements \Application {
    private $menues = null;
    
    public function getDescription() {
        return $this->__w("Add menus to your page and administrate them. Menus are the root of all pages, adding a new menu entry is the same as creating a new page.");
    }

    public function updateLists() {
        $result = $_POST['data'];
        $toSave = [];
        foreach ($result as $id => $list) {
            if (!isset($list['items'])) {
                continue;
            }
            $items = $list['items'];
            $name = $list['name'];
            $allentries = array();
            foreach ($items as $item) {
                $entry = $this->convertToEntry($item);
                $allentries[] = $entry;
            }
            
            if (isset($list['deleted']) && $list['deleted'] == "true") {
                $this->getApi()->getListManager()->deleteMenu($this->getConfiguration()->id, $id);
            } else {
                $data = [$id, $allentries, $name];
                $toSave[] = $data;    
            }
            
        }
        
        foreach ($toSave as $save) {
            $this->getApi()->getListManager()->saveMenu($this->getConfiguration()->id, $save[0], $save[1], $save[2]);
        }
        
    }

    public function getName() {
        return $this->__f("Menu editor");
    }

    public function render() {
        $this->includefile("menu");
    }

    public function renderSetup() {
        $this->includeFile("setup");
    }

    public function getMenuLists() {
        $menues = $this->getMenus();
        $lists = [];
        foreach ($menues as $menu) {
            $lists[] = $menu->entryList;
        }
        return $lists;
    }

    private function createItems($items) {
        $retItems = array();

        if (!$items || !is_array($items)) {
            return $retItems;
        }

        foreach ($items as $item) {
            $entryItem = new EntryItem();
            $entryItem->id = $item->id;
            $entryItem->name = $item->name;
            $entryItem->linke = $item->hardLink;
            $entryItem->fontAwsomeIcon = $item->fontAwsomeIcon;
            $entryItem->userLevel = $item->userLevel;
            $entryItem->scrollAnchor = $item->scrollAnchor;
            $entryItem->scrollPageId = $item->scrollPageId;
            $entryItem->items = $this->createItems($item->subentries);
            $entryItem->disabledLangues = $item->disabledLangues;
            $entryItem->hidden = $item->hidden;
            $retItems[] = $entryItem;
        }

        return $retItems;
    }


    public function getJSonEncodedList($listEntry) {
        $entryList = new EntryList();
        $entryList->menuname = $listEntry->name;
        $entryList->items = $this->createItems($listEntry->entries);
        return json_encode($entryList);
    }

    public function convertToEntry($item) {
        $entry = $this->getApi()->getListManager()->getListEntry(@$item['id']);
        if (!$entry) {
             $entry = new \core_listmanager_data_Entry();
        }
        
        $entry->name = $item['name'];
        @$entry->hardLink = $item['link'];
        @$entry->scrollPageId = $item['scrollPageId'];
        @$entry->scrollAnchor = $item['scrollAnchor'];
        @$entry->hidden = $item['hidden'] == "true";
        
        if (isset($item['userLevel'])) {
            $entry->userLevel = $item['userLevel'];
        }
        if (isset($item['icon'])) {
            $entry->fontAwsomeIcon = $item['icon'];
        }

        
        $entry->subentries = array();
        if(isset($item['items'])) {
            foreach($item['items'] as $subitem) {
                $entry->subentries[] = $this->convertToEntry($subitem);
            }
        }
        
        if (!isset($item['disabledLangues'])) {
            $entry->disabledLangues = [];
        } else {
            $entry->disabledLangues = $item['disabledLangues'];
        }
        
        return $entry;

    }
    
    public function setHomePage() {
        $entryId = $_POST['data']['entryId'];
        $entry = $this->getApi()->getListManager()->getListEntry($entryId);
        $storeConfig = $this->getApi()->getStoreManager()->getMyStore();
        $storeConfig->homePage = $entry->pageId;
        $this->getApi()->getStoreManager()->saveStore($storeConfig);
    }
    
    public function setPageHomePage() {
        $pageName = $_POST['data']['pageName'];
        $storeConfig = $this->getApi()->getStoreManager()->getMyStore();
        $storeConfig->homePage = $pageName;
        $this->getApi()->getStoreManager()->saveStore($storeConfig);
    }

    public function applicationAdded() {
        $this->getApi()->getListManager()->createMenuList($this->getConfiguration()->id);
    }

    public function addEntry() {
        $core_listmanager_data_Entry = new \core_listmanager_data_Entry();
        $core_listmanager_data_Entry->name = $_POST['data']['text'];
        $this->getApi()->getListManager()->addEntry($this->getConfiguration()->id, $core_listmanager_data_Entry, null);
    }
    
    private function isDisabledDueToLanaguage($entry) {
        $selectedLanguage = $this->getFactory()->getSelectedTranslation(); 
        foreach ($entry->disabledLangues as $key => $lang) {
            if ($lang == $selectedLanguage) {
                return true;
            }
        }
            
        return false;
    }
    
    public function renderConfig() {
        $this->includefile("config");
    }

    private function isDisabledDueToAccessLevel($entry) {
        if (isset($entry->userLevel) && $entry->userLevel) {
            $user = \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject();
            if ($user == null) {
                return true;
            }

            if ($user->type < $entry->userLevel) {
                return true;
            }
        }        
    }
    
    /**
     * 
     * @param \core_listmanager_data_Entry[] $entries
     * @param type $level
     * @param type $prefix
     */
    public function printEntries($entries, $level, $prefix) {
        echo "<div class='entries'>";
        
        foreach ($entries as $entry) {
            if ($this->isDisabledDueToAccessLevel($entry) && !$this->isDisabledEnabled()) {
                continue;
            }
            
            if ($this->isDisabledDueToLanaguage($entry)) {
                continue;
            }
            
            if ($entry->hidden) {
                continue;
            }
            
            $name = $entry->name;
            $linkName = \GetShopHelper::makeSeoUrl($entry->name, $prefix);
            $pageId = $entry->pageId;

            $fontAwesome = "";
            if (isset($entry->fontAwsomeIcon) && trim($entry->fontAwsomeIcon))  {
                $fontAwesome = "<i class='fa ".$entry->fontAwsomeIcon."'></i> ";
            }
            $activate = $this->getPage()->getId() == $pageId ? "active" : "";
            
            $link = "/?page=$pageId";
            if (isset($entry->hardLink) && $entry->hardLink) {
                $link = $entry->hardLink;
                $linkName = $entry->hardLink;
                if(stristr($link,"page=".$this->getPage()->getId())) {
                    $activate = "active";
                }
            }
            
            $disabledClass = ""; 
            if ($this->isDisabledEnabled() && $this->isDisabledDueToAccessLevel($entry)) {
                $disabledClass = "gs_menu_disabled";
                $link = "?page=login";
                $linkName = "?page=login";
            }
            
            if ($entry->scrollPageId && $entry->scrollAnchor) {
                echo "<div class='entry $disabledClass'><div scrollPageId='$entry->scrollPageId' scrollAnchor='$entry->scrollAnchor' class='gs_scrollitem'>$fontAwesome $name</div>";
                if ($entry->subentries) {
                    $this->printEntries($entry->subentries, $level+1, $prefix);
                }
                echo "</div>";
            } else {
               echo "<div class='entry $activate $disabledClass'><a ajaxlink='$link' href='$linkName'><div>$fontAwesome $name</div></a>";
                if ($entry->subentries) {
                    $this->printEntries($entry->subentries, $level+1, $prefix);
                }
                echo "</div>";
            }
        }
        echo "</div>";
    }

    public function saveGlobalSettings() {
        $this->setConfigurationSetting("disableMenuItemsInsteadOfHide", $_POST['disableMenuItemsInsteadOfHide']);
    }
    
    public function isDisabledEnabled() {
        $disabled = $this->getGlobalConfigurationSetting("disableMenuItemsInsteadOfHide");
        
        if (isset($disabled) && $disabled == "true") {
            return true;
        }
        
        return false;   
    }
    
    public function getMenus() {
        if (!$this->menues) {
            $this->menues = $this->getApi()->getListManager()->getMenues($this->getConfiguration()->id);
        }
        
        return $this->menues;
    }
    
}

class EntryItem {
    public $id = "";
    public $name = "";
    public $link = "";
    public $linke = "";
    public $pageId = "";
    public $fontAwsomeIcon = "";
    public $scrollPageId = "";
    public $scrollAnchor = "";
    public $userLevel;
    public $items;
    public $disabledLangues;
    public $hidden = false;
}

class EntryList {

    public $menuname = "";
    public $items = array();

}

?>
