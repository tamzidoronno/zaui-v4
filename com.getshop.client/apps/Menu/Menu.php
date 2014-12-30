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

    public function getDescription() {
        return "Add a new menu to your page. Menues are the root of all pages, adding a new menuentry is the same as adding a new page.";
    }

    public function updateLists() {
        $result = $_POST['data'];
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
            $this->getApi()->getListManager()->setEntries($id, $allentries);
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
        if (isset($item['items'])) {
            foreach ($item['items'] as $subitem) {
                $entry->subentries[] = $this->convertToEntry($subitem);
            }
        }
        return $entry;
    }

    public function applicationAdded() {
        $this->getApi()->getListManager()->createMenuList($this->getConfiguration()->id);
    }

    public function addEntry() {
        $core_listmanager_data_Entry = new \core_listmanager_data_Entry();
        $core_listmanager_data_Entry->name = $_POST['data']['text'];
        $this->getApi()->getListManager()->addEntry($this->getConfiguration()->id, $core_listmanager_data_Entry, null);
    }

    public function getMenuEntries() {
        $entries = $this->getApi()->getListManager()->getList($this->getConfiguration()->id);
        if (!is_array($entries)) {
            return array();
        }

        return $entries;
    }

    public function printEntries($entries, $level) {
        echo "<div class='entries'>";
        foreach ($entries as $entry) {
            $name = $entry->name;
            $linkName = \GetShopHelper::makeSeoUrl($entry->name);
            $pageId = $entry->pageId;
            $activate = $this->getPage()->getId() == $pageId ? "active" : "";
            echo "<div class='entry $activate'><a ajaxlink='/?page=$pageId' href='$linkName'><div>$name</div></a>";
            if ($entry->subentries) {
                $this->printEntries($entry->subentries, $level+1);
            }
            echo "</div>";
        }
        echo "</div>";
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
