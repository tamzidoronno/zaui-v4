<?php
namespace ns_da9f257b_fc83_4cb1_9422_3ee2d7d2bf20;

class Publications extends \MarketingApplication implements \Application {
    var $noPublicationsFound;
    
    public function getDescription() {
        return $this->__("Publication allows adding linking to online publications.");
    }

    public function getName() {
        return "Publications";
    }

    public function render() {
        if (!isset($_SESSION['ns_da9f257b_fc83_4cb1_9422_3ee2d7d2bf20_publications_filelist_id'])) {
            $_SESSION['ns_da9f257b_fc83_4cb1_9422_3ee2d7d2bf20_publications_filelist_id'] = "ns_da9f257b_fc83_4cb1_9422_3ee2d7d2bf20_".uniqid();
        }
        if ($this->hasWriteAccess()) {
            $this->includefile("addNewPublication");
        }
        $this->includefile('publicationList');
    }
    
    public function fileUplaoded($fileId) {
        if (!$_SESSION['ns_da9f257b_fc83_4cb1_9422_3ee2d7d2bf20_publications_filelist']) {
            $_SESSION['ns_da9f257b_fc83_4cb1_9422_3ee2d7d2bf20_publications_filelist'] = array();
        }
        $_SESSION['ns_da9f257b_fc83_4cb1_9422_3ee2d7d2bf20_publications_filelist'][] = $fileId;
    }
    
    public function changeDate() {
        $javaDate = $this->convertToJavaDate(strtotime($_POST['data']['date']));
        $this->getApi()->getNewsManager()->changeDateOfNews($_POST['data']['id'], $javaDate);
    }
    
    public function fileDeleted($fileId) {
        if($key = array_search($fileId, $_SESSION['ns_da9f257b_fc83_4cb1_9422_3ee2d7d2bf20_publications_filelist']) !== false){
            $array = $_SESSION['ns_da9f257b_fc83_4cb1_9422_3ee2d7d2bf20_publications_filelist'];
            $key = array_search($fileId, $array);
            unset($array[$key]);
            $_SESSION['ns_da9f257b_fc83_4cb1_9422_3ee2d7d2bf20_publications_filelist'] = $array;
        }
    }
    
    public function getAllEntries() {
        $res = $this->getApi()->getNewsManager()->getAllNews($this->getpublicationlistid());
        $this->noPublicationsFound = false;
        if(sizeof($res) == 0) {
            $this->noPublicationsFound = true;
        }
        return $res;
    }
    
    public function getpublicationlistid() {
        return "ns_da9f257b_fc83_4cb1_9422_3ee2d7d2bf20_".$this->getConfigurationSetting("publicationlistid");
    }
    
    public function getFileListId() {
        if ($this->getParentWrappedApplication() !== null && method_exists($this->getParentWrappedApplication(), "getFileuploadListId")) {
            return $this->getParentWrappedApplication()->getFileuploadListId();
        }
        return $this->getAppInstanceId();
    }
    
    public function getFileuploadListId() {
       return $_SESSION['ns_da9f257b_fc83_4cb1_9422_3ee2d7d2bf20_publications_filelist_id'];
    }
    
    public function addEntry() {
        $_SESSION['ns_da9f257b_fc83_4cb1_9422_3ee2d7d2bf20_publications_filelist_id'] = null;
        $publications = new \app_newsmanager_data_NewsEntry();
        $publications->authors = ucfirst($_POST['data']['authors']);
        $publications->articleName = $_POST['data']['articleName'];
        $publications->articleLink = $_POST['data']['articleLink'];
        $publications->publisher = $_POST['data']['publisher'];
        $publications->ISSN = $_POST['data']['ISSN'];
        $publications->PMID = $_POST['data']['PMID'];
        
        if ($_SESSION['ns_da9f257b_fc83_4cb1_9422_3ee2d7d2bf20_publications_filelist']) {
            $publications->fileIds = $_SESSION['ns_da9f257b_fc83_4cb1_9422_3ee2d7d2bf20_publications_filelist'];
        }
        
        $publicationId = $this->getApi()->getNewsManager()->addNews($publications, $this->getpublicationlistid());
        $this->clearAutoSavedText();
        
        $_SESSION['ns_da9f257b_fc83_4cb1_9422_3ee2d7d2bf20_publications_filelist'] = array();
    }
    
    public function removeEntry() {
        $this->getApi()->getNewsManager()->deleteNews($_POST['data']['id']);
    }
    
    public function getFilteredYear(){
        if(isset($_POST['data']['year'])){
            $selectedYear = $_POST['data']['year'];
            return $selectedYear;
        }else{
            $selectedYear = date("Y");
            return $selectedYear;
        }
    }
}
?>
