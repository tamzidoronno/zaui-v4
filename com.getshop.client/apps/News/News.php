<?php
namespace ns_dabb8a85_f593_43ec_bf0d_240467118a40;

class News extends \MarketingApplication implements \Application {
    public $singleton = true;
    var $entries;
    var $noNewsFound;
    
    public function getDescription() {
        return $this->__("A news application makes you push news to your WebShop. And share to subscribers and push to facebook if the Facebook application has been added.");
    }

    
    public function getAvailablePositions() {
        return "middle";
    }
    
    public function getName() {
        return $this->__("News");
    }

    public function postProcess() {
    }

    public function preProcess() {
    }

    public function render() {
        if ($this->hasWriteAccess()) {
            $this->includefile("addnewnews");
        } 
        $this->includefile("filter");
        $this->includefile("NewsTemplate");
    }
    
    public function loadFilteredNews() {
        $this->includefile("NewsTemplate");
    }
    
    /**
     * @return \app_newsmanager_data_NewsEntry[]
     */
    public function getAllEntries() {
        $res = $this->getApi()->getNewsManager()->getAllNews($this->getNewsListId());
        $this->noNewsFound = false;
        if(isset($_POST['event']) && $_POST['event'] == "loadFilteredNews") {
            $type = $_POST['data']['type'];
            $offset = $_POST['data']['offset'];
            $res = $this->filterNews($type, $offset, $res);
        }
        if(sizeof($res) == 0) {
            $this->noNewsFound = true;
        }
        return $res;
    }
    
    public function getNewsListId() {
        return $this->getConfigurationSetting("newslistid");
    }

    public function getStarted() {
        echo $this->__f("Get started, add this application and start adding news and updates to your page.");
    }

    public function addEntry() {
        $image = $this->getImageDisplayer();
        $news = new \app_newsmanager_data_NewsEntry();
        $news->subject = $_POST['data']['subject'];
        $news->content = $_POST['data']['text'];
        $news->content = nl2br($news->content);
        $news->image = $image->getImageId();
        $newsId = $this->getApi()->getNewsManager()->addNews($news, $this->getNewsListId());
        $this->clearAutoSavedText();
        $image->deleteImage();
    }
    
    public function removeEntry() {
        $this->getApi()->getNewsManager()->deleteNews($_POST['data']['id']);
    }
    
    public function publishEntry() {
        $this->getApi()->getNewsManager()->publishNews($_POST['data']['id']);
    }

    /**
     * @return \ns_831647b5_6a63_4c46_a3a3_1b4a7c36710a\ImageDisplayer
     */
    public function getImageDisplayer() {
        $imgInstance = $this->getConfigurationSetting("imgapp");
        if(!$imgInstance) {
            $newInstance = $this->getFactory()->getApplicationPool()->createNewInstance("831647b5-6a63-4c46-a3a3-1b4a7c36710a");
            $this->setConfigurationSetting("imgapp", $newInstance->id);
            $imgInstance = $newInstance->id;
        }
        $res = $this->getFactory()->getApplicationPool()->getApplicationInstance($imgInstance);
        return $res;
    }

    public function getTemplateNumber() {
        $number = $this->getConfigurationSetting("template");
        if(!$number) {
            $number = 1;
        }
        return $number;
    }

    /**
     * 
     * @param type $type
     * @param type $offset
     * @param \app_newsmanager_data_NewsEntry[] $news
     * @return type
     */
    public function filterNews($type, $offset, $news) {
        if($offset == 0) {
            return $news;
        }
        $res = array();
        if($type == "monthly") {
            foreach($news as $new) {
                if(date("m.Y", strtotime($new->rowCreatedDate)) == $_POST['data']['newsdate']) {
                    $res[] = $new;
                }
            }
        } else {
            foreach($news as $new) {
                if(date("Y", strtotime($new->rowCreatedDate)) == $_POST['data']['newsdate']) {
                    $res[] = $new;
                }
            }
        }
        
        return $res;
    }

}
?>