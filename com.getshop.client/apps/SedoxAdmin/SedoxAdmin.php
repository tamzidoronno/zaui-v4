<?php
namespace ns_1475891a_3154_49f9_a2b4_ed10bfdda1fc;

class SedoxAdmin extends \ns_5278fb21_3c0a_4ea1_b282_be1b76896a4b\SedoxCommon implements \Application {
    private $currentSedoxUser = null;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "SedoxAdmin";
    }

    public function render() {
        $this->includefile("uploadmodal");
        $this->includefile("sedoxadminlist");
    }
    
    public function renderProduct($product=false, $fromList=false) {
        
        if (isset($_POST['data']['justCreatedFileId']) && $_POST['data']['justCreatedFileId']) {
            $_POST['data']['fileId'] = $_POST['data']['justCreatedFileId'];
            $this->fileSelected();
        }
        
        if (!$product) {
            $product = $this->getApi()->getSedoxProductManager()->getProductById($_POST['data']['productId']);
        }
        
        $file = $product;
        
        $time = $this->formatJavaDateToTime($file->rowCreatedDate);
        $date = date("H:i", $time);
        $user = $this->getApi()->getUserManager()->getUserById($file->firstUploadedByUserId);
        $sedoxUserAccount = $this->getApi()->getSedoxProductManager()->getSedoxUserAccountById($file->firstUploadedByUserId);
        $balance = $sedoxUserAccount->creditAccount->balance;
        $seeUserButton = "<div class='sedoxadmin_see_user_button' userid='$user->id' title='Check User'><i class='fa fa-search'></i></div>";
        $impersonateUserButton = "<a href='/impersonate.php?userId=$user->id'><div class='sedoxadmin_impersonate_user_button' userid='$user->id' title='Impersonate'><i class='fa fa-magic'></i></div></a>";


        $status = "";
        if (!$file->started) {

            $status = "<div class='current_status_line'><div class='start_button' gsclick='markProductAsStarted' productid='$file->id' title='Start Tuning'>START</div></div>";
        } else {
            $startedByUser = $this->getApi()->getUserManager()->getUserById($file->startedByUserId);
            $status = "<div class='current_status_line'><b>Started by</b>: $startedByUser->fullName <div gsclick='markProductAsStarted' productid='$file->id' class='stop_button'><i class='fa fa-stop-circle'></i></div></div>";
        }

        $startedByUser = $this->getApi()->getUserManager()->getUserById($file->startedByUserId);
        $finishedByName = $startedByUser ? $startedByUser->fullName : "N/A";

        $innerstatus = "";
        $text = "";

        if (array_key_exists("notifyForCustomer", $file->states)) {
            $innerstatus = "<div class='finished_marker yellow'><i class='fa fa-comment'></i></div>";
            $text = "Notified by";
        } else if (array_key_exists("sendProductByMail", $file->states)) {
            $innerstatus = "<div class='finished_marker'><i class='fa fa-check'></i></div>";
            $text = "Finished by";
        } else if (array_key_exists("purchaseOnlyForCustomer", $file->states)) {
            $innerstatus = "<div class='finished_marker'><i class='fa fa-dollar'></i></div>";
            $text = "Purchased by";
        } else if ($product->isFinished) {
            $text = "Finised by";
            $innerstatus = "<div class='finished_marker'><i class='fa fa-check'></i></div>";
        }

        if ($innerstatus)
            $status = "<div class='current_status_line'><b>$text</b>: $finishedByName $innerstatus</div>";
                    

        echo "<div class='col_row_content' productid='$file->id'>";
            echo "<div class='col_row_content_inner'>";
                echo "<div class='header_content'>";
                    echo "<div class='col_content col1'>$file->id</div>";
                    echo "<div class='col_content col2'>$date</div>";
                    echo "<div class='col_content col3'>$file->printableName</div>";
                    echo "<div class='col_content col4'>$impersonateUserButton $seeUserButton <span class='ownerusername'>$user->fullName</span></div>";
                    echo "<div class='col_content col5'>$balance</div>";
                echo "</div>";
                echo "<div class='admin_extrainfo_row'>";
                echo "<b>Requested:</b> ".$this->getRequestedString($file);
                echo $status;
                echo "</div>";
            echo "</div>";
            $this->setCurrentProduct($file);
            $this->includefile("productview");
        echo "</div>";
        
        if (!$fromList)
            echo "<script>getshop.SedoxDatabankTheme.setAll();</script>";
    }
    
    public function getFilesToProcess() {
        
    }
    
    public function getTotalPages() {
        return 10;
    }
    
    public function uploadFile() {
        $seperator = ";base64,";
        $index = strrpos($_POST['data']['fileBase64'], $seperator)+  strlen($seperator);
        $base64 = substr($_POST['data']['fileBase64'], $index);

        $_SESSION['SEDOX_FILE_ADMIN_UPLOADED'] = $base64;
        $_SESSION['SEDOX_FILE_ADMIN_UPLOADED_FILENAME'] = $_POST['data']['fileName'];
    }
    
    public function deleteFile() {
        $this->getApi()->getSedoxProductManager()->removeBinaryFileFromProduct($_POST['data']['productid'], $_POST['data']['sedox_file_id']);
        $_POST['data']['productId'] = $_POST['data']['productid'];
        $_POST['data']['fileId'] = "";
        $this->fileSelected();
    }
    
    public function purchaseProductOnly() {
        $files = array($_POST['data']['sedox_file_id']);
        $this->getApi()->getSedoxProductManager()->purchaseOnlyForCustomer($_POST['data']['productid'], $files);
    }
    
    public function finalizeFileUpload() {
        $base64 = $_SESSION['SEDOX_FILE_ADMIN_UPLOADED'];
        $filename = $_SESSION['SEDOX_FILE_ADMIN_UPLOADED_FILENAME'];
        $this->getApi()->getSedoxProductManager()->addFileToProduct($base64, $filename, $_POST['data']['type'], $_POST['data']['productid'], $_POST['data']['options']);
    }
    
    public function markRowAsExpanded() {
        if (isset($_SESSION['sedox_admin_row_expanded']) && $_SESSION['sedox_admin_row_expanded'] == $_POST['data']['productid']) {
            unset($_SESSION['sedox_admin_row_expanded']);
        } else {
            $_SESSION['sedox_admin_row_expanded'] = $_POST['data']['productid'];
        }
    }
    
    public function isExpanded($productId) {
        if (isset($_SESSION['sedox_admin_row_expanded']) && $_SESSION['sedox_admin_row_expanded'] == $productId) {
            return true;
        }
        
        return false;
    }
    
    public function toggleProductSalable() {
        $product = $this->getApi()->getSedoxProductManager()->getProductById($_POST['data']['productid']);
        $this->getApi()->getSedoxProductManager()->toggleSaleableProduct($product->id, !$product->saleAble);
    }
    
    public function markProductAsStarted() {
        $product = $this->getApi()->getSedoxProductManager()->getProductById($_POST['data']['productid']);
        $this->getApi()->getSedoxProductManager()->toggleStartStop($product->id, !$product->started);
        $this->markRowAsExpanded();
    }
    
    public function markAsFinished() {
        $this->getApi()->getSedoxProductManager()->markAsFinished($_POST['data']['productid'], true);
    }
    
    public function sendFileByMail() {
        $files = array($_POST['data']['sedox_file_id']);
        $this->getApi()->getSedoxProductManager()->sendProductByMail($_POST['data']['productid'], $_POST['data']['comment'], $files);
    }
    
    public function notifyByEmail() {
        $this->getApi()->getSedoxProductManager()->notifyForCustomer($_POST['data']['productid'], $_POST['data']['comment']);
    }
    
    public function purchaseOrder() {
        $files = array($_POST['data']['sedox_file_id']);
        $this->getApi()->getSedoxProductManager()->purchaseOnlyForCustomer($_POST['data']['productid'], $files);
    }

    /**
     * 
     * @param \core_sedox_SedoxProduct $product
     */
    public function getRequestedString($product) {
        if (count($product->binaryFiles) < 1) {
            return "N/A";
        }
        
        $options = $product->binaryFiles[0]->options;
        
        $requests = ucwords($options->requested_remaptype)." -";
        $requests .= $options->requested_adblue == "1" ? " AD Blue" : "";
        $requests .= $options->requested_decat == "1" ? " Decat" : "";
        $requests .= $options->requested_dpf == "1" ? " DPF" : "";
        $requests .= $options->requested_dtc == "1" ? " DTC" : "";
        $requests .= $options->requested_egr == "1" ? " EGR" : "";
        $requests .= $options->requested_vmax == "1" ? " Vmax" : "";
        
        return $requests;
    }

    public function setInformation() {
        $this->getApi()->getSedoxProductManager()->setExtraInformationForFile($_POST['data']['productId'], $_POST['data']['fileId'], $_POST['data']['info']);
    }
    
    public function setChecksum() {
        $this->getApi()->getSedoxProductManager()->setChecksum($_POST['data']['productId'], $_POST['data']['checksum']);
    }
    
    public function getUserSettingsOrder() {
        return 1;
    }
    
    public function renderUserSettings($user) {
        $this->currentSedoxUser = $user;
        $this->includefile("usersettings");
    }
    
    public function getCurrentUser() {
        return $this->currentSedoxUser;
    }
    
    public function updateUser() {
        $this->getApi()->getSedoxProductManager()->changeDeveloperStatus($_POST['userid'], $_POST['isDeveloper']);
        $this->getApi()->getSedoxProductManager()->setPushoverIdForUser($_POST['pushoverid'], $_POST['userid']);
        $this->getApi()->getSedoxProductManager()->toggleIsNorwegian($_POST['userid'], $_POST['isNorwegian']);
        $this->getApi()->getSedoxProductManager()->togglePassiveSlaveMode($_POST['userid'], $_POST['isPassiveSlave']);
        $this->getApi()->getSedoxProductManager()->toggleBadCustomer($_POST['userid'], $_POST['badCustomer']);
        $this->getApi()->getSedoxProductManager()->toggleAllowWindowsApp($_POST['userid'], $_POST['canUseExternalProgram']);
        $this->getApi()->getSedoxProductManager()->setFixedPrice($_POST['userid'], $_POST['fixedrate']);
        
    }
    
    public function cancelSearch() {
        unset($_SESSION['SEDOX_EDITOR_SEARCHWORD']);
    }
    
    public function gsEmailSetup($model) {
        if (!$model) {
            $this->includefile("emailsettings");
            return;
        }
        
        $this->setConfigurationSetting("uploadadminsubject", $_POST['uploadadminsubject']);
        $this->setConfigurationSetting("uploadadminspecialemail", $_POST['uploadadminspecialemail']);
        $this->setConfigurationSetting("uploadadminnospecialemail", $_POST['uploadadminnospecialemail']);
        
        $this->setConfigurationSetting("uploadusersubject", $_POST['uploadusersubject']);
        $this->setConfigurationSetting("uploaduseremail", $_POST['uploaduseremail']);
        
        $this->setConfigurationSetting("purchaseadminsubject", $_POST['purchaseadminsubject']);
        $this->setConfigurationSetting("purchaseadminemail", $_POST['purchaseadminemail']);
        
        $this->setConfigurationSetting("filereadyusernoattachmentemail", $_POST['filereadyusernoattachmentemail']);
        $this->setConfigurationSetting("filereadyuserattachmentemail", $_POST['filereadyuserattachmentemail']);
        
        $this->setConfigurationSetting("sentToDifferentEmail", $_POST['sentToDifferentEmail']);
        
        $this->setConfigurationSetting("signature", $_POST['signature']);
    }
    
    public function setType() {
        $this->getApi()->getSedoxProductManager()->setType($_POST['data']['productid'], $_POST['data']['value']);
    }
    
    public function sendProductToDifferentEmail() {
        $this->getApi()->getSedoxProductManager()->sendProductToDifferentEmail($_POST['data']['productId'], $_POST['data']['email'], [$_POST['data']['fileId']],  $_POST['data']['comment']);
    }

    public function getExtraInfo($binFile) {
        $extraRequests = "";
                    
        if($binFile->options->requested_adblue) {
            $extraRequests .= "AdBlue, ";
        }
        if($binFile->options->requested_decat) {
            $extraRequests .= "Decat, ";
        }
        if($binFile->options->requested_dpf) {
            $extraRequests .= "DPF, ";
        }
        if($binFile->options->requested_dtc) {
            $extraRequests .= "DTC, ";
        }
        if($binFile->options->requested_egr) {
            $extraRequests .= "EGR, ";
        }
        if($binFile->options->requested_vmax) {
            $extraRequests .= "Vmax, ";
        }
        if($extraRequests != "") {
            $extraRequests = substr($extraRequests, 0, -2);
        }

        return $extraRequests;
    }

}
?>
