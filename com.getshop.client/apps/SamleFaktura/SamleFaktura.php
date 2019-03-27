<?php
namespace ns_cbe3bb0f_e54d_4896_8c70_e08a0d6e55ba;

class SamleFaktura extends \PaymentApplication implements \Application {
    public function getDescription() {
        return "This payment method is used for merging invoices into one invoice.";
    }

    public function getName() {
        return "SamleFaktura";
    }

    public function render() {
        if (isset($_SESSION['samlefaktura_order_id'])) {
            $this->includefile("ordercompleted");
            $this->cancel();
        } else if (!isset($_SESSION['samlefaktura_orders']) || !count($_SESSION['samlefaktura_orders'])) {
            $this->includefile("overview");
        } else {
            $this->includefile("selectuser");
        }
    }
    
    /**
     * 
     * @return \core_ordermanager_data_Order[]
     */
    public function getOrdersToBeConsilidated() {
        $orders = array();
        foreach ($_SESSION['samlefaktura_orders'] as $orderId) {
            $orders[] = $this->getApi()->getOrderManager()->getOrder($orderId);
        }
        return $orders;
    }

    public function groupOrdersByUserId($orders) {
        $userOrders = array();
        foreach ($orders as $order) {
            
            if (!isset($userOrders[$order->userId])) {
                $userOrders[$order->userId] = array();
            }
            
            $userOrders[$order->userId][] = $order;
        }
        
        return $userOrders;
    }

    public function showSelectUser() {
        $_SESSION['samlefaktura_orders'] = array();
        foreach ($_POST['data'] as $varname => $value) {
            if ($value == "true" && strstr($varname, "orderid_") > -1) {
                $_SESSION['samlefaktura_orders'][] = str_replace("orderid_", "", $varname);
            }
        }
    }
    
    public function createInvoice() {
        $order = $this->getApi()->getOrderManager()->mergeAndCreateNewOrder($_POST['data']['userid'], $_SESSION['samlefaktura_orders'], "70ace3f0-3981-11e3-aa6e-0800200c9a66", $_POST['data']['invoicenote']);
        $_SESSION['samlefaktura_order_id'] = $order->id;
    }
    
    public function sendInvoice() {
        $subject = "Invoice attached";
        $body = "Attached is the invoice for your order";
        $this->getApi()->getOrderManager()->sendRecieptWithText($_SESSION['samlefaktura_order_id'], $_POST['data']['emailaddress'], $subject, $body);
    }
    
    public function cancel() {
        unset($_SESSION['samlefaktura_orders']);
        unset($_SESSION['samlefaktura_order_id']);
    }
    
    public function renderPaymentOption() {
        $this->includefile("paymentoption");
    }
    
    public function getColor() {
        return "darkgreen";
    }
}
?>
