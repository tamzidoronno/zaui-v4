<?php
namespace ns_32c15c30_d665_11e2_8b8b_0800200c9a66;

use WebshopApplication;
use \Application;

class RealEstate extends WebshopApplication implements Application {
    
    public function getName() {
        return $this->__f("Real estate");        
    }
    
    public function getDescription() {
        echo $this->__f("Need to display your realestates? well, here is the application that allows you to do that, simply add the application to your page and start editing your realestate");
    }
    
    public function render() {
        $this->includefile('realestate');
    }
    
    private function saveContentInternal($settingskey) {
        if ($_POST['data']['altid'] === $settingskey) {
            $this->setConfigurationSetting($settingskey, $_POST['data']['content']);
        }
    }
    
    public function saveContent() {
        $this->saveContentInternal("title");
        $this->saveContentInternal("shortdesc");
        $this->saveContentInternal("address");
        $this->saveContentInternal("parkingspots");
        $this->saveContentInternal("ownership");
        $this->saveContentInternal("arealsize");
        $this->saveContentInternal("rental");
        $this->saveContentInternal("longitude");
        $this->saveContentInternal("latitude");
        
        if($this->getLongitude() && $this->getLatitude()) {
            $this->saveGoogleMapsData();
        }
        
    }
    
    private function getSettingTranslated($key, $defaultext=false) {
        $defaultext = !$defaultext ? "click to change" : $defaultext;
        return $this->getConfigurationSetting($key)  ? $this->getConfigurationSetting($key) : $defaultext;
    }
    
    public function getAddress() {
        return $this->getSettingTranslated("address");
    }
    
    public function getParkingspots() {
        return $this->getSettingTranslated("parkingspots");
    }
    
    public function getOwnerRelations() {
        return $this->getSettingTranslated("ownership");
    }
    
    public function getSize() {
        return $this->getSettingTranslated("arealsize");
    }
    
    public function getRental()  {
        return $this->getSettingTranslated("rental");
    }
    
    public function getTitle() {
        return $this->getSettingTranslated("title", "The name of the realestate, click to change");
    }
    
    public function getShortDescription() {
        return $this->getSettingTranslated("shortdesc", "A short description of the realestate, click to change");
    }
    
    public function getLongitude() {
        return $this->getSettingTranslated("longitude");
    }
    
    public function getLatitude() {
        return $this->getSettingTranslated("latitude");
    }
    
    public function uploadImage() {
        $content = $_POST['data']['data'];
        $content = base64_decode(str_replace("data:image/png;base64,", "",$content));
        $imgId = \FileUpload::storeFile($content);
        $this->setConfigurationSetting("image", $imgId);
    }
    
    public function deleteImage() {
        $this->setConfigurationSetting("image", "");
    }

    public function saveGoogleMapsData() {
        $data = array();
        $data['longitude'] = $this->getLongitude();
        $data['latitude'] = $this->getLatitude();
        $desc = substr($this->getShortDescription(), 0, 100);
        $readmore = "<a href='?page=".$this->getPage()->id."'>".$this->__w("Les mer")."</a>";
        $address = $this->getAddress();
        $data['description'] = "<b>" . $address . "</b><br>" .$desc . "<br>" .$readmore;
        $data['title'] = $this->getTitle() . ", "  . $address;
        $data['id'] = $this->getConfiguration()->id;
        
        $mapsCords = json_decode($this->getFactory()->getConfigurationFlag("googlemaps"));
        if(!@$mapsCords) {
            $mapsCords = array();
        }
        
        $newRes = array();
        foreach($mapsCords as $cord) {
            if($cord->id != $this->getConfiguration()->id) {
                $newRes[] = $cord;
            }
        }
        
        $newRes[] = $data;
        $this->getFactory()->setConfigurationFlag("googlemaps", json_encode($newRes));
    }
}
?>