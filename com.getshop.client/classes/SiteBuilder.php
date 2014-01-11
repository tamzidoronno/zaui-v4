<?php

class SiteBuilder extends ApplicationBase {
    /*  @var $api GetShopApi */

    var $api;

    function __construct() {
        $this->api = $this->getApi();
    }

    public function addContactForm($where) {
        $appConfig = $this->api->getPageManager()->addApplicationToPage($this->getPage()->id, "96de3d91-41f2-4236-a469-cd1015b233fc", $where);
    }
    
    public function addImageDisplayer($imageId, $where) {
        $appConfig = $this->api->getPageManager()->addApplicationToPage($this->getPage()->id, "831647b5-6a63-4c46-a3a3-1b4a7c36710a", $where);
        $app = new ns_831647b5_6a63_4c46_a3a3_1b4a7c36710a\ImageDisplayer();
        $app->setConfiguration($appConfig);
        $app->attachImageIdToApp($imageId);
    }
    
    public function addContentManager($content, $where = "middle") {
        $appConfig = $this->api->getPageManager()->addApplicationToPage($this->getPage()->id, "320ada5b-a53a-46d2-99b2-9b0b26a7105a", $where);
        if ($content) {
            $this->api->getContentManager()->saveContent($appConfig->id, $content);
        } else {
            $app = new ns_320ada5b_a53a_46d2_99b2_9b0b26a7105a\ContentManager();
            $app->setConfiguration($appConfig);
            $app->applicationAdded();
        }
        return $appConfig;
    }
    
    public function clearPage() {
        $this->getApi()->getPageManager()->clearPage($this->getPage()->id);
    }
}
?>
