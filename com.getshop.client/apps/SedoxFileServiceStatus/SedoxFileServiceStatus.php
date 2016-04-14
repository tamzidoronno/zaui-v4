<?php
namespace ns_b9642995_2bb2_474c_b1e8_5c859dad67a1;

class SedoxFileServiceStatus extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SedoxFileServiceStatus";
    }

    public function render() {
        $this->includefile("fileservicestatus");
    }
    
    public function renderConfig() {
        $this->includefile("config");
    }

    public function getTime() {
        return date('H:i');
    }
    
    public function saveStatus() {
        $this->setConfigurationSetting("forced", $_POST['forced']);
    }
    
    public function getStatus() {
        $app = $this->getApi()->getStoreApplicationPool()->getApplication("cf3f46d9-0073-4966-977d-8e202dc5abbb");
        $openingApp = $this->getFactory()->getApplicationPool()->createInstace($app);
        
        $forced = filter_var($this->getConfigurationSetting("forced"), FILTER_VALIDATE_BOOLEAN);
        $colorClass = "red";
        
        if($forced) {
            
        } else if(!$forced) {
            $day = date("l");
            $hour = date("H");
            
            if($day == "Sunday") {
                $limitedOpenTime = explode("-", $openingApp->getConfigurationSetting("sunday_limitedservice"));
                if($hour >= $limitedOpenTime[0] && $hour < $limitedOpenTime[1]) {
                    $colorClass = "yellow";
                }
            } else if ($day == "Saturday") {
                $fullOpenTime = explode("-", $openingApp->getConfigurationSetting("saturday_fullservice"));
                $limitedOpenTime = explode("-", $openingApp->getConfigurationSetting("saturday_limitedservice"));
                if($hour >= $fullOpenTime[0] && $hour < $fullOpenTime[1]) {
                    $colorClass = "green";
                } else if($hour >= $limitedOpenTime[0] && $hour < $limitedOpenTime[1]) {
                    $colorClass = "yellow";
                }
            } else {
                $fullOpenTime = explode("-", $openingApp->getConfigurationSetting("monday_friday_fullservice"));
                $limitedOpenTime = explode("-", $openingApp->getConfigurationSetting("monday_friday_limitedservice"));
                if($hour >= $fullOpenTime[0] && $hour < $fullOpenTime[1]) {
                    $colorClass = "green";
                } else if($hour >= $limitedOpenTime[0] && $hour < $limitedOpenTime[1]) {
                    $colorClass = "yellow";
                }
            }
        }
        return $colorClass;
    }
}
?>
