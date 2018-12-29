<?php
namespace ns_4bab2f13_491b_4c34_973c_e776ca2d88d6;

class ApacDoorLog extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ApacDoorLog";
    }

    public function render() {
        if (isset($_SESSION['ns_4bab2f13_491b_4c34_973c_e776ca2d88d6_lockid'])) {
            $this->includefile("lock");
            return;
        }
        
        $this->includefile("selectdoor");
    }
    
    public function selectLock() {
        $_SESSION['ns_4bab2f13_491b_4c34_973c_e776ca2d88d6_serverid'] = $_POST['data']['serverid'];
        $_SESSION['ns_4bab2f13_491b_4c34_973c_e776ca2d88d6_lockid'] = $_POST['data']['lockid'];
    }
    
    public function cancelLockSelection() {
        unset($_SESSION['ns_4bab2f13_491b_4c34_973c_e776ca2d88d6_serverid']);
        unset($_SESSION['ns_4bab2f13_491b_4c34_973c_e776ca2d88d6_lockid']);
    }
    
    public function forceOpen() {
        $this->getApi()->getGetShopLockSystemManager()->openLock($_SESSION['ns_4bab2f13_491b_4c34_973c_e776ca2d88d6_lockid']);
    }
    
    public function forceClose() {
        $this->getApi()->getGetShopLockSystemManager()->closeLock($_SESSION['ns_4bab2f13_491b_4c34_973c_e776ca2d88d6_lockid']);
    }
}
?>
