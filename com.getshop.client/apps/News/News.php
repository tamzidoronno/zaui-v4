<?php
namespace ns_dabb8a85_f593_43ec_bf0d_240467118a40;

class News extends \MarketingApplication implements \Application {
    public $singleton = true;
    var $entries;
    
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
        $this->includefile("NewsTemplate");
    }
    
    public function getAllEntries() {
        return $this->getApi()->getNewsManager()->getAllNews();
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
        $newsId = $this->getApi()->getNewsManager()->addNews($news);
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

}

?>
