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

class MenuEditor extends \SystemApplication implements \Application {
   
    public function getDescription() {
        return "";
    }

    public function updateLists() {
        $result = $_POST['data'];
        foreach($result as $id => $list) {
            $items = $list['items'];
            $name = $list['name'];
            $allentries = array();
            foreach($items as $item) {
                $entry = $this->convertToEntry($item);
                $allentries[] = $entry;
            }
            $this->getApi()->getListManager()->setEntries($id, $allentries);
        }
    }
    
    public function getName() {
        return $this->__f("Menu editor");
    }
    
    public function render() {
        echo "menu";
    }

    public function renderSetup() {
        $this->includeFile("setup");
    }
    
    public function getMenuLists() {
        return $this->getApi()->getListManager()->getAllListsByType("MENU");
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
            $entryItem->pageId = $item->pageId;
            $entryItem->linke = $item->hardLink;
            $entryItem->userLevel = $item->userLevel;
            $entryItem->items = $this->createItems($item->subentries);
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
        $entry = $this->getApi()->getListManager()->getListEntry($item['id']);
        if (!$entry) {
             $entry = new \core_listmanager_data_Entry();
        }
        $entry->name = $item['name'];
        if (isset($item['linke'])) {
            $entry->hardLink = $item['linke'];
        }
        
        if (isset($item['link']) && !isset($item['pageId'])) {
            $entry->hardLink = $item['link'];
        }
        
        if (isset($item['pageId']) && $item['pageId']) {
            $entry->pageId = $item['pageId'];
            $entry->hardLink = null;
        }
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
        return $entry;
    }
}

class EntryItem {
    public $id = "";
    public $name = "";
    public $linke = "";
    public $pageId = "";
    public $userLevel;
    public $items;
}

class EntryList {
    public $menuname = "";
    public $items = array();
}
?>
