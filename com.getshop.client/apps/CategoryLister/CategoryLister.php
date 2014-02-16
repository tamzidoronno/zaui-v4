<?php
namespace ns_e9d04f19_6eaa_4a17_9b7b_aa387dbaed92;

class CategoryLister extends \WebshopApplication implements \Application {

    var $entries;
    var $currentEntry;

    function __construct() {
        $this->setSkinVariable("height", 100, "The height of the category image");
        $this->setSkinVariable("width", 100, "The width of the category image");        
    }
    

    public function getDescription() {
        return $this->__f("Create categories and add a list of products to the category, attach images to the categories, and name them as you want.");
    }
    
    public function getAvailablePositions() {
        return "middle";
    }
    
    public function applicationAdded() {
        $this->newEntry("Category 1", "d2af2175-e990-416e-ae61-d7f6999d87d3");
        $this->newEntry("Category 2", "feda4884-b151-4521-b321-8c12c29e7598");
        $this->newEntry("Category 3", "caf81fa0-f83a-4a70-807b-e8606b067065");
        $this->newEntry("Category 4", "6cb2ad0d-1441-4f66-90b3-bb6dae4fb78e");
    }
    
    public function getName() {
        return $this->__f("Category list");
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }

    public function getEntries() {
        return $this->entries;
    }
    
    public function render() {
        $this->entries = $this->getApi()->getListManager()->getList($this->getConfiguration()->id);
        $this->includefile("CategoryListerTemplateEntries");
    }
    
    public function deleteEntry() {
        $id = $_POST['data']['id'];
        $this->getApi()->getListManager()->deleteEntry($id, $this->getConfiguration()->id);
    }

    public function renameEntry() {
        if (isset($_POST['data']['renameId'])) {
            $entry = $this->getApi()->getListManager()->getListEntry($_POST['data']['renameId']);
            $entry->name = $_POST['data']['newEntry'];
            $this->getApi()->getListManager()->updateEntry($entry);
        }
    }

    public function newEntry($name = "New entry", $image = "") {
        $entry = $this->getApiObject()->core_listmanager_data_Entry();
        $entry->name = $name;
        $entry->imageId= $image;
        $this->getApi()->getListManager()->addEntry($this->getConfiguration()->id, $entry, $this->getPage()->id);
    }

    public function printEntry($entry) {
        $this->currentEntry = $entry;
        $this->includefile("CategoryListerTemplateEntry");
    }

    public function getCurrentEntry() {
        return $this->currentEntry;
    }

    public function getCurrentMenuEntry() {
        return $this->currentEntry;
    }

    public function removeImage() {
        $id = $_POST['data']['id'];
        $entry = $this->getApi()->getListManager()->getListEntry($id);
        $entry->imageId = "";
        $this->getApi()->getListManager()->updateEntry($entry);
    }

    public function uploadCategoryImage() {
        $tmp_name = $_FILES['files']['tmp_name'];
        $content = file_get_contents($tmp_name[0]);

        if (!$content) {
            die("Failed to fetch file");
        }

        /* @var $fileSaved core_filemanager_answer_FileSaved */
        $entry = $this->getApi()->getListManager()->getListEntry($_POST['id']);
        $entry->imageId = \FileUpload::storeFile($content);
        $this->getApi()->getListManager()->updateEntry($entry);

        $result['imageId'] = $entry->imageId;
        echo json_encode($result);
    }

}

?>
