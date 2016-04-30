<?php
namespace ns_df607cb0_efb4_44f8_bbac_7bf6fa4bc5b7;

class Carousel extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "Carousel";
    }

    public function render() {
        $this->includefile("carousel");
    }
    
    public function saveCarouselSettings() {
        $this->setConfigurationSetting("carouselheight", $_POST['data']['carouselheight']);
        $this->setConfigurationSetting("autoslide", $_POST['data']['autoslide']);
        $this->setConfigurationSetting("slidedirection", $_POST['data']['slidedirection']);
        $this->setConfigurationSetting("slidespeed", $_POST['data']['slidespeed']);
        $this->setConfigurationSetting("showbullets", $_POST['data']['showbullets']);
        $this->setConfigurationSetting("showarrows", $_POST['data']['showarrows']);
        $this->setConfigurationSetting("arrowhorizontal", $_POST['data']['arrowhorizontal']);
        $this->setConfigurationSetting("arrowvertical", $_POST['data']['arrowvertical']);
    }
    
    public function saveSlideImage() {
        $seperator = ";base64,";
        $index = strrpos($_POST['data']['fileBase64'], $seperator)+  strlen($seperator);
        $base64 = substr($_POST['data']['fileBase64'], $index);
        $slideNumber = $_POST['data']['slideNumber'];
        
        $fileId = \FileUpload::storeFile(base64_decode($base64));
        
        $this->setConfigurationSetting("slideimage" . $slideNumber, $fileId);
        
        echo $fileId;
    }
    
    public function setSlideNumber() {
        if(!$this->getConfigurationSetting("slidenumber") || $this->getConfigurationSetting("slidenumber") == "") {
            $this->setConfigurationSetting("slidenumber", 0);
        }
        
        $currentSlideNumber = $this->getConfigurationSetting("slidenumber");
        
        if($_POST['data']['slideNum'] == "+1" && $currentSlideNumber != 10) {
            $this->setConfigurationSetting("slidenumber", $currentSlideNumber + 1);
            echo "<div class='slide_image' slidenumber='" . $this->getConfigurationSetting("slidenumber") . "'>";
            echo "Slide #" . $this->getConfigurationSetting("slidenumber");
            echo "<div class='value_input'>";
            echo "<input type='file' name='slideimage'>";
            echo "</div>";
            echo "</div>";
        } else if($_POST['data']['slideNum'] == "-1" && $currentSlideNumber != 0) {
            $this->setConfigurationSetting("slidenumber", $currentSlideNumber - 1);
            echo "removeSlider_" . $currentSlideNumber;
        }
    }
}
?>
