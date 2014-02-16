<?php

namespace ns_ee96fe89_e09d_4f7b_921a_3272462e9b7e;

class Airgram extends \ReportingApplication implements \Application {
    var $entries;
    var $dept;
    var $currentMenuEntry;
    
    function __construct() {
    }

    public function getDescription() {
        return $this->__f("Airgram allows you to push important notifications to your phone, use this application to push notifications to your cell phone when something important is happening, for example if someone is starting to chat with you, you will be instantly notified and can responde accordingly. Just download airgram from google play or app store and start using it.");
    }

    public function getAvailablePositions() {
        return "left";
    }
    
    public function getName() {
        return "Airgram";
    }

    public function postProcess() {
        
    }
    
   /**
     * 
     * Should display you configuration page.
     */
    public function renderConfig() {
        $this->includefile("airgramconfig");
    }    

    public function preProcess() {
        
    }
    
    public function getStarted() {
    }

    public function render() {
    }
}
?>
