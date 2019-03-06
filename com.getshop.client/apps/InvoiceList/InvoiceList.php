<?php
namespace ns_0775b147_b913_43cd_b9f4_a2a721ad3277;

class InvoiceList extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "InvoiceList";
    }

    public function render() {
        $this->includefile("invoicelist");
        $this->includefile('sendboxouter');
    }

    public function getMonth() {
        if (isset($_SESSION['ns_0775b147_b913_43cd_b9f4_a2a721ad3277_month'])) {
            return $_SESSION['ns_0775b147_b913_43cd_b9f4_a2a721ad3277_month'];
        }
        
        return date('m');
    }

    public function getYear() {
        if (isset($_SESSION['ns_0775b147_b913_43cd_b9f4_a2a721ad3277_year'])) {
            return $_SESSION['ns_0775b147_b913_43cd_b9f4_a2a721ad3277_year'];
        }
        return date('Y');
    }
    
    public function getTotalPaidAmount($order) {
        $amountPaid = 0.0;
        
        foreach ($order->orderTransactions as  $trans) {
            $amountPaid += $trans->amount;
        }
        
        return $amountPaid;
        
    }
    
    public function showMonth() {
        $_SESSION['ns_0775b147_b913_43cd_b9f4_a2a721ad3277_month'] = $_POST['data']['month'];
        $_SESSION['ns_0775b147_b913_43cd_b9f4_a2a721ad3277_year'] = $_POST['data']['year'];
    }

    /**
     * 
     * @param \core_ordermanager_data_Order $order
     */
    public function getLastSentDate($order) {
        $logEntryFound = $this->getLastLogEntry($order);
        $icon = "fa-cancel";
        $date = null;
        $type = "";
        
        if ($logEntryFound) {
            if (strtolower($logEntryFound->type) == "ehf") {
                $icon = "fa-asterisk";
            }
            
            if (strtolower($logEntryFound->type) == "email") {
                $icon = "fa-at";
            }
            
            $type = $logEntryFound->type;
            $date = $logEntryFound->date;
        }
        
        $text = $date ? "Sent date: ".date('d.m.Y', strtotime($date))." ( $type ) "  : "Not sent yet";
        $prev = $date ? "<i title='$text' class='fa fa-check'></i>" : "<i title='$text' class='fa fa-warning'></i>";
        $retText = $date ? $prev." (<i class='fa $icon' title='$text'/>)" : $prev;
        return $retText;
    }
    
    public function getLastLogEntry($order) {
        $logs = $order->shipmentLog;
        $highestTime = 0;
        
        $logEntryFound = null;
        foreach ($logs as $log) {
            $time = strtotime($log->date);
            if ($time > $highestTime) {
                $highestTime = $time;
                $logEntryFound = $log;
            }
        }
        
        
        return $logEntryFound;
    }
    
    public function showOrderToSendList() {
        $this->includefile('sendboxresult');
    }

    /**
     * 
     * @param \core_ordermanager_data_Order $order
     */
    public function getDefaultSentType($order) {
        $user = $this->getApi()->getUserManager()->getUserById($order->userId);
        $canUseEhf = false;
        $isCompany = $user->companyObject != null && $user->companyObject->vatNumber;
        $isInvoice = $order->payment && $order->payment->paymentId == "70ace3f0-3981-11e3-aa6e-0800200c9a66";

        if ($isCompany && $isInvoice) {
            $vatnumber = $user->companyObject->vatNumber;
            $vatnumber = str_replace(' ', '', $vatnumber);
            $canUseEhf = $this->getApi()->getGetShop()->canInvoiceOverEhf($vatnumber);
        }
        
        if ($canUseEhf) {
            return "ehf";
        }
        
        return "mail";
    }
    
    public function quickSendInvoice() {
        $order = $this->getApi()->getOrderManager()->getOrder($_POST['data']['orderid']);
        $type = $this->getDefaultSentType($order);
        
        if ($type == "ehf") {
            $orderList = new \ns_9a6ea395_8dc9_4f27_99c5_87ccc6b5793d\EcommerceOrderList();
            $xml = $this->getApi()->getOrderManager()->getEhfXml($order->id);
            
            if ($xml == "failed") {
                echo "Ehf Validation Failed, GetShop has been notified...";
                return;
            }
            
            $res = $orderList->sendDocument($xml);
            
            if ($res[0] != "ok") {
                echo "Something went wrong during sending EHF, please contact GetShop Support.";
                return;
            }

            $this->getApi()->getOrderManager()->registerSentEhf($order->id);
            $this->getApi()->getOrderManager()->closeOrder($order->id, "Invoice sent by EHF to customer.");
            
            echo "ok";
            return;
        }
        
        if ($type == "mail") {
            if (!$_POST['data']['mail'] || !filter_var($_POST['data']['mail'], FILTER_VALIDATE_EMAIL)) {
               echo "Wrong or empty email address";
               return;
            }
            
            $subject = "Invoice attached";
            $body = "Attached is the invoice for your order";
            $this->getApi()->getOrderManager()->sendRecieptWithText($_POST['data']['orderid'], $_POST['data']['mail'], $subject, $body);
            echo "ok";
        }
    }

}
?>
