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
        $this->sendMail($_POST['data']['confirmationEmail']);
    }
    
    public function getBrReg() {
        $company = $this->getApi()->getUtilManager()->getCompanyFromBrReg($_POST['data']['number']);
        echo json_encode($company);
        die();
    }
    
    public function downloadPdf() {
        $_SESSION['lasgruppen_pdf_data'] = json_encode($_POST);
        
        if (isset($_POST['data']['page4']['emailCopy']) && $_POST['data']['page4']['emailCopy']) {
            $this->sendMail($_POST['data']['page4']['emailCopy']);
        }
    }
    
    private function sendMail($mailAddress) {
        $address = LasGruppenOrderSchema::$url."/scripts/generatePdfLasgruppen.php?id=" . session_id();
        session_write_close();
        $content = $this->getApi()->getUtilManager()->getBase64EncodedPDFWebPage($address);
        $attachments = [];
        $attachments['bestilling.pdf'] = htmlentities($content);
        $this->getApi()->getMessageManager()->sendMailWithAttachments($mailAddress, $mailAddress, "Bestilling fra Certego", "", "Certego", "No_replay@certego.no", $attachments);
    }

}
