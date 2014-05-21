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

    private function saveFileToSession() {
        if (isset($_FILES['originalfile'])) {
            $sum  = md5_file($_FILES["originalfile"]["tmp_name"]);
            $product = $this->getApi()->getSedoxProductManager()->getSedoxProductByMd5Sum($sum);
            
            if ($product) {
                $_SESSION['originalfile_match'] = $product->id;
            } else {
                $binaryFileRead = fread(fopen($_FILES["originalfile"]["tmp_name"], "r"), filesize($_FILES["originalfile"]["tmp_name"]));
                $_SESSION['originalfile_filename'] = $_FILES['originalfile']['name'];
                $_SESSION['originalfile_base64'] = base64_encode($binaryFileRead);
            }   
        }
    }
    
    private function isFileUploaded() {
        return isset($_SESSION['originalfile_base64']);
    }
    
    public function saveCarDetails() {
        $sedoxProduct = new \core_sedox_SedoxProduct();
        $sedoxProduct->brand = $_POST['data']['brand'];
        $sedoxProduct->model = $_POST['data']['model'];
        $sedoxProduct->build = $_POST['data']['enginesize'];
        $sedoxProduct->power = $_POST['data']['power'];
        $sedoxProduct->tool = $_POST['data']['tool'];
        $sedoxProduct->comment = $_POST['data']['comments'];
        $sedoxProduct->gearType = $_POST['data']['automaticgear'] == "true" ? "auto" : "man";
        $sedoxProduct->useCreditAccount = $_POST['data']['usecredit'];
        
        $this->getApi()->getSedoxProductManager()->createSedoxProduct($sedoxProduct, $_SESSION['originalfile_base64'], $_SESSION['originalfile_filename']);
        $_SESSION['fileuploaded'] = true;
    }
    
    private function isFinished() {
        return isset($_SESSION['fileuploaded']);
    }
    
    private function isFileMatch() {
        return isset($_SESSION['originalfile_match']);
    }
    
    public function sendSpecialRequest() {
        $desc = $_POST['data']['desc'];
        $this->getApi()->getSedoxProductManager()->requestSpecialFile($_SESSION['originalfile_match'], $desc);
        $_SESSION['fileuploaded'] = true;
        
    }
    
    private function resetUpload() {
        unset($_SESSION['fileuploaded']);
        unset($_SESSION['originalfile_filename']);
        unset($_SESSION['originalfile_base64']);
        unset($_SESSION['originalfile_match']);
    }
    
    public function render() {
        $this->saveFileToSession();
        if ($this->isFinished()) {
            $this->includefile("finished");
            $this->resetUpload();
        } else if ($this->isFileMatch()) {
            $this->includefile("filematch");
        } else if ($this->isFileUploaded()) {
            $this->includefile("cardetails");
        } else {
            $this->includefile("uploadform");
        }
    }
}
?>