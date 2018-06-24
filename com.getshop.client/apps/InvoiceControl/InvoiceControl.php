<?php
namespace ns_b3654609_a195_464f_84c4_e083702c0bf2;

class InvoiceControl extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function searchOrders() {
//        echo "TEST";
    }
    
    public function getName() {
        return "InvoiceControl";
    }

    public function render() {
    
        if(!isset($_POST['data']['keyword']) && isset($_SESSION['searchwordused'])) {
            $_POST['data']['keyword'] = $_SESSION['searchwordused'];
            unset($_SESSION['unpaidonly']);
        }
        
        if(isset($_POST['data']['keyword']) && $_POST['data']['keyword']) {
            $_SESSION['searchwordused'] = $_POST['data']['keyword'];
        }
        if(isset($_POST['data']['unpaidonly'])) {
            $_SESSION['unpaidonly'] = $_POST['data']['unpaidonly'] == "true";
        }
        
        $this->includefile("invoice");
    }

    public function markpaid() {
        $date = $this->convertToJavaDate(strtotime($_POST['data']['paymentdate']));
        $this->getApi()->getOrderManager()->markAsPaid($_POST['data']['orderid'], $date, 0.0);
    }
    
    public function getGroupedOrders() {
        if(isset($_POST['data']['keyword']) && $_POST['data']['keyword']) {
            $filterOptions =  new \core_common_FilterOptions();
            $filterOptions->searchWord = $_POST['data']['keyword'];
            $filterOptions->extra = new \stdClass();
            $filterOptions->extra->{'paymenttype'} = "70ace3f0-3981-11e3-aa6e-0800200c9a66";
            $res = $this->getApi()->getOrderManager()->getOrdersFiltered($filterOptions);
            $orders = $res->datas;
        } else {
            $orders = $this->getApi()->getOrderManager()->getAllUnpaidInvoices();
        }
        
        $groupedOrders = new \stdClass();
        
        foreach ($orders as $order) {
            if(isset($_SESSION['unpaidonly']) && $_SESSION['unpaidonly']) {
                if($order->status == 7) {
                    continue;
                }
            }
            if (!isset($groupedOrders->{$order->userId})) {
                $groupedOrders->{$order->userId} = array();
            }
            $groupedOrders->{$order->userId}[] = $order;
        }
        
        return $groupedOrders;
    }

}
?>
