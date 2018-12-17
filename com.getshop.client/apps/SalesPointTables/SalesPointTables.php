<?php
namespace ns_300e2528_8518_411b_b343_28ad83eced77;

class SalesPointTables extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SalesPointTables";
    }

    public function render() {
        $this->includefile("configuretables");
    }
    
    public function createTable() {
        $this->getApi()->getPosManager()->createNewTable($_POST['data']['name'], $_POST['data']['tablenumber']);
    }
    
    public function deleteTable() {
        $this->getApi()->getPosManager()->deleteTable($_POST['data']['tableid']);
    }
    
    public function editTable() {
        $_SESSION['ns_300e2528_8518_411b_b343_28ad83eced77_tableid'] = $_POST['data']['tableid'];
    }
}
?>
