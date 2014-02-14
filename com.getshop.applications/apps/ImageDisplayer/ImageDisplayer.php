<?php

namespace ns_831647b5_6a63_4c46_a3a3_1b4a7c36710a;

class ImageDisplayer extends \ApplicationBase implements \Application {

    var $entries;
    var $dept;
    var $currentMenuEntry;

    function __construct() {
        
    }

    public function getDescription() {
        return $this->__f("Insert images into any application area.");
    }

    public function getAvailablePositions() {
        return "left";
    }

    public function getName() {
        return "ImageDisplayer";
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }

    public function getStarted() {
        
    }
    
    public function imageDeleted() {
        $this->deleteImage();
    }
    
    private function saveTexts() {
        if (isset($_POST['data']['textFields'])) {
            $jsonString = json_encode($_POST['data']['textFields']);
            $this->setConfigurationSetting("texts",$jsonString);
        } else {
            $this->setConfigurationSetting("texts",null);
        }
        
    }
    
    public function updateCordinates() {
        $this->saveTexts();
        if (isset($_POST['data']['imageId'])) {
            $this->attachImageIdToApp($_POST['data']['imageId']);
        }
        
        $c = $_POST['data']['cords'];
        $serialized = $c[0] . ":" . $c[2] . ":" . $c[1] . ":" . $c[3];
        $this->setConfigurationSetting("rotation", $_POST['data']['rotation']);
        $this->setConfigurationSetting("image_cords", $serialized);
        $this->setConfigurationSetting("compression", $_POST['data']['compression']);
        $this->setConfigurationSetting("new_type", "1");
        
    }

    public function saveOriginalImage() {        
        $this->saveTexts();
        
        $content = base64_decode(str_replace("data:image/png;base64,", "", $_POST['data']['data']));
        $imgId = \FileUpload::storeFile($content);
        $this->attachImageIdToApp($imgId);
        $c = $_POST['data']['cords'];
        $serialized = $c[0] . ":" . $c[2] . ":" . $c[1] . ":" . $c[3];
        $this->setConfigurationSetting("new_type", "1");
        $this->setConfigurationSetting("image_cords", $serialized);
        $this->setConfigurationSetting("compression", $_POST['data']['compression']);
        $this->setConfigurationSetting("rotation", $_POST['data']['rotation']);
        echo $imgId;
    }
    
    public function attachImageIdToApp($id) {
        $this->setConfigurationSetting("original_image", $id);
        $this->setConfigurationSetting("image", $id);
    }
    
    private function deleteImage() {
        $this->setConfigurationSetting("original_image", null);
        $this->setConfigurationSetting("image", null);
        $this->setConfigurationSetting("texts", null);
    }
    
    public function removeImage() {
        $this->getApi()->getPageManager()->removeApplication($this->getConfiguration()->id, $this->getPage()->id);
    }
    
    public function showImageEditor() {
        $this->includefile("imageeditor");
    }

    public function render() {
        if (!$this->getConfigurationSetting("original_image")) {
            $this->includefile("uploadimage");
        }
        
        $this->includefile("ImageDisplayer");
    }
    
    public function getTexts() {
        $textFields = $this->getConfigurationSetting("texts");
 
        if (!$textFields) {
            $textFields = "[]";
        }
        
        return $textFields;
    }

}

?>
