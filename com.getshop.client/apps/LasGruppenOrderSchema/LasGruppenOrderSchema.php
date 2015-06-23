<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of LasGruppenOrderSchema
 *
 * @author ktonder
 */

namespace ns_7004f275_a10f_4857_8255_843c2c7fb3ab;

class LasGruppenOrderSchema extends \ApplicationBase implements \Application {
    public static $url = "http://20192.getshop.com";
    
    public function getDescription() {
        return "TEST";
    }

    public function getName() {
        return $this->__f("LasGruppen Ordering Schema");
    }

    public function render() {
        $this->includefile("orderingschema");
    }
    
    public function sendConfirmation() {
        if (isset($_POST['data']['confirmationEmail']) && $_POST['data']['confirmationEmail']) {
            $attachments = $this->getAttachments();
            $this->sendMail($_POST['data']['confirmationEmail'], $attachments);
        }
    }
    
    public function getBrReg() {
        $company = $this->getApi()->getUtilManager()->getCompanyFromBrReg($_POST['data']['number']);
        echo json_encode($company);
        die();
    }
    
    public function downloadPdf() {
        $_SESSION['lasgruppen_pdf_data'] = json_encode($_POST);
        $attachments = $this->getAttachments();
        
        if ($_POST['data']['page4']['securitytype'] != "signature") {
            $this->sendMail("system@certego.no", $attachments);
        }
        
        if (isset($_POST['data']['page4']['emailCopy']) && $_POST['data']['page4']['emailCopy']) {
            $this->sendMail($_POST['data']['page4']['emailCopy'], $attachments);
        }
    }
    
    private function getAttachments() {
        $address = LasGruppenOrderSchema::$url."/scripts/generatePdfLasgruppen.php?id=" . session_id();
        $sessionId = session_id();
        session_write_close();
        $content = $this->getApi()->getUtilManager()->getBase64EncodedPDFWebPage($address);
        
        session_id($sessionId);
        session_start();
        session_id($sessionId);
        
        $attachments = [];
        $attachments['bestilling.pdf'] = $content;
        return $attachments;
    }
    
    private function sendMail($mailAddress, $attachments) {
        $this->getApi()->getMessageManager()->sendMailWithAttachments($mailAddress, $mailAddress, "Bestilling fra Certego", "", "certego@getshop.com", "Certego AS", $attachments);
    }
    
    public function doLogin() {
        $user = $this->getApi()->getUserManager()->checkUserNameAndPassword($_POST['data']['username'], $_POST['data']['password']);
        if ($user) {
            echo "success";
        } else {
            echo "failed";
        }
    }
    
    public function requestPincode() {
        $success = $this->getApi()->getUserManager()->requestNewPincode($_POST['data']['username'], $_POST['data']['password']);
        if ($success) {
            echo "success";
        } else {
            echo "failed";
        }
    }
    
    public function loginWithPincode() {
        $userLoggedIn = $this->getApi()->getUserManager()->loginWithPincode($_POST['data']['username'], $_POST['data']['password'], $_POST['data']['pincode']);
        
        if ($userLoggedIn != null && isset($userLoggedIn)) {
            unset($_SESSION['tempaddress']);
            $_SESSION['loggedin'] = serialize($userLoggedIn);
            echo "success";
        } else {
            echo "failed";
        }
    }
    
    public function logout() {
        $this->getApi()->getUserManager()->logout();
        session_destroy();
    }
}