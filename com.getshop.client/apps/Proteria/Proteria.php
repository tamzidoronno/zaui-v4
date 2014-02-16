<?php

namespace ns_e18089e2_56fa_4481_ae13_651a67d0f016;

class Proteria extends \ApplicationBase implements \Application {

    var $entries;
    var $dept;
    var $currentMenuEntry;

    function __construct() {
        
    }

    public function getDescription() {
        return "Proteria is a scandianivan shipping handling system.";
    }

    public function getAvailablePositions() {
        return "left";
    }

    public function getName() {
        return "Proteria";
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }

    public function getCallBackUrl() {
        return "http://" . $_SERVER['SERVER_NAME'] . "/callback.php?app=" . $this->getConfiguration()->id . "&callback_method=handleCallback";
    }

    public function renderConfig() {
        $this->includefile("proteriaconfig");
    }

    public function handleCallback() {
        $orderNr = $_GET['OrdreNr'];
        $trackingNumber = $_GET['SendingsNr'];
        
        $this->startAdminImpersonation("OrderManager", "getOrderByincrementOrderId");
        $order = $this->getApi()->getOrderManager()->getOrderByincrementOrderId($orderNr);
        $this->stopImpersionation();
        
        $order->trackingNumber = $trackingNumber;
        file_put_contents("/tmp/sendnr", serialize($order));
        
        $this->startAdminImpersonation("OrderManager", "saveOrder");
        $this->getApi()->getOrderManager()->saveOrder($order);
        $this->stopImpersionation();
        
        $this->startAdminImpersonation("OrderManager", "changeOrderStatus");
        $this->getApi()->getOrderManager()->changeOrderStatus($order->id, 6);
        $this->stopImpersionation();        
    }

    public function getStarted() {
        
    }

    public function render() {
        
    }

    public function requestAdminRights() {
        $this->requestAdminRight("OrderManager", "saveOrder", $this->__o("Updating the order."));
        $this->requestAdminRight("OrderManager", "getOrderByincrementOrderId", $this->__o("Needed to fetch the order."));
        $this->requestAdminRight("OrderManager", "changeOrderStatus", $this->__o("Require admin right for this function because it needs to update the orderstatus upon callback"));
    }

}

?>
