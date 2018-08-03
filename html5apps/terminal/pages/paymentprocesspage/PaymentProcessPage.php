<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of PaymentProcessPage
 *
 * @author ktonder
 */
class PaymentProcessPage extends Page {
    public $state = "";
    public $messageToDisplay = "";
    
    public function getId() {
        
    }

    public function preRun() {
        if (isset($_POST['orderid'])) {
            $_GET['orderid'] = $_POST['orderid'];
            $_GET['amount'] = $_POST['amount'];
        }
        
        if (isset($_POST['cancelPayment'])) {
            $data = new \core_pmsbookingprocess_StartPaymentProcess();
            $data->terminalid = $this->getTerminalId();
            $this->getApi()->getPmsBookingProcess()->cancelPaymentProcess($this->getBookingEngineName(), $data);
        }
        
        if (isset($_POST['action'])) {
            $this->handleDebugActions();
        }
        
        $messages = $this->getApi()->getPmsBookingProcess()->getTerminalMessages($this->getBookingEngineName());
        
        if ($messages) {
            $msg = end($messages);
            if ($msg == "completed") {
                $this->messageToDisplay = "";
                $this->state = "completed";
                unset($_SESSION['lastMessage']);
            } else if ($msg == "payment failed") {
                $this->messageToDisplay = "";
                $this->state = "payment failed";
                unset($_SESSION['lastMessage']);
            } else {
                $this->messageToDisplay = $msg;
                $_SESSION['lastMessage'] = $msg;
            }
        }
 
        if (isset($_SESSION['lastMessage'])) {
            $this->messageToDisplay = $_SESSION['lastMessage'];
        }
        
        if (isset($_GET['retry']) && $_GET['retry'] == "true") {
            $data = new \core_pmsbookingprocess_StartPaymentProcess();
            $data->terminalid = $this->getTerminalId();
            $data->reference = $_GET['orderid'];
            $this->result = $this->getApi()->getPmsBookingProcess()->startPaymentProcess($this->getBookingEngineName(), $data);
        }
    }
    
    public function render() {
        if ($this->state == "payment failed") {
            echo "<script>document.location='/?page=paymentfailedpage&orderid=".$_GET['orderid']."&amount=".$_GET['amount']."';</script>";
        } else if ($this->state == "completed") {
            echo "<script>document.location='/?page=paymentsuccesspage&orderid=".$_GET['orderid']."&amount=".$_GET['amount']."';</script>";
        } else {
            $this->includeFile("paymentprocess");
        }
    }

    public function handleDebugActions() {
        if ($_POST['action'] == "success") {
            $this->getApi()->getPmsBookingProcess()->addTestMessagesToQueue($this->getBookingEngineName(), "completed");
        }
        if ($_POST['action'] == "failed") {
            $this->getApi()->getPmsBookingProcess()->addTestMessagesToQueue($this->getBookingEngineName(), "payment failed");
        }
        if ($_POST['action'] == "other") {
            $this->getApi()->getPmsBookingProcess()->addTestMessagesToQueue($this->getBookingEngineName(), "Please insert credit card");
        }
    }

}
