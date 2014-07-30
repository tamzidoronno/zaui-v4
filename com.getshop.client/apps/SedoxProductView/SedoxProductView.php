<?php

namespace ns_23fac58b_5066_4222_860c_a9e88196b8a1;

class SedoxProductView extends \ApplicationBase implements \Application {

    public function getDescription() {
        return "Sedox Product View";
    }

    public function getName() {
        return "Sedox Product View";
    }

    public function render() {
        $this->includefile("productview");
    }

    public function getCurrentProduct() {
        if (isset($_GET['productId'])) {
            $_SESSION['sedox_current_productid'] = $_GET['productId'];
        }

        if (!isset($_SESSION['sedox_current_productid'])) {
            return null;
        }

        return $this->getApi()->getSedoxProductManager()->getProductById($_SESSION['sedox_current_productid']);
    }

    public function getPriceForFile($binFile) {
        if (strtolower($binFile->fileType) == "original") {
            return 40;
        }

        if (strtolower($binFile->fileType) == "eco") {
            return 50;
        }

        if (strtolower($binFile->fileType) == "tune") {
            return 60;
        }

        if (strtolower($binFile->fileType) == "power") {
            return 70;
        }

        if (strtolower($binFile->fileType) == "various") {
            return 160;
        }
        
        if (strtolower($binFile->fileType) == "cmdencrypted") {
            return 0;
        }

        return 100;
    }

    public function saveModifiedFile() {
        $toDecode = str_replace("data:;base64,", "", $_POST['data']['fileBase64']);
        $this->getApi()->getSedoxProductManager()->addFileToProduct($toDecode, $_POST['data']['fileName'], $_POST['data']['fileType'], $_SESSION['sedox_current_productid']);
    }

    public function purchaseProduct() {
        $this->getApi()->getSedoxProductManager()->purchaseProduct($_SESSION['sedox_current_productid'], $_POST['data']['files']);
        $codeSafe = urlencode(base64_encode(implode(":-:", $_POST['data']['files'])));
        echo "filedownload.php?files=" . $codeSafe;
        die();
    }

    /**
     * This function needs to be here in order to
     * be able to reload the product view 
     * after a file has been purchased.
     */
    public function dummy() {
        
    }

    public function setOrginalCheckSum() {
        $this->getApi()->getSedoxProductManager()->setChecksum($_POST['data']['productId'], $_POST['data']['checksum']);
    }

    public function purchaseProductOnly() {
        $this->setOrginalCheckSum();
        $this->getApi()->getSedoxProductManager()->purchaseOnlyForCustomer($_POST['data']['productId'], $_POST['data']['files']);
    }

    public function notifyByEmailAndSms() {
        $this->setOrginalCheckSum();
        $productId = $_POST['data']['productId'];
        $extraText = $_POST['data']['extraInfo'];
        $this->getApi()->getSedoxProductManager()->notifyForCustomer($productId, $extraText);
    }

    public function sendProductByEmail() {
        $this->setOrginalCheckSum();
        $productId = $_POST['data']['productId'];
        $extraText = $_POST['data']['extraInfo'];
        $files = $_POST['data']['files'];
        $this->getApi()->getSedoxProductManager()->sendProductByMail($productId, $extraText, $files);
    }

    public function deleteBinaryFile() {
        $this->getApi()->getSedoxProductManager()->removeBinaryFileFromProduct($_POST['data']['productId'], $_POST['data']['binFileId']);
    }
}
?>