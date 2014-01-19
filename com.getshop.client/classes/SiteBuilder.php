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

    public function addImageDisplayer($imageId, $where, $type = false) {
        if ($type) {
            if(stristr($where, "main_")) {
                $imageId = "10310e92-2184-4fb0-87d3-ba73739d23f7";
            } else {
                $imageId = "8d566b68-6b2c-4816-82e9-56189b9b1c9a";
            }
        }
        $appConfig = $this->api->getPageManager()->addApplicationToPage($this->getPage()->id, "831647b5-6a63-4c46-a3a3-1b4a7c36710a", $where);
        $app = new ns_831647b5_6a63_4c46_a3a3_1b4a7c36710a\ImageDisplayer();
        $app->setConfiguration($appConfig);
        $app->attachImageIdToApp($imageId);
    }

    public function addContentManager($content, $where, $type = false) {
        if ($type) {
            $content = "This is just a test: pagetype: " . $type;
        }
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

    public function addMap($where) {
        $this->api->getPageManager()->addApplicationToPage($this->getPage()->id, "17c48891-6f7a-47a0-849d-b50de9af218f", $where);
    }

    public function addYouTube($movieid, $where, $type) {
        if (!$movieid) {
            $movieid = "mbyzgeee2mg";
        }
        $this->api->getPageManager()->addApplicationToPage($this->getPage()->id, "8e239f3d-2244-471e-a64d-3241b167b7d2", $where);
    }

}

?>
