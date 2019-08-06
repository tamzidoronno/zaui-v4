<?php
namespace ns_2fa3ce10_3637_43d5_a2c7_8e9152fab41a;

class WareHouseView extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "WareHouseView";
    }

    public function render() {
        if (isset($_GET['warehouseid'])) {
            $_SESSION['ns_2fa3ce10_3637_43d5_a2c7_8e9152fab41a_warehouseid'] = $_GET['warehouseid'];
        }
        $this->includefile("view");
    }
    
    public function deleteLocation() {
        $this->getApi()->getWareHouseManager()->deleteWareHouseLocation($_POST['data']['locationid']);
    }
    
    public function addLocation() {
        $whl = new \core_warehousemanager_WareHouseLocation();
        $whl->locationName = $_POST['data']['locationname'];
        $whl->row = $_POST['data']['row'];
        $whl->column = $_POST['data']['column'];
        $whl->wareHouseId = $_SESSION['ns_2fa3ce10_3637_43d5_a2c7_8e9152fab41a_warehouseid'];
        $this->getApi()->getWareHouseManager()->addWareHouseLocation($whl);
    }
    
    public function updateLocationRow() {
        $location = $this->getApi()->getWareHouseManager()->getWareHouseLocation($_POST['data']['locationid']);
        $location->locationName = $_POST['data']['locationName'];
        $location->column = $_POST['data']['column'];
        $location->row = $_POST['data']['row'];
        $location->barcode = $_POST['data']['barcode'];
        $this->getApi()->getWareHouseManager()->addWareHouseLocation($location);
    }
}
?>
