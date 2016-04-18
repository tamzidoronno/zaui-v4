<?php
namespace ns_6de59d68_05bb_4f25_b7ea_f98e468a5fde;

class SedoxSetProductInformation extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SedoxSetProductInformation";
    }

    public function render() {
        $this->includefile("setinformationmodal");
    }
    
    public function setInformation() {
        $this->getApi()->getSedoxProductManager()->setExtraInformationForFile($_POST['data']['productId'], $_POST['data']['fileId'], $_POST['data']['info']);
        $this->getApi()->getSedoxProductManager()->setSpecialRequestsForFile($_POST['data']['productId'], $_POST['data']['fileId'], 
            filter_var($_POST['data']['dpf'], FILTER_VALIDATE_BOOLEAN), 
            filter_var($_POST['data']['egr'], FILTER_VALIDATE_BOOLEAN), 
            filter_var($_POST['data']['decat'], FILTER_VALIDATE_BOOLEAN), 
            filter_var($_POST['data']['vmax'], FILTER_VALIDATE_BOOLEAN), 
            filter_var($_POST['data']['adblue'], FILTER_VALIDATE_BOOLEAN), 
            filter_var($_POST['data']['dtc'], FILTER_VALIDATE_BOOLEAN)
        );
    }
}
?>
