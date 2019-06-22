<?php
namespace ns_486009b1_3748_4ab6_aa1b_95a4d5e2d228;

class DefaultPaymentHandlingAction extends \PaymentApplication implements \Application {
    public $overrideDefault = true;
    public $header = false;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "DefaultPaymentHandlingAction";
    }

    public function render() {
        $this->includefile("common");
        
        if ($this->order->status == 7 || $this->order->closed) {
            $this->includefile("closedoptions");
        } else {
            $this->includefile("openoptions");
        }
    }
    
    public function creditOrder() {
        $this->getApi()->getOrderManager()->creditOrder($_POST['data']['orderid']);
        $this->order = $this->getApi()->getOrderManager()->getOrder($_POST['data']['orderid']);
    }
    
    public function sendReceipt() {
        $this->getApi()->getInvoiceManager()->sendReceiptToCashRegisterPoint($_POST['data']['deviceid'], $_POST['data']['orderid']);
    }
    
    public function saveUser($user) {
        $order = $this->getApi()->getOrderManager()->getOrder($_POST['data']['orderid']);
        $order->userId = $user->id;
        $this->getApi()->getOrderManager()->saveOrder($order);
        $this->order = $this->getApi()->getOrderManager()->getOrder($_POST['data']['orderid']);
    }
    
    public function createNewUser() {
        $user = new \core_usermanager_data_User();
        $user->fullName = $_POST['data']['name'];
        $createUser = $this->getApi()->getUserManager()->createUser($user);
        return $createUser;
    }
    
    public function createCompany() {
        $name = $_POST['data']['companyname'];
        $vat = $_POST['data']['vatnumber'];
        $user = $this->getApi()->getUserManager()->createCompany($vat, $name);
        
        $order = $this->getOrder();
        $order->userId = $user->id;
        $order->cart->address = $user->address;
        $order->cart->address->fullName = $user->fullName;
        $this->getApi()->getOrderManager()->saveOrder($order);
        return $user;
    }
    
    public function changeUser($user) {
        $order = $this->getApi()->getOrderManager()->getOrder($_POST['data']['orderid']);
        $order->userId = $user->id;
        $this->getApi()->getOrderManager()->saveOrder($order);
        $this->order = $this->getApi()->getOrderManager()->getOrder($_POST['data']['orderid']);
    }
    
    public function markOrderAsPaid() {
        $order = $this->getApi()->getOrderManager()->getOrder($_POST['data']['orderid']);
        $amount = $this->getApi()->getOrderManager()->getTotalAmount($order);
        $date = $this->convertToJavaDate(strtotime($_POST['data']['paymentdate']));
        
        $this->getApi()->getOrderManager()->markAsPaid($order->id, $date, $amount);
        $this->order = $this->getApi()->getOrderManager()->getOrder($_POST['data']['orderid']);
    }
    
    public function cancelCurrentOrder() {
        $this->getApi()->getOrderManager()->deleteOrder($_POST['data']['orderid']);
    }
    
    public function closeCurrentOrder() {
        $this->getApi()->getOrderManager()->closeOrder($_POST['data']['orderid'], "Closed by administrator");
        $this->order = $this->getApi()->getOrderManager()->getOrder($_POST['data']['orderid']);
    }
    
    public function updateOrderNote() {
        $order = $this->getApi()->getOrderManager()->getOrder($_POST['data']['orderid']);
        $order->invoiceNote = $_POST['data']['note'];
        $this->getApi()->getOrderManager()->saveOrder($order);
        $this->order = $this->getApi()->getOrderManager()->getOrder($_POST['data']['orderid']);
    }
    
    public function changeMethod() {
        $this->getApi()->getOrderManager()->changeOrderType($_POST['data']['orderid'], $_POST['data']['paymentid']);
    }
}
?>
