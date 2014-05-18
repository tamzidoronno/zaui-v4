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
            $_SESSION['originalfile'] = "ORIGNAL";
        }
    }
    
    private function isFileUploaded() {
        return isset($_SESSION['originalfile']);
    }
    
    public function saveCarDetails() {
        $sedoxProduct = new \core_sedox_SedoxProduct();
        $sedoxProduct->userBrand = $_POST['data']['brand'];
        $sedoxProduct->userModel = $_POST['data']['model'];
        $sedoxProduct->userBuild = $_POST['data']['enginesize'];
        $sedoxProduct->userPower = $_POST['data']['power'];
        $sedoxProduct->userTool = $_POST['data']['tool'];
        $sedoxProduct->comment = $_POST['data']['comments'];
        $sedoxProduct->gearType = $_POST['data']['automaticgear'] == "true" ? "auto" : "man";
        $sedoxProduct->useCreditAccount = $_POST['data']['usecredit'];
        
        
        $_SESSION['fileuploaded'] = true;
    }
    
    private function isFinished() {
        return isset($_SESSION['fileuploaded']);
    }
    
    private function resetUpload() {
        unset($_SESSION['fileuploaded']);
        unset($_SESSION['originalfile']);
    }
    
    public function render() {
        $this->saveFileToSession();
        if ($this->isFinished()) {
            $this->includefile("finished");
            $this->resetUpload();
        } else if ($this->isFileUploaded()) {
            $this->includefile("cardetails");
        } else {
            $this->includefile("uploadform");
        }
    }
}
?>