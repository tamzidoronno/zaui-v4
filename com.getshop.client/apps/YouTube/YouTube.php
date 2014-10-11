<?php
namespace ns_8e239f3d_2244_471e_a64d_3241b167b7d2;

class YouTube extends \ApplicationBase implements \Application {
    var $entries;
    var $dept;
    var $currentMenuEntry;
    
    function __construct() {
    }

    public function getDescription() {
        return $this->__f("Add videos to your page.");
    }

    public function getAvailablePositions() {
        return "left";
    }
    
    public function getName() {
        return "YouTube";
    }
    
    public function search() {
        $this->includefile("searchresult");
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }
     
    /* 
     * @return core_youtubemanager_data_SearchResult[]
     */
    public function searchYoutube($query) {
        return $this->getApi()->getYouTubeManager()->searchYoutube($query);
    }
    
    public function getYoutubeName() {
        if($this->getConfigurationSetting("youtubeid")) {
            return $this->getConfigurationSetting("youtubeid");
        }
        return false;
    }
        
    
    public function getStarted() {
    }
    
    public function setYoutubeId() {
        $id = $_POST['data']['id'];
        $this->setConfigurationSetting("youtubeid", $id);
        if(isset($_POST['data']['height'])) {
            $this->setConfigurationSetting("height", $_POST['data']['height']);
        }
    }
    
    public function getHeight() {
        if($this->getConfigurationSetting("height")) {
            $height = $this->getConfigurationSetting("height");
            if ($height > 0) {
                return $height;
            }
        }
        return "100%";
    }
    
    public function render() {
        $this->includefile("YouTube");
    }
}
?>