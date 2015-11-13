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

    public function doesProductExists() {
        $product = $this->getExistingProduct();
        if ($product != null) {
            return;
        }
        echo "false";
    }

    public function getExistingProduct() {
        if (isset($_POST['data']['md5'])) {
            $product = $this->getApi()->getSedoxProductManager()->getSedoxProductByMd5Sum($_POST['data']['md5']);
            return $product;
        }

        if (isset($_FILES['originalfile'])) {
            $sum = md5_file($_FILES["originalfile"]["tmp_name"]);
            $product = $this->getApi()->getSedoxProductManager()->getSedoxProductByMd5Sum($sum);
            return $product;
        }
        return null;
    }

    private function codeToMessage($code) {
        switch ($code) {
            case UPLOAD_ERR_INI_SIZE:
                $message = "The uploaded file exceeds the upload_max_filesize directive in php.ini";
                break;
            case UPLOAD_ERR_FORM_SIZE:
                $message = "The uploaded file exceeds the MAX_FILE_SIZE directive that was specified in the HTML form";
                break;
            case UPLOAD_ERR_PARTIAL:
                $message = "The uploaded file was only partially uploaded";
                break;
            case UPLOAD_ERR_NO_FILE:
                $message = "No file was uploaded";
                break;
            case UPLOAD_ERR_NO_TMP_DIR:
                $message = "Missing a temporary folder";
                break;
            case UPLOAD_ERR_CANT_WRITE:
                $message = "Failed to write file to disk";
                break;
            case UPLOAD_ERR_EXTENSION:
                $message = "File upload stopped by extension";
                break;

            default:
                $message = "Unknown upload error";
                break;
        }
        return $message;
    }

    public function saveCarDetails() {
        $fileResource = fopen($_FILES["originalfile"]["tmp_name"], "r");
        $fileSize = filesize($_FILES["originalfile"]["tmp_name"]);

        $binaryFileRead = fread($fileResource, $fileSize);
        $filename = $_FILES['originalfile']['name'];
        $filecontent = base64_encode($binaryFileRead);

        $slave = isset($_POST['partnerselect']) ? $_POST['partnerselect'] : null;
        $useCredit = isset($_POST['usecredit']) && $_POST['usecredit'] == "on" ? true : false;
        $geartype = isset($_POST['automaticgear']) && $_POST['automaticgear'] == "on" ? "auto" : "man";
        $this->saveFileContent($_POST['brand'], $_POST['model'], $_POST['enginesize'], $_POST['power'], $_POST['year'], $_POST['tool'], $_POST['comments'], $geartype, $useCredit, $slave, $filename, $filecontent);
        $_SESSION['fileuploaded'] = true;
    }

    public function saveFileContent($brand, $model, $engineSize, $power, $year, $tool, $comment, $geartype, $useCredit, $slave, $filename, $filecontent, $origin = "WebPage") {
        $sedoxProduct = new \core_sedox_SedoxProduct();
        $sedoxProduct->brand = $brand;
        $sedoxProduct->model = $model;
        $sedoxProduct->engineSize = $engineSize;
        $sedoxProduct->power = $power;
        $sedoxProduct->year = $year;
        $sedoxProduct->tool = $tool;
        $sedoxProduct->gearType = $geartype;
        
        $savedProduct = $this->getApi()->getSedoxProductManager()->createSedoxProduct($sedoxProduct, $filecontent, $filename, $slave, $origin, $comment, $useCredit);
		if (isset($_POST['reference']) && $_POST['reference'] != "") {
			$this->getApi()->getSedoxProductManager()->addReference($savedProduct->id, $_POST['reference']);
		}
    }

    public function sendSpecialRequest() {
        $desc = $_POST['data']['desc'];
        $this->getApi()->getSedoxProductManager()->requestSpecialFile($_POST['data']['fileId'], $desc);
        $_SESSION['fileuploaded'] = true;
    }

    public function render() {
        $product = $this->getExistingProduct();
        if ($product) {
            $this->includefile("filematch");
            return;
        }

        if (!isset($_SESSION['fileuploaded'])) {
            if (!isset($_FILES['originalfile'])) {
                $this->includefile("uploadform");
                return;
            }

            $errorCode = $_FILES['originalfile']['error'];
            if ($errorCode) {
                $message = $this->codeToMessage($errorCode);
                throw new \Exception($message);
            }

            $this->saveCarDetails();
            $this->includefile("finished");
            unset($_SESSION['fileuploaded']);
        } else {
            $this->includefile("finished");
            unset($_SESSION['fileuploaded']);
        }
    }

    public function getSlaves() {
        $userId = $this->getUser()->id;
        return $this->getApi()->getSedoxProductManager()->getSlaves($userId);
    }
    
    public function renderConfig() {
        $this->includeFile("config");
    }

}

?>