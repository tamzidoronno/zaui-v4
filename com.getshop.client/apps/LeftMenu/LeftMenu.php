<?php

namespace ns_00d8f5ce_ed17_4098_8925_5697f6159f66;

/**
 * Description of LeftMenu
 *
 * @author boggi
 */
class LeftMenu extends \WebshopApplication implements \Application {

    var $entries;
    var $dept;
    var $currentMenuEntry;
    var $topLevelDepth = -1;

    function __construct() {
        $this->setSkinVariable("canUploadIcons", false, "If it is possible to upload icons on the first level of entries in the left menu.");
        $this->setSkinVariable("ICON_HEIGHT", 40, "Sets height of the icon on first level");
        $this->setSkinVariable("ICON_WIDTH", 40, "Sets width of the icon on the first level");
    }

    public function getDescription() {
        return $this->__f("Get started building yourself a left / right menu, you can do it by adding this application!<br><br>This menu support subentries and hardlinking, and for each entry a page is created where you can add products / other applications.");
    }

    public function getAvailablePositions() {
        return "left";
    }

    public function getName() {
        return $this->__("Left Menu");
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }

    public function loadEntries() {
        $this->entries = $this->getApi()->getListManager()->getList($this->getConfiguration()->id);
    }

    public function render() {
        $this->loadEntries();
        $this->includefile("LeftMenuTemplate");
    }

    public function addEntry() {
        $name = $_POST['data']['newentry'];
        $this->createEntry($name);
    }

    private function createEntry($name) {
        $entry = $this->getApiObject()->core_listmanager_data_Entry();
        $entry->name = $name;
        $entry->pageType = 2;
        $entry = $this->getApi()->getListManager()->addEntry($this->getConfiguration()->id, $entry, null);
        $pageId = $entry->pageId;
        $appid = $this->getConfiguration()->id;
        if (isset($_POST['applicationArea'])) {
            $pageArea = $_POST['applicationArea'];
        } else {
            $pageArea = $_POST['core']['apparea'];
        }
        $this->getApi()->getPageManager()->addExistingApplicationToPageArea($pageId, $appid, $pageArea);
        
        $_GET['page'] = $entry->pageId;
        $this->getFactory()->clearCachedPageData();
        $this->getFactory()->initPage();

    }

    public function addSubEntry() {
        if (isset($_POST['data']['newentry'])) {
            $entry = $this->getApiObject()->core_listmanager_data_Entry();
            $entry->name = $_POST['data']['newentry'];
            $entry->parentId = $_POST['data']['entryId'];
            $parentPageId = $this->getConfiguration()->originalPageId;

            $listId = $this->getConfiguration()->id;
            $entry = $this->getApi()->getListManager()->addEntry($listId, $entry, $parentPageId);

            $pageId = $entry->pageId;
            $appid = $this->getConfiguration()->id;
            if (isset($_POST['applicationArea'])) {
                $pageArea = $_POST['applicationArea'];
            } else {
                $pageArea = $_POST['core']['apparea'];
            }
            $this->getApi()->getPageManager()->addExistingApplicationToPageArea($pageId, $appid, $pageArea);
        }
    }

    public function renameEntry() {
        if (isset($_POST['data']['newName']) && $_POST['data']['newName']) {
            $id = $_POST['data']['entryId'];
            $entry = $this->getApi()->getListManager()->getListEntry($id);
            $entry->name = $_POST['data']['newName'];
            $this->getApi()->getListManager()->updateEntry($entry);
        }
    }

    public function applicationAdded() {
        
    }

    public function deleteEntry() {
        $this->getApi()->getListManager()->deleteEntry($_POST['data']['id'], $this->getConfiguration()->id);
        $this->loadEntries();
        if (count($this->getEntries()) == 0) {
            $pageId = $this->getConfiguration()->originalPageId;
            $this->getApi()->getPageManager()->removeApplication($this->getConfiguration()->id, $pageId);
        }
    }

    public function getCurrentMenuEntry() {
        return $this->currentMenuEntry;
    }

    public function getDept() {
        return $this->dept;
    }

    public function displayMenuEntry($entry, $dept) {
        $this->currentMenuEntry = $entry;
        $this->dept = $dept;
        if ($dept == 0) {
            $this->topLevelDepth++;
        }
        $this->includefile("LeftMenuTemplateMenuEntry");
    }

    public function getTopLevelDepth() {
        return $this->topLevelDepth;
    }

    public function displayMenuConfiguration() {
        $this->includefile("LeftMenuTemplateMenuConfiguration");
    }

    public function MoveEntry() {
        $parentid = $_POST['data']['parentid'];
        $after = $_POST['data']['after'];
        $id = $_POST['data']['id'];
        if ($parentid == "0") {
            $parentid = "";
        }
        $this->getApi()->getListManager()->orderEntry($id, $after, $parentid);
    }

    /**
     * Return a list of entries.
     * @return core_listmanager_data_Entry
     */
    public function getEntries() {
        if (!$this->entries) {
            return array();
        }
        return $this->entries;
    }

    private function storeFile() {
        $tmp_name = $_FILES['files']['tmp_name'];
        $content = file_get_contents($tmp_name[0]);

        if (!$content) {
            die("Failed to fetch file");
        }

        return \FileUpload::storeFile($content);
    }

    public function uploadIcon() {
        /* @var $fileSaved core_filemanager_answer_FileSaved */
        $entry = $this->getApi()->getListManager()->getListEntry($_POST['id']);
        $entry->imageId = $this->storeFile();
        $this->getApi()->getListManager()->updateEntry($entry);

        $result['imageId'] = $entry->imageId;
        echo json_encode($result);
    }

    public function removeIcon() {
        $entry = $this->getApi()->getListManager()->getListEntry($_POST['data']['id']);
        $entry->imageId = null;
        $this->getApi()->getListManager()->updateEntry($entry);
    }

    public function isLastInLevel() {
        $id = $this->currentMenuEntry->id;
        $entries = $this->entries;
        return $this->isLastInLevelRecursive($entries, $id);
    }

    public function isLastInLevelRecursive($entries, $id) {
        $foundOnSubLevel = false;

        $i = 0;
        foreach ($entries as $entry) {
            $i++;

            if ($id == $entry->id) {
                return count($entries) == $i;
            }

            if (is_array($entry->subentries) && !$foundOnSubLevel) {
                $foundOnSubLevel = $this->isLastInLevelRecursive($entry->subentries, $id);
            }
        }

        return $foundOnSubLevel;
    }

}

?>
