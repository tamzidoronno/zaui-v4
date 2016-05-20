<?php
namespace ns_f65ee7f2_134f_413f_82d2_2606ab8440dd;

class NewsElements extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "NewsElements";
    }

    public function render() {
        $type = $this->getConfigurationSetting("type");
        if(!$type) {
            echo "Not spefied a type yet, please specify one";
            return;
        }
        
        if($this->getFactory()->getPage()->javapage->id == "news_template_1") {
            echo $type;
        } else {
            $this->includefile($type);
        }
    }
    
    public function saveNewsContent() {
        $entry = $this->getApi()->getNewsManager()->getNewsForPage($this->getPage()->javapage->id);
        $entry->subject = $_POST['data']['subject'];
        $entry->content = $_POST['data']['content'];
        $entry->date = $this->convertToJavaDate(strtotime($_POST['data']['date']));
        $entry->rowCreatedDate = $this->convertToJavaDate(strtotime($_POST['data']['date']));
        
        $this->getApi()->getNewsManager()->addNews($entry, $entry->newsListId);
    }
    
    public function fileUplaoded($fileId) {
        $entry = $this->getApi()->getNewsManager()->getNewsForPage($_GET['listid']);
        $entry->image = $fileId;
        $this->getApi()->getNewsManager()->addNews($entry);
    }
    
    /**
     * This function is used from the wrapped app
     */
    public function getFileuploadListId() {
        return $this->getPage()->javapage->id;
    }    
    
}
?>
