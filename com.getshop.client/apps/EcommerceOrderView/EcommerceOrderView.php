<?php
namespace ns_bce90759_5488_442b_b46c_a6585f353cfe;

class EcommerceOrderView extends \MarketingApplication implements \Application {
    private $order;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "EcommerceOrderView";
    }

    public function render() {
        $this->setData();
        $this->includefile("overview");
    }
    
    public function setData() {
        if ($this->getModalVariable("orderid")) {
            $_SESSION['EcommerceOrderView_current_order'] = $this->getModalVariable("orderid");
        }
        if (isset($_SESSION['EcommerceOrderView_current_order'])) {
            $this->order = $this->getApi()->getOrderManager()->getOrder($_SESSION['EcommerceOrderView_current_order']);
        }
    }

    public function loadOrder($orderId) {
        $_SESSION['EcommerceOrderView_current_order'] = $orderId;
        $this->setData();
    }
    
    public function markForResending() {
        $order = $this->getApi()->getOrderManager()->getOrder($_POST['data']['id']);
        $order->transferredToAccountingSystem = false;
        $order->triedTransferredToAccountingSystem  = false;
        $this->getApi()->getOrderManager()->saveOrder($order);
    }
    
    public function directTransferOrder() {
        $errors = $this->getApi()->getGetShopAccountingManager()->transferDirect($_POST['data']['id']);
        if (count($errors)) {
            $obj = $this->getStdErrorObject(); // Get a default error message
            $obj->fields->errorMessage = "<b>".$this->__f("Could not transfer the item due to the following reasons").":</b>";
            foreach ($errors as $error) {
                $obj->fields->errorMessage .= "<br/>$error";
            }
            $this->doError($obj); // Code will stop here.
        }
    }
    
    public function getOrder() {
        return $this->order;
    }
    
    
    public function isTabActive($tabName) {
        if ($tabName == "accounting") {
            return "active";
        }
        
        return false;
    }
    
    public function test() {
        
    }

}
?>
