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


}
?>
