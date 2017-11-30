<?php
namespace ns_01b8bd0c_0375_42fa_a44e_d177485db704;

class ApacLocks extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ApacLocks";
    }
    
    public function ApacLocks_getLockData() {
        echo "HELLO";
    }

    public function render() {
        $servers = $this->getApi()->getGetShopLockSystemManager()->getLockServers();
        foreach ($servers as $server) {
            $args = array();
            
            echo "<h2>".$server->givenName."</h2>";
            
            $attributes = array(
                array("zwaveDeviceId", "DeviceId", 'zwaveDeviceId'),
                array("typeOfLock", "Type", 'typeOfLock'),
                array("name", "Name", 'name'),
                array("maxnumberOfCodes", "MaxCodes", 'maxnumberOfCodes')
            );
            
            $table = new \GetShopModuleTable($this, "ApacLocks", "getLockData", null, $attributes);
            $table->setData($server->locks);
            $table->render();
        }
    }
}
?>
