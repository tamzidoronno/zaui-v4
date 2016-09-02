<?php
namespace ns_eaaf0123_f479_4b0f_839f_1948c9b927ef;

class NewsFeed extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "NewsFeed";
    }

    public function render() {
        $this->includefile("news");
    }
}
?>
