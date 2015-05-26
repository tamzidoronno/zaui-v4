<?php

namespace ns_17c48891_6f7a_47a0_849d_b50de9af218f;

/**
 * Description of LeftMenu
 *
 * @author boggi
 */
class GoogleMaps extends \WebshopApplication implements \Application {
    var $entries;
    var $dept;
    var $currentMenuEntry;
    
    function __construct() {
    }

    public function getDescription() {
        return $this->__w("Add google maps to your page, pin point locatins and restrict it to a given area, and even connect the locations to a given page.");
    }
    
    public function configure() {
        $this->includefile("configuration");
    }

    public function getAvailablePositions() {
        return "left";
    }
    
    public function getName() {
        return "Google Maps";
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }
    
    public function includeExtraJavascript() {
        $array = array();
        return $array;
    }
    
    public function getStarted() {
    }

    public function render() {
        $this->includefile("map");
    }
    
    public function saveConfiguration() {
        if(!$this->isEditorMode()) {
            return;
        }
        $config = $_POST['data']['config'];
        $this->setConfigurationSetting("config", json_encode($config));
    }
    
    public function getGoogleMapsConfiguration() {
        $config = $this->getConfigurationSetting("config");
        $res = new Configuration();
        if($config != null) {
            $res = json_decode($config);
        } else {
            $res = new \stdClass;
            $res->{'zoom'} = 3;
            $res->{'startlongitude'} = 66.31338;
            $res->{'startaltitude'} = 14.14661;
            $res->{'maxZoom'} = 50;
            $res->{'minZoom'} = 0;
        }
        
        if(!isset($res->markers) || !is_array($res->markers)) {
            $res->markers = array();
        }
        
        $mapsCords = json_decode($this->getFactory()->getConfigurationFlag("googlemaps"));
        if(@$mapsCords) {
            foreach($mapsCords as $cord) {
                $res->markers[] = $cord;
            }
        }
        
        return $res;
    }
}
?>
