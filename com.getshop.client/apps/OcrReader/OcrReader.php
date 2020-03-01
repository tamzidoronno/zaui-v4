<?php
namespace ns_36de3b7e_e29e_4327_b3f5_7206febf4909;

class OcrReader extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "OcrReader";
    }

    public function render() {
        $this->includefile("paymentdownload");
        $this->includefile("ocrreaderpanel");
    }

    public function retryConnectUnmatched() {
        $this->getApi()->getStoreOcrManager()->retryMatchOrders();
    }
    
    public function setAccountingId() {
        $id = $_POST['data']['id'];
        $password = $_POST['data']['password'];
        $this->getApi()->getStoreOcrManager()->setAccountId($id, $password);
    }
    
    public function disconnectAccountingId() {
        $password = $_POST['data']['prompt'];
        $this->getApi()->getStoreOcrManager()->disconnectAccountId($password);
    }
    
    public function createTransactionFile() {
        $result = $this->getApi()->getGetShopAccountingManager()->createBankTransferFile();
        echo $result;
    }
    
    public function setupocr() {
        $id = $_POST['data']['avtaleid'];
        $password = $_POST['data']['password'];
        $this->getApi()->getStoreOcrManager()->setAccountId($id, $password);
        
        $app = $this->getApi()->getStoreApplicationPool()->getApplication("70ace3f0-3981-11e3-aa6e-0800200c9a66");
        $invoiceapp = $this->getFactory()->getApplicationPool()->createInstace($app);
        $invoiceapp->setConfigurationSetting("kidSize", $_POST['data']['kidsize']);
        $invoiceapp->setConfigurationSetting("defaultKidMethod", "orderid");
    }
}
?>
