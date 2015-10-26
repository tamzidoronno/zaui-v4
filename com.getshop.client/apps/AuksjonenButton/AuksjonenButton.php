<?php
namespace ns_91192b8c_19da_465e_89ee_763fcf5d97c3;

class AuksjonenButton extends \WebshopApplication implements \Application {
    public function getDescription() {
        return "Test";
    }

    public function getName() {
        return "AuksjonenButton";
    }

    public function render() {
        $url = $this->getUrl();
        $link = $this->getLink();
        if(!$url || !$link) {
            $this->includefile("configuration");
        } else {
            $this->includefile("view");
        }
    }
    
    public function getUrl() {
        return $this->getConfigurationSetting("url");
    }
    
    public function getLink() {
        return $this->getConfigurationSetting("link");
    }
    
    public function addUrl() {
        $this->setConfigurationSetting("url", $_POST['data']['url']);
        $this->setConfigurationSetting("link", $_POST['data']['link']);
    }
}
?>
