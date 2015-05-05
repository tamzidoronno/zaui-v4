<?php

namespace ns_831647b5_6a63_4c46_a3a3_1b4a7c36710a;

class ImageDisplayer extends \ApplicationBase implements \Application {

    var $entries;
    var $dept;
    var $currentMenuEntry;

    function __construct() {
        
    }

    public function getDescription() {
        return $this->__w("Upload and manipulate on images or add this to be able to search for professional images trough our image stock.");
    }

    public function getAvailablePositions() {
        return "left";
    }

    public function getName() {
        return $this->__w("Image");
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
        $prefix = $this->getPrefix();
        if (isset($_POST['data']['textFields'])) {
            $jsonString = json_encode($_POST['data']['textFields']);
            $this->setConfigurationSetting("texts".$prefix,$jsonString);
        } else {
            $this->setConfigurationSetting("texts".$prefix,null);
        }
    }
    
    public function updateCordinates() {
        $this->saveTexts();
        if (isset($_POST['data']['imageId'])) {
            $this->attachImageIdToApp($_POST['data']['imageId']);
        }
        $prefix = $this->getPrefix();
        $c = $_POST['data']['cords'];
        $serialized = $c[0] . ":" . $c[2] . ":" . $c[1] . ":" . $c[3];
        $this->setConfigurationSetting("rotation".$prefix, $_POST['data']['rotation']);
        $this->setConfigurationSetting("image_cords".$prefix, $serialized);
        $this->setConfigurationSetting("compression".$prefix, $_POST['data']['compression']);
        $this->setConfigurationSetting("new_type".$prefix, "1");
        $this->setConfigurationSetting("link".$prefix, $_POST['data']['link']);
        $this->setConfigurationSetting("adjustment".$prefix, $_POST['data']['adjustment']);
        $this->setConfigurationSetting("originalSize".$prefix, $_POST['data']['originalSize']);
        if($_POST['data']['originalSize'] == "true") {
            $this->setConfigurationSetting("compression".$prefix, "");
        }
        if(!$this->getConfigurationSetting("zoom_able")) {
            $this->setConfigurationSetting("zoom_able", "false");
        }
    }

    public function saveOriginalImage() {        
        $this->saveTexts();
        $prefix = $this->getPrefix();
        $content = base64_decode(str_replace("data:image/png;base64,", "", $_POST['data']['data']));
        $imgId = \FileUpload::storeFile($content);
        $this->attachImageIdToApp($imgId);
        $c = $_POST['data']['cords'];
        $serialized = $c[0] . ":" . $c[2] . ":" . $c[1] . ":" . $c[3];
        $this->setConfigurationSetting("new_type".$prefix, "1");
        $this->setConfigurationSetting("image_cords".$prefix, $serialized);
        $this->setConfigurationSetting("compression".$prefix, $_POST['data']['compression']);
        $this->setConfigurationSetting("rotation".$prefix, $_POST['data']['rotation']);
        $this->setConfigurationSetting("link".$prefix, $_POST['data']['link']);
        $this->setConfigurationSetting("adjustment".$prefix, $_POST['data']['adjustment']);
        $this->setConfigurationSetting("originalSize".$prefix, $_POST['data']['originalSize']);
        if($_POST['data']['originalSize'] == "true") {
            $this->setConfigurationSetting("compression".$prefix, "");
        }
        if(!$this->getConfigurationSetting("zoom_able")) {
            $this->setConfigurationSetting("zoom_able", "false");
        }
        echo $imgId;
    }
    
    public function attachImageIdToApp($id) {
        $prefix = $this->getPrefix();
        $this->setConfigurationSetting("original_image".$prefix, $id);
        $this->setConfigurationSetting("image".$prefix, $id);
    }
    
    private function deleteImage() {
        $prefix = $this->getPrefix();
        $this->setConfigurationSetting("original_image".$prefix, null);
        $this->setConfigurationSetting("image".$prefix, null);
        $this->setConfigurationSetting("texts".$prefix, null);
    }
    
    public function removeImage() {
        $this->getApi()->getPageManager()->removeApplication($this->getConfiguration()->id, $this->getPage()->id);
    }
    
    public function getPrefix() {
        $prefix = "";
        if($this->getFactory()->getMainLanguage() != $this->getFactory()->getSelectedTranslation()) {
            $prefix = "_" . $this->getFactory()->getSelectedTranslation();
        }
        return $prefix;
    }
    
    public function showImageEditor() {
        $this->includefile("imageeditor");
    }

    public function render() {
        if (!$this->getOriginalImageId() && $this->hasWriteAccess()) {
            $this->includefile("uploadimage");
        }
        $this->getImageId();
        $this->includefile("ImageDisplayer");
    }
    
    public function getImageId() {
        return $this->getTranslatedConfigurationSettings("image");
    }
    
    public function getOriginalImageId() {
        return $this->getTranslatedConfigurationSettings("original_image");
    }

    public function getTranslatedConfigurationSettings($name) {
        $prefix = $this->getPrefix();
        
        if($this->getConfigurationSetting($name.$prefix)) {
            return $this->getConfigurationSetting($name.$prefix);
        }
        return $this->getConfigurationSetting($name);
    }
    
    public function getTexts() {
        $textFields = $this->getTranslatedConfigurationSettings("texts");
 
        if (!$textFields) {
            $textFields = "[]";
        }
        
        return $textFields;
    }
    
    public function isZoomAble() {
        $zoomable = $this->getConfigurationSetting("zoom_able");
        
        if (!$zoomable) {
            return true;
        }
        
        return $zoomable === "true";
    }
    
    public function toggleZoomImage() {
        $value = $this->isZoomAble() ? "false" : "true";
        $this->setConfigurationSetting("zoom_able", $value);
    }
}
?>