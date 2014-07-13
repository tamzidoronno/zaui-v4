<?php

/*
 * The main purposes of this program is to be able to let 
 * customers upload a tuningfile and add it to the database.
 * 
 * The program is created by GetShop on behalf of Sedox Performance AS
 */

/**
 * Description of SedoxFileUpload
 *
 * @author ktonder
 */
namespace ns_a2172f9b_c911_4d9a_9361_89b57bc01d40;

class SedoxFileUpload extends \ApplicationBase implements \Application {
    
    public function getDescription() {
        return $this->__f("Uploading files");
    }

    public function getName() {
        return $this->__f("Sedox FileUploader");
    }

    private function getExistingProduct() {
        if (isset($_FILES['originalfile'])) {
            $sum  = md5_file($_FILES["originalfile"]["tmp_name"]);
            $product = $this->getApi()->getSedoxProductManager()->getSedoxProductByMd5Sum($sum);
            return $product;
        }
        return null;
    }

    public function saveCarDetails() {
        $binaryFileRead = fread(fopen($_FILES["originalfile"]["tmp_name"], "r"), filesize($_FILES["originalfile"]["tmp_name"]));
        $filename = $_FILES['originalfile']['name'];
        $filecontent = base64_encode($binaryFileRead);
        
        $sedoxProduct = new \core_sedox_SedoxProduct();
        $sedoxProduct->brand = $_POST['brand'];
        $sedoxProduct->model = $_POST['model'];
        $sedoxProduct->engineSize = $_POST['enginesize'];
        $sedoxProduct->power = $_POST['power'];
        $sedoxProduct->year = $_POST['year'];
        $sedoxProduct->tool = $_POST['tool'];
        $sedoxProduct->comment = $_POST['comments'];
        $sedoxProduct->gearType = isset($_POST['automaticgear']) && $_POST['automaticgear'] == "on" ? "auto" : "man";
        $sedoxProduct->useCreditAccount = isset($_POST['usecredit']) && $_POST['usecredit'] == "on" ? true : false;
        
        $slave = isset( $_POST['partnerselect']) ? $_POST['partnerselect'] : null;
        $this->getApi()->getSedoxProductManager()->createSedoxProduct($sedoxProduct, $filecontent, $filename, $slave);
        $_SESSION['fileuploaded'] = true;
    }
    
    public function sendSpecialRequest() {
        $desc = $_POST['data']['desc'];
        $this->getApi()->getSedoxProductManager()->requestSpecialFile($_SESSION['originalfile_match'], $desc);
        $_SESSION['fileuploaded'] = true;   
    }
    
    public function render() {
        if (!isset($_FILES['originalfile'])) {
            $this->includefile("uploadform");
            return;
        }
        
        $product = $this->getExistingProduct();
        if ($product) {
            $this->includefile("filematch");
        } else {
            $this->saveCarDetails();
            $this->includefile("finished");
        }
    }
    
    public function getSlaves() {
        $userId = $this->getUser()->id;
        return $this->getApi()->getSedoxProductManager()->getSlaves($userId);
    }
}
?>