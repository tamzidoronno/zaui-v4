<?php
namespace ns_1051b4cf_6e9f_475d_aa12_fc83a89d2fd4;

/**
 * @author boggi
 */
class TopMenu extends \SystemApplication implements \Application {
    var $entries;
    var $currentMenuEntry;
    
    public function getDescription() {
        return $this->__f("A topmenu enables you to display top menu entries");
    }

    public function getName() {
        return $this->__f("Top menu");
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }
    
    public function setAsHomePage() {
        $storeConfig = $this->getFactory()->getStoreConfiguration();
        $storeConfig->homePage = $_POST['data']['id'];
        $this->getApi()->getStoreManager()->saveStore($storeConfig);
    }
    
    public function addEntry() {
        $entry = $this->getApiObject()->core_listmanager_data_Entry();
        $entry->name = $_POST['data']['newentry'];
        $entry->pageType = -1;
        $entry = $this->getApi()->getListManager()->addEntry($this->getConfiguration()->id, $entry, "");
        $_GET['page'] = $entry->pageId;
        $this->getFactory()->clearCachedPageData();
        $this->getFactory()->initPage();
    }
    
    public function renameEntry() {
        if(isset($_POST['data']['renameId'])) {
            if($_POST['data']['newentry']) {
                $entry = $this->getApi()->getListManager()->getListEntry($_POST['data']['renameId']);
                $entry->name = $_POST['data']['newentry'];
                $this->getApi()->getListManager()->updateEntry($entry);
            }
        }
    }

    public function render() {
        $this->entries = $this->getApi()->getListManager()->getList($this->getConfiguration()->id);
        $this->includefile("TopMenuTemplateEntries");
    }
    
    public function deleteEntry() {
        $id = $_POST['data']['id'];
        $this->getApi()->getListManager()->deleteEntry($id, $this->getConfiguration()->id);
    }
    
    public function getCurrentMenuEntry() {
        return $this->currentMenuEntry;
    }
    
    public function setCurrentMenuEntry($entry) {
        $this->currentMenuEntry = $entry;
    }
    
    public function getEntries() {
        return $this->entries;
    }
    
    public function MoveEntry() {
        $id = $_POST['data']['id'];
        $after = @$_POST['data']['after'];
        $this->getApi()->getListManager()->orderEntry($id, $after, "");
    }
}

?>
