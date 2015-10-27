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
            return 110;
        }
        
        if (strtolower($binFile->fileType) == "cmdencrypted") {
            return 0;
        }
	
        if (strtolower($binFile->fileType) == "cmd original") {
            return 0;
        }

        return 100;
    }

    public function saveModifiedFile() {
        $seperator = ";base64,";
        $index = strrpos($_POST['data']['fileBase64'], $seperator)+  strlen($seperator);
        $base64 = substr($_POST['data']['fileBase64'], $index);
        $this->getApi()->getSedoxProductManager()->addFileToProduct($base64, $_POST['data']['fileName'], $_POST['data']['fileType'], $_SESSION['sedox_current_productid']);
    }

    public function purchaseProduct() {
        $files = isset($_POST['data']['files']) ? $_POST['data']['files'] : null;
        $this->getApi()->getSedoxProductManager()->purchaseProduct($_SESSION['sedox_current_productid'], $files);
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
        $files = isset($_POST['data']['files']) ? $_POST['data']['files'] : null;
        $this->getApi()->getSedoxProductManager()->purchaseOnlyForCustomer($_POST['data']['productId'], $files);
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
    
    public function getUploadedSedoxUser($sedoxProduct) {
        $user = $this->getApi()->getSedoxProductManager()->getSedoxUserAccountById($sedoxProduct->firstUploadedByUserId);
        return $user;
    }
    
    public function getUploadedUserName($sedoxProduct) {
        $user = $this->getApi()->getUserManager()->getUserById($sedoxProduct->firstUploadedByUserId);
        return $user;
    }
    
    public function setAddInformation() {
        $productId = $_POST['data']['productid'];
        $fileId = $_POST['data']['fileid'];
        $text = $_POST['data']['text'];
        $this->getApi()->getSedoxProductManager()->setExtraInformationForFile($productId, $fileId, $text);
    }
    
    public function showAddInformation() {
        $productId = $_POST['data']['productid'];
        $fileid = $_POST['data']['fileid'];
        $text = $this->getApi()->getSedoxProductManager()->getExtraInformationForFile($productId, $fileid);
        
        echo "<center>";
        echo "Extra information:";
        echo "<br><input id='extrafileinformationbox' type='textfield' placeholder='$text'/>";
        echo "<br><div fileid='$fileid' productid='$productId' class='gs_button saveextrainfo'>Save</div>";
        echo "<div class='checkbuttons' style='text-align: left; width: 230px;'><br><input type='radio' name='info' value='Tuning with DPF removal'/>Tuning with DPF removal";
        echo "<br><input type='radio' name='info' value='Power with DPF removal'/>Power with DPF removal";
        echo "<br><input type='radio' name='info' value='Tuning with EGR removal'/>Tuning with EGR removal";
        echo "<br><input type='radio' name='info' value='Power with EGR removal'/>Power with EGR removal";
        echo "<br><input type='radio' name='info' value='Tuning with DPF and EGR removal'/>Tuning with DPF and EGR removal";
        echo "<br><input type='radio' name='info' value='Power with DPF and EGR removal'/>Power with DPF and EGR removal";
        echo "<br><input type='radio' name='info' value='DPF removal only'/>DPF removal only";
        echo "<br><input type='radio' name='info' value='EGR removal only'/>EGR removal only";
        echo "<br><input type='radio' name='info' value='DPF and EGR removal only'/>DPF and EGR removal only</div>";
        echo "</center>";
    }
    
    public function toggleSaleableProduct() {
		$productId = $_POST['data']['productId'];
		$saleable = $_POST['data']['saleAble'];
		$this->getApi()->getSedoxProductManager()->toggleSaleableProduct($productId, $saleable);
    }
	
    public static function getReference($sedoxProduct) {
            if (!\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()) {
                    return null;
            }

            if (!isset($sedoxProduct->reference->{\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->id})) {
                    return null;
            }

            return $sedoxProduct->reference->{\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->id};
    }

    public function showUserInformation() {
        echo "<div class='SedoxAdmin'>";
        $sedoxAdmin = new \ns_e22e25dd_8000_471c_89a3_6927d932165e\SedoxAdmin();
        $sedoxAdmin->showUserInformation();
        echo "</div>";

    }

    public function getPrice() {
        $files = isset($_POST['data']['files']) ? $_POST['data']['files'] : null;
        $res = $this->getApi()->getSedoxProductManager()->getPriceForProduct($_POST['data']['productId'], $files);
        if ($res == -1) {
            echo "N/A";
        } else if($res < 0) {
            echo "0";
        } else {
            echo $res;
        }
    }
    
    public function toggleStartStop() {
        $productId = $_POST['data']['productId'];
        $this->getApi()->getSedoxProductManager()->toggleStartStop($productId, $_POST['data']['started'] == "0");
    }
    
    public function markAsFinished() {
        $this->getApi()->getSedoxProductManager()->markAsFinished($_POST['data']['productId'], true);
    }
}
?>