<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of AlreadyBookedPage
 *
 * @author ktonder
 */
class AlreadyBookedPage extends Page {
    public $processingReference = false;
    public $result;
    
    public function getId() {
        
    }

    public function render() {
        
        if ($this->processingReference && $this->result) {
            if ($this->result->paidFor) {
                echo "<script>";
                $bookingId = $this->result->pmsBookingId;
                echo "document.location = '/?page=checkbookingpage&bookingid=$bookingId'";
                echo "</script>";
            } else {
                echo "<script>";
                $orderId = $this->result->orderId;
                $amount = $this->result->amount;
                echo "document.location = '/?page=paymentprocesspage&orderid=$orderId&amount=$amount&name=".$this->result->name."'";
                echo "</script>";
            }
        } else {
            $this->includeFile('alreadybooked');
        }
    }
    
    public function preRun() {
        if (isset($_POST['reference'])) {
            $this->processingReference = true;
            $data = new \core_pmsbookingprocess_StartPaymentProcess();
            $data->terminalid = $this->getTerminalId();
            $data->reference = $_POST['reference'];
            $this->result = $this->getApi()->getPmsBookingProcess()->startPaymentProcess($this->getBookingEngineName(), $data);
        }
    }

}
