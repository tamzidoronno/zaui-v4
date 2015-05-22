<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

namespace ns_59b96c13_e34b_44ea_9552_cb5247ce1ea3;

class ProMeisterIntrest extends \ApplicationBase implements \Application {
    
    public function getDescription() {
        return "";
    }

    public function getName() {
        return "ProMeisterIntrest";
    }

    public function render() {
        if (!isset($_SESSION['ProMeisterIntrest_courseListEntryId']))  {
            $this->includefile("courses");
        } elseif (!isset($_SESSION['ProMeisterIntrest_selectedGroup'])) {
            $this->includefile("groups");  
        } else {
            $this->includefile("contact");
        }
    }
    
    public function isInvoiceEmailActivated() {
        return true;
    }
    
    public function getOrgNumberLength() {
        return 10;
    }

    public function isConnectedToCurrentPage() {
        return false;
    }
    
    public function isExtraDep() {
        if (!isset($_SESSION['ProMeisterIntrest_selectedGroup'] )) {
            return false;
        }
        if ($_SESSION['ProMeisterIntrest_selectedGroup']  == "1cdd1d93-6d1b-4db3-8e91-3c30cfe38a4a") {
            return true;
        }

        if ($_SESSION['ProMeisterIntrest_selectedGroup']  == "ddcdcab9-dedf-42e1-a093-667f1f091311" || $_SESSION['ProMeisterIntrest_selectedGroup']  == "608c2f52-8d1a-4708-84bb-f6ecba67c2fb") {
            return true;
        }

        return false;
    }
    
    public function getTextForExtraDep() {
        if (!isset($_SESSION['ProMeisterIntrest_selectedGroup'] )) {
            return "";
        }
        if ($_SESSION['ProMeisterIntrest_selectedGroup']  == "1cdd1d93-6d1b-4db3-8e91-3c30cfe38a4a") {
            return "Meko-Id";
        }

        if ($_SESSION['ProMeisterIntrest_selectedGroup']  == "ddcdcab9-dedf-42e1-a093-667f1f091311" || $_SESSION['ProMeisterIntrest_selectedGroup']  == "608c2f52-8d1a-4708-84bb-f6ecba67c2fb") {
            return "Kundnummer";
        }

        return "";
    }
    
    public function getCheckType() {
        if ($_SESSION['ProMeisterIntrest_selectedGroup']  == "1cdd1d93-6d1b-4db3-8e91-3c30cfe38a4a") {
            return 1;
        }

        if ($_SESSION['ProMeisterIntrest_selectedGroup']  == "ddcdcab9-dedf-42e1-a093-667f1f091311" || $_SESSION['ProMeisterIntrest_selectedGroup']  == "608c2f52-8d1a-4708-84bb-f6ecba67c2fb") {
            return 2;
        }

        return 0;
    }
    
    public function courseSelected() {
        $_SESSION['ProMeisterIntrest_courseListEntryId'] = $_POST['data']['entryId'];
    }
    
    public function groupSelected() {
        $_SESSION['ProMeisterIntrest_selectedGroup'] = $_POST['data']['groupid'];
    }
    
    private function clear() {
        unset($_SESSION['ProMeisterIntrest_selectedGroup']);
        unset($_SESSION['ProMeisterIntrest_courseListEntryId']);
    }
    
    public function send() {
        $entry = $this->getApi()->getListManager()->getListEntry($_SESSION['ProMeisterIntrest_courseListEntryId']);
//        $company = $this->getApi()->getUtilManager()->getCompanyFromBrReg($_POST['data']['vatNumber']);
        $company = $this->getApi()->getUtilManager()->getCompanyFree($_POST['data']['vatNumber']);
        
        $subject = "Intresseanmälen";
        $content = "<br/>Event: " . $entry->name;
        $content .= "<br/>";
        if ($company) {
            $content .= "<br/><b>Company</b>";
            $content .= "<br/>Company name: ".$company->name;
            $content .= "<br/>VatNumber: ".$company->vatNumber;
            $content .= "<br/>Address: ".$company->streetAddress;
            $content .= "<br/>Post code: ".$company->postnumber;
            $content .= "<br/>City: ".$company->city;
        }
        
        $content .= "<br/>";
        $content .= "<br/>Deltagarens namn: ".$_POST['data']['name'];
        $content .= "<br/>Deltagarens e-post: ".$_POST['data']['email'];
        $content .= "<br/>Verkstadens e-post: ".$_POST['data']['invoiceemail'];
        $content .= "<br/>Deltagarens mobiltelefon: ".$_POST['data']['cellphone'];
        $content .= "<br/>Kundnummer: ".$_POST['data']['extradep'];
        
        
        $to = $this->getEmail();

        $this->getApi()->getMessageManager()->sendMail($to, "ProMeister", $subject, $content, "noreply@getshop.com", "GetShop");
        $this->clear();
    }
    
    public function getEmail() {
        if(isset($this->getFactory()->getStoreConfiguration()->emailAdress)) {
            return $this->getFactory()->getStoreConfiguration()->emailAdress;
        }
        return "";
    }
    
    public function findCompanies() {
        $name = $_POST['data']['name'];
        $result = $this->getApi()->getUtilManager()->getCompaniesFromBrReg($name);
        if (!is_array($result) || sizeof($result) == 0) {
            return;
        }

        echo "<table width='100%'>";
        foreach ($result as $company) {
            $orgnr = $company->vatNumber;
            $name = $company->name;
            echo "<tr orgnr='$orgnr' class='company_selection'>";
            echo "<td>" . $orgnr . "</td>";
            echo "<td class='selected_name'>" . $name . "</td>";
            echo "<td><span class='gs_button small select_searched_company'>" . $this->__w("select") . "</span></td>";
            echo "</tr>";
        }
        echo "</table>";
    }
    
    public function getCompanyInformation() {
        $this->includefile('company');
    }
}
