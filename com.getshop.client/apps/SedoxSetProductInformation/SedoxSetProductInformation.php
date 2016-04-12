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
    }
}
?>
