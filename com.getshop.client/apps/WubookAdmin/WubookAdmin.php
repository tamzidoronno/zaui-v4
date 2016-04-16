<?php
namespace ns_2d93c325_b7eb_4876_8b08_ae771c73f95a;

class WubookAdmin extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "WubookAdmin";
    }
    
    public function setEngine() {
        $engine = $_POST['data']['engine'];
        $this->setConfigurationSetting("engine_name", $engine);
    }
    
    public function getSelectedName() {
        return $this->getConfigurationSetting("engine_name");
    }

    public function render() {
        if (!$this->getSelectedName()) {
            echo "You need to specify a booking engine first<br>";
            $engines = $this->getApi()->getStoreManager()->getMultiLevelNames();
            foreach($engines as $engine) {
                echo "<span gstype='clicksubmit' style='font-size: 20px; cursor:pointer; display:inline-block; margin-bottom: 20px;' method='setEngine' gsname='engine' gsvalue='$engine'>$engine</span><br>"; 
            }
            return;
        }
        $this->includefile("wubookadminpanel");
        $this->includefile("actiondone");
    }
    
    public function doAction() {
        
    }
    
}
?>
