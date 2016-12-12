<?php
namespace ns_17c0563b_a3b6_4519_b7d1_0880005b11e3;

class PmsStopSubscription extends \WebshopApplication implements \Application {
    
    var $failedStop = "";
    
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsStopSubscription";
    }
    
    public function selectEngine() {
        $this->setConfigurationSetting("selectedkey", $_POST['data']['name']);
    }
    
    public function stopsubscription() {
        $end = strtotime($_POST['data']['enddate']);
        $id = $_POST['data']['id'];
        $date = date('Y-m-01', strtotime("+2 month"));
        $futureTime = strtotime($date)-86400;
        if($futureTime > $end) {
            $this->failedStop = "Kan ikke fortsette, minum avslutningsdato er: " . date("d.m.Y", $futureTime+86400);
        } else {
            $this->getApi()->getPmsManager()->endRoom($this->getSelectedName(), $id, $this->convertToJavaDate($end));
        }
    }
     
    public function getSelectedName() {
        return $this->getConfigurationSetting("selectedkey");
    }
    
    public function render() {
        if($this->failedStop) {
            echo $this->failedStop;
        }
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
