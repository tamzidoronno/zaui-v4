<?php
namespace ns_3e0e8a59_bb0c_467c_8198_2eceaa1fcc60;

class IntrumInkasso extends \PaymentApplication implements \Application {
    /* @var $order core_ordermanager_data_Order */
    var $order;
    var $intriumClientNumber = "12345";
    var $intriumDepartment = "01";
    
    public function getDescription() {
        return "Get a deal with Intrum and send the invoices to the debt collector with a click of a button.";
    }

    public function getName() {
        return "IntrumInkasso";
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
        
        $totalLines = $innledning . "\r\n" . $client . "\r\n" . $debtor . "\r\n" . $invoice;
        echo $totalLines;
        return true;
    }

    public function createIntroductionLine() {
        $line = "00";
        $line .= $this->prependSpaces("", 3,22);
        $line .= $this->prependSpaces("", 23,122);
        return $line;
    }

    public function prependSpaces($current, $start, $stop) {
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
        return $text . $current;
    }

    public function createClient() {
        $line = "01";
        $line .= $this->prependSpaces($this->intriumClientNumber, 3, 7);
        $line .= $this->prependSpaces($this->intriumDepartment, 8, 10);
        $line .= $this->prependSpaces($this->order->incrementOrderId, 11, 40);
        return $line;
    }

    public function createDeptor() {
        $user = $this->getApi()->getUserManager()->getUserById($this->order->userId);
        
        $line = "01";
        $line .= $this->prependSpaces($user->customerId, 3, 32);
        $line .= $this->prependSpaces($user->fullName, 33, 82);
        if(!$user->address || !$user->address || !$user->address->postCode || !$user->address->city) {
            echo "Address is incorrect, please correct the address, city, address, postcode needs to be filled in correctly.";
            exit(0);
        }
        $line .= $this->prependSpaces($user->address->address, 83, 112);
        $line .= $this->prependSpaces($user->address->address2, 113, 142);
        $line .= $this->prependSpaces("", 143, 202); //not in use
        $line .= $this->prependSpaces($user->address->postCode, 203, 207); 
        $countrycode = $user->address->countrycode;
        if(!$countrycode) {
            $countrycode = "NO";
        }
        $line .= $this->prependSpaces($user->address->city, 208, 227); 
        $line .= $this->prependSpaces($countrycode, 228, 229); 
        
        
        $orgid = "";
        if(isset($user->companyObject->vatNumber)) {
            $orgid = $user->companyObject->vatNumber;
        }
        $length = 9;
        if(!$orgid) {
            $orgid = $user->birthDay;
            $length = 11;
        }
        if(!$orgid || !ctype_digit($orgid) || strlen($orgid) != $length) {
            echo "Invalid organisation number or personal number: " . $orgid . " " . strlen($orgid) . " of " . $length . " in total length.";
            exit(0);
        }
        
        $line .= $this->prependSpaces($orgid, 230, 241);
        $line .= $this->prependSpaces($user->cellPhone, 242, 261);
        $line .= $this->prependSpaces("", 262, 281);
        $line .= $this->prependSpaces($user->cellPhone, 282, 301);
        $line .= $this->prependSpaces($user->emailAddress, 302, 351);
        $line .= $this->prependSpaces("", 352,352);
        $line .= $this->prependSpaces("", 353,359);
        $line .= $this->prependSpaces("", 360,389);
        $line .= $this->prependSpaces("", 390,889);
        return $line;
    }

    public function createInvoice() {
        $line = "03";
        $line .= $this->prependSpaces("", 3, 72);
        $line .= $this->prependSpaces(date("Ymd", strtotime($this->order->rowCreatedDate)), 73, 80);
        $line .= $this->prependSpaces(date("Ymd", strtotime($this->order->dueDate)), 81, 88);
        $line .= $this->prependSpaces("", 89, 90);
        $line .= $this->prependSpaces("", 91, 92);
        $line .= $this->prependSpaces($this->order->incrementOrderId, 93, 112);
        $line .= $this->prependSpaces("", 113, 118);
        $line .= $this->prependSpaces("", 119,126);
        $line .= $this->prependSpaces($this->getApi()->getOrderManager()->getTotalAmount($this->order), 127,138);
        $line .= $this->prependSpaces("", 139,148);
        $line .= $this->prependSpaces($this->getApi()->getOrderManager()->getTotalAmount($this->order), 149,160);
        $line .= $this->prependSpaces("", 161,168);
        $line .= $this->prependSpaces("", 169,176);
        $line .= $this->prependSpaces("", 177,186);
        $line .= $this->prependSpaces("", 187,197);
        $line .= $this->prependSpaces("", 198,388);
        $line .= $this->prependSpaces("", 389,396);
        return $line;
    }

}
?>
