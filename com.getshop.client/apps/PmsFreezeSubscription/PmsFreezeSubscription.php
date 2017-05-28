<?php
namespace ns_83f7da63_84d2_4b24_b53a_276bf0fbb910;

class PmsFreezeSubscription extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsFreezeSubscription";
    }
       
    public function selectEngine() {
        $this->setConfigurationSetting("selectedkey", $_POST['data']['name']);
    }
    public function getSelectedName() {
        return $this->getConfigurationSetting("selectedkey");
    }
    
    public function freezeSubscription() {
        $endDate = strtotime("01.".$_POST['data']['freezeSubscription']);
        $endDate = strtotime("+1month", $endDate);
        $this->getApi()->getPmsManager()->freezeSubscription($this->getSelectedName(), $_POST['data']['id'], $this->convertToJavaDate($endDate));
    }
    
    public function render() {
        if(!$this->getSelectedName()) {
            echo "Please specify a booking engine first. <br>";
            $engns = $this->getApi()->getStoreManager()->getMultiLevelNames();

            foreach($engns as $engine) {
                echo '<div gstype="clicksubmit" style="font-size: 16px; cursor:pointer; margin-top: 10px;" method="selectEngine" gsname="name" gsvalue="'.$engine.'">' . $engine . "</div>";
            }
            return;
        }
        $this->includefile("listsubscriptionstoprint");
    }
}
?>
