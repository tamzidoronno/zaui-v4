<?php
namespace ns_b3654609_a195_464f_84c4_e083702c0bf2;

class InvoiceControl extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "InvoiceControl";
    }

    public function render() {
        $this->includefile("invoice");
    }

    public function getGroupedOrders() {
        $orders = $this->getApi()->getOrderManager()->getAllUnpaidInvoices();
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
