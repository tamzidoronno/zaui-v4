<?php
namespace ns_5532e18a_3e3d_4804_8ded_30bbb33e5bd5;

class SalesPointCashPoints extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SalesPointCashPoints";
    }

    public function render() {
        $this->includefile("cashpoints");
    }
    
    public function createNewCashPoint() {
        $this->getApi()->getPosManager()->createCashPoint($_POST['data']['name']);
    }
    
    public function PosManager_getCashPoints() {
        $this->includefile("configcashpoint");
    }
    
    public function updateNameOnCashPoint() {
        echo "TEST";
    }
    
    public function saveConfig() {
        $cashPoint = $this->getApi()->getPosManager()->getCashPoint($_POST['data']['id']);
        $cashPoint->productListIds = $_POST['data']['productlistsids'];
        $cashPoint->receiptPrinterGdsDeviceId = $_POST['data']['receiptprinter'];
        $cashPoint->kitchenPrinterGdsDeviceId = $_POST['data']['kitchenprinter'];
        $cashPoint->departmentId = $_POST['data']['departmentId'];
        $cashPoint->isMaster = $_POST['data']['isMaster'];
        $cashPoint->warehouseid = $_POST['data']['warehouseid'];
        $cashPoint->cashPointName = $_POST['data']['cashPointName'];
        
        $this->getApi()->getPosManager()->saveCashPoint($cashPoint);
    }
    
    public function activateExternal() {
        echo "OK";
        $this->getApi()->getPosManager()->toggleExternalAccess($_POST['data']['id']);
        
    }
}
?>
