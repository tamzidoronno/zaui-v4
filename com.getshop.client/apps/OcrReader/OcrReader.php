<?php
namespace ns_36de3b7e_e29e_4327_b3f5_7206febf4909;

class OcrReader extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "OcrReader";
    }

    public function render() {
        $this->includefile("ocrreaderpanel");
    }

    public function setAccountingId() {
        $id = $_POST['data']['id'];
        $password = $_POST['data']['password'];
        $this->getApi()->getStoreOcrManager()->setAccountId($id, $password);
    }
}
?>
