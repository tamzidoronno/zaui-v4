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
        $news = new \app_newsmanager_data_NewsEntry();
        $news->subject = $_POST['data']['subject'];
        $news->content = $_POST['data']['text'];
        $news->content = nl2br($news->content);
        $newsId = $this->getApi()->getNewsManager()->addNews($news);
        $this->getApi()->getPageManager()->createPageWithId(0, "", $newsId);
       
    }
    
    public function removeEntry() {
        $this->getApi()->getNewsManager()->deleteNews($_POST['data']['id']);
    }
    
    public function publishEntry() {
        $this->getApi()->getNewsManager()->publishNews($_POST['data']['id']);
    }
}

?>
