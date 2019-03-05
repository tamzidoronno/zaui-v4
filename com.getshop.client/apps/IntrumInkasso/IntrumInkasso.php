<?php
namespace ns_3e0e8a59_bb0c_467c_8198_2eceaa1fcc60;

class IntrumInkasso extends \PaymentApplication implements \Application {
    /* @var $order core_ordermanager_data_Order */
    var $order;
    var $intriumClientNumber = "29244";
    var $intriumDepartment = "0";
    
    public function getDescription() {
        return "Get a deal with Lowell and send the invoices to the debt collector with a click of a button.";
    }

    public function getName() {
        return "LowellInkasso";
    }

    public function render() {
        
    }

    public function setOrder($orderId) {
        $this->order = $this->getApi()->getOrderManager()->getOrderByincrementOrderId($orderId);
    }
    
    /**
     * Should display you configuration page.
     */
    public function renderConfig() {
        $this->includefile("intrumconfig");
    }  
    
    public function saveSettings() {
        $this->setConfigurationSetting("customernumber", $_POST['customernumber']);
        $this->setConfigurationSetting("department", $_POST['department']);
    }
    
    public function getAddress() {
        return "file transfer - ftp (intrum)";
    }
    
    public function transfer() {
        $innledning = $this->createIntroductionLine();
        $client = $this->createClient();
        $debtor = $this->createDeptor();
        $invoice = $this->createInvoice();
        $time = time();
        $totalLines = $innledning . "\r\n" . $client . "\r\n" . $debtor . "\r\n" . $invoice. "\r\n99";
        $this->transferToSftp($totalLines, "NewCase_" . $time .".txt");
        return true;
    }

    public function createIntroductionLine() {
        $line = "00";
        $line .= $this->appendSpaces("", 3,22);
        $line .= $this->appendSpaces("", 23,122);
        return $line;
    }

    public function appendSpaces($current, $start, $stop) {
        $numberOfSpacesMax = $stop - $start;
        $numberOfSpacesMax++;
        if(strlen($current) > $numberOfSpacesMax) {
            echo "Field is too large: $current, max size is : $numberOfSpacesMax size in variable: " .strlen($current)."<br>";
            exit(0);
        }
        $numberOfSpaces = $numberOfSpacesMax - strlen($current);
        $text = "";
        for($i = 0; $i < $numberOfSpaces;$i++) {
            $text .= " ";
        }
        return $current . $text;
    }

    public function createClient() {
        $instances = $this->getApi()->getStoreApplicationPool()->getApplication("3e0e8a59-bb0c-467c-8198-2eceaa1fcc60");
        $this->intriumClientNumber = $instances->settings->customernumber->value;
        $this->intriumDepartment = $instances->settings->department->value;
        
        $user = $this->getApi()->getUserManager()->getUserById($this->order->userId);
        $countrycode = $user->address->countrycode;
        if(!$countrycode) {
            $countrycode = "NO";
        }

        
        
        $user = $this->getApi()->getUserManager()->getUserById($this->order->userId);
        $line = "01";
        
        $line .= $this->appendSpaces($this->intriumClientNumber, 3, 7);
        if(stristr($countrycode, "no")) {
            $line .= $this->appendSpaces("1", 8, 10);
        } else {
            $line .= $this->appendSpaces("2", 8, 10);
        }
        $line .= $this->appendSpaces($user->customerId, 11, 40);
        return $line;
    }

    public function createDeptor() {
        $user = $this->getApi()->getUserManager()->getUserById($this->order->userId);
        $countrycode = $user->address->countrycode;
        if(!$countrycode) {
            $countrycode = "NO";
        }
        
        $line = "02";
        $line .= $this->appendSpaces("", 3, 32);
        $line .= $this->appendSpaces($user->fullName, 33, 82);
        if(!$user->address || !$user->address || !$user->address->postCode || !$user->address->city) {
            echo "Address is incorrect, please correct the address, city, address, postcode needs to be filled in correctly.";
            exit(0);
        }
        $line .= $this->appendSpaces($user->address->address, 83, 112);
        $line .= $this->appendSpaces($user->address->address2, 113, 142);
        $line .= $this->appendSpaces("", 143, 202); //not in use
        
        if(stristr($countrycode, "no")) {
            $line .= $this->appendSpaces($user->address->postCode, 203, 207);
            $line .= $this->appendSpaces($user->address->city, 208, 227); 
        } else {
            $line .= $this->appendSpaces("", 203, 207);
            $line .= $this->appendSpaces($user->address->postCode . " " . $user->address->city, 208, 227); 
        }
        $line .= $this->appendSpaces($countrycode, 228, 229); 
        
        
        $orgid = "";
        if(isset($user->companyObject->vatNumber)) {
            $orgid = $user->companyObject->vatNumber;
        }
        $length = 9;
        if(!$orgid) {
            $orgid = $user->birthDay;
            $length = 11;
        }
        $orgid = trim($orgid);
        
        if(stristr($countrycode, "no") && (!$orgid || !ctype_digit($orgid) || strlen($orgid) != $length)) {
            echo "Invalid organisation number or personal number: " . $orgid . " " . strlen($orgid) . " of " . $length . " in total length.";
            exit(0);
        }
        
        $line .= $this->appendSpaces($orgid, 230, 241);
        $line .= $this->appendSpaces($user->cellPhone, 242, 261);
        $line .= $this->appendSpaces("", 262, 281);
        $line .= $this->appendSpaces($user->cellPhone, 282, 301);
        $line .= $this->appendSpaces($user->emailAddress, 302, 351);
        $line .= $this->appendSpaces("", 352,352);
        $line .= $this->appendSpaces("", 353,359);
        $line .= $this->appendSpaces("", 360,389);
        $line .= $this->appendSpaces("", 390,889);
        return $line;
    }

    public function createInvoice() {
        $amount = (int)($this->getApi()->getOrderManager()->getTotalAmount($this->order) * 100);
        $line = "03";
        $line .= $this->appendSpaces("", 3, 72);
        $line .= $this->appendSpaces(date("Ymd", strtotime($this->order->rowCreatedDate)), 73, 80);
        $line .= $this->appendSpaces(date("Ymd", strtotime($this->order->dueDate)), 81, 88);
        $line .= $this->appendSpaces("", 89, 90);
        $line .= $this->appendSpaces("", 91, 92);
        $line .= $this->appendSpaces($this->order->incrementOrderId, 93, 112);
        $line .= $this->appendSpaces("", 113, 118);
        $line .= $this->appendSpaces("", 119,126);
        $line .= $this->appendSpaces($amount, 127,138);
        $line .= $this->appendSpaces("", 139,148);
        $line .= $this->appendSpaces($amount, 149,160);
        $line .= $this->appendSpaces("", 161,168);
        $line .= $this->appendSpaces("", 169,176);
        $line .= $this->appendSpaces("", 177,186);
        $line .= $this->appendSpaces("", 187,197);
        $line .= $this->appendSpaces("", 198,388);
        $line .= $this->appendSpaces("", 389,396);
        return $line;
    }

    public function transferToSftp($totalLines, $csv_filename) {
        
        $strServer = "sftp-test.lowell.no";
        $strServerPort = "22";
        $strServerUsername = "201857";
        $strServerPassword = "9lLR2Z3evWGRe5aF9xrC";

        if($this->getApi()->getStoreManager()->isProductMode()) {
            $strServer = "sftp.lowell.no";
            $strServerPassword = "lCrHc6ElE1qg7qrFtCEo";
        }
        
        //connect to server
        $resConnection = ssh2_connect($strServer, $strServerPort);

        if(ssh2_auth_password($resConnection, $strServerUsername, $strServerPassword)){
            //Initialize SFTP subsystem

            $resSFTP = ssh2_sftp($resConnection);    
            $resFile = fopen('ssh2.sftp://' . intval($resSFTP) ."/in/".$csv_filename, 'w');
            fwrite($resFile, $totalLines);
            fclose($resFile);                   
            echo "sucessfully transferred";

        }else{
            echo "Unable to authenticate on server";
        }
                     
    }

}
?>
