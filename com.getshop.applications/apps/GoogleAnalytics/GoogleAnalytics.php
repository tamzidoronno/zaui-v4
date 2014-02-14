<?php
namespace ns_0cf21aa0_5a46_41c0_b5a6_fd52fb90216f;

class GoogleAnalytics extends \ReportingApplication implements \Application {
    public $singleton = true; 
    
    public function getDescription() {
        return $this->__("Google analytics is by far the best reporting tool for webadministrator, by adding this application you will enable your googla analytics account to communicate with your webshop.");
    }

    public function getName() {
        return $this->__("Google Analytics");
    }

    public function postProcess() {
        
    }
    
    public function getYoutubeId() {
        return "5fYo4QkBx1c";
    }

    public function preProcess() {
        
    }
    
    public function renderConfig() {
        $this->includefile("googleanalyticsconfig");
    }

    public function getCode() {
        $settings = $this->getConfiguration()->settings;
        if ($settings->code)
            return $settings->code->value;
        
        return "";
    }
    
    public function render() {
        $this->includefile("analytics");
    }

}

?>
