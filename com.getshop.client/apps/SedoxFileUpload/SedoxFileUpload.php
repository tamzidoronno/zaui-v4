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
        
        $slave = isset( $_POST['partnerselect']) ? $_POST['partnerselect'] : null;
        $useCredit = isset($_POST['usecredit']) && $_POST['usecredit'] == "on" ? true : false;
        $geartype = isset($_POST['automaticgear']) && $_POST['automaticgear'] == "on" ? "auto" : "man";
        $this->saveFileContent($_POST['brand'], $_POST['model'], $_POST['enginesize'], $_POST['power'], $_POST['year'], $_POST['tool'], $_POST['comments'], $geartype, $useCredit, $slave, $filename, $filecontent);
        $_SESSION['fileuploaded'] = true;
    }
    
    public function saveFileContent($brand, $model, $engineSize, $power, $year, $tool, $comment, $geartype, $useCredit, $slave, $filename, $filecontent) {
        $sedoxProduct = new \core_sedox_SedoxProduct();
        $sedoxProduct->brand = $brand;
        $sedoxProduct->model = $model;
        $sedoxProduct->engineSize = $engineSize;
        $sedoxProduct->power = $power;
        $sedoxProduct->year = $year;
        $sedoxProduct->tool = $tool;
        $sedoxProduct->comment = $comment;
        $sedoxProduct->gearType = $geartype;
        $sedoxProduct->useCreditAccount = $useCredit;
        
        $this->getApi()->getSedoxProductManager()->createSedoxProduct($sedoxProduct, $filecontent, $filename, $slave);    
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