<?php
namespace ns_fab79388_f1d7_4fcc_8ca9_d32208e85491;

class AuksjonenFeedback extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "AuksjonenFeedback";
    }
    
    public function saveform() {
        $saved = $this->getConfigurationSetting("savedform");
        if(!$saved) {
            $saved = array();
        } else {
            $saved = json_decode($saved, true);
        }
        foreach($_POST['data'] as $key => $value) {
            if($key == "comment") {
                continue;
            }
            $count = (int)$value;
            if($count > 0) {
                $counter = 0;
                if(isset($saved[$key])) {
                    $counter = (int)$saved[$key];
                }
                $counter += $count;
                $saved[$key] = $counter;
            }
        }
        
        if(!isset($saved['comments'])) {
            $saved['comments'] = array();
        }
        if($_POST['data']['comment']) {
            $saved['comments'][time()] = $_POST['data']['comment'];
        }
        
        
        $this->startAdminImpersonation("StoreApplicationPool", "setSetting");
        $this->setConfigurationSetting("savedform", json_encode($saved));
        $this->stopImpersionation();
    }

    public function render() {
        if(isset($_POST['event']) && $_POST['event'] == "saveform") {
            echo "<h1>Takk for din tilbakemelding</h1>";
        } else {
            $this->includefile("questions");
            if($this->isEditorMode()) {
                $this->includefile("result");
            }
        }
    }
    
    public function requestAdminRights() {
        $this->requestAdminRight("StoreApplicationPool", "setSetting", $this->__o("Need to update configurationskeys."));
    }
}
?>
