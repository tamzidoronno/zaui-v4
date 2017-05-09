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
        $this->includefile("invoice");
    }

    public function markpaid() {
        $date = $this->convertToJavaDate(strtotime($_POST['data']['paymentdate']));
        $this->getApi()->getOrderManager()->markAsPaid($_POST['data']['orderid'], $date);
    }
    
    public function getGroupedOrders() {
        if(isset($_POST['data']['keyword']) && $_POST['data']['keyword']) {
            $filterOptions =  new \core_common_FilterOptions();
            $filterOptions->searchWord = $_POST['data']['keyword'];
            $res = $this->getApi()->getOrderManager()->getOrdersFiltered($filterOptions);
            $orders = $res->datas;
        } else {
            $orders = $this->getApi()->getOrderManager()->getAllUnpaidInvoices();
        }
        
        $groupedOrders = new \stdClass();
        
        foreach ($orders as $order) {
            if (!isset($groupedOrders->{$order->userId})) {
                $groupedOrders->{$order->userId} = array();
            }
            $groupedOrders->{$order->userId}[] = $order;
        }
        
        return $groupedOrders;
    }

}
?>
