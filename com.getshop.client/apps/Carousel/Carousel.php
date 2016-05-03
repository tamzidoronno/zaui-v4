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
        $slideId = $_POST['data']['slideId'];
        
        $fileId = \FileUpload::storeFile(base64_decode($base64));
        
        $this->getApi()->getBannerManager()->setImageForSlide($slideId, $fileId);
    }
    
    public function addSlide() {
        $slideIds = $this->getConfigurationSetting("slideids");
        $slideIdsArray = array_filter(explode("|", $slideIds));
        
        if(count($slideIdsArray) != 10) {
            $slideId = $this->getApi()->getBannerManager()->addSlide();
            
            $this->setConfigurationSetting("slideids", $slideIds . ($slideIds != "" ? "|" : "") . $slideId);
            
            echo "<div class='slide_image' slide_id='" . $slideId . "'>";
            echo "<div class='gss_button delete_slide'><i class='fa fa-trash-o'></i></div>";
            echo " Background: ";
            echo "<div class='background_select'>";
            echo "<input type='file' name='slideimage'>";
            echo "</div>";
            echo "</div>";
        } else {
            echo "<div style='color: red;'>";
            echo " Maximum slide number reached!";
            echo "</div>";
        }
    }
    
    public function deleteSlide() {
        $slideId = $_POST['data']['slideId'];
        $this->getApi()->getBannerManager()->deleteSlide($slideId);
        $slideIds = explode("|", $this->getConfigurationSetting("slideids"));
        
        unset($slideIds[array_search($slideId, $slideIds)]);
        
        $slideIdsToSave = "";
        foreach($slideIds as $slideId) {
            $slideIdsToSave = $slideIdsToSave . ($slideIdsToSave != "" ? "|" : "") . $slideId;
        }
        
        $this->setConfigurationSetting("slideids", $slideIdsToSave);
    }
    
    public function applicationAdded() {
        $this->setConfigurationSetting("carouselheight", "500");
        $this->setConfigurationSetting("autoslide", "true");
        $this->setConfigurationSetting("slidedirection", "right");
        $this->setConfigurationSetting("slidespeed", "3000");
        $this->setConfigurationSetting("showbullets", "true");
        $this->setConfigurationSetting("showarrows", "true");
        $this->setConfigurationSetting("arrowvertical", "50");
        $this->setConfigurationSetting("arrowhorizontal", "2");
    }
}
?>
