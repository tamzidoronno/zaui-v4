<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of PaymentFailedPage
 *
 * @author ktonder
 */
class PaymentSuccessPage extends Page {
    public function getId() {
        
    }

    public function render() {
        $this->includeFile("success");
        $this->printInvoice();
    }

    public function printInvoice() {
        $data = new \core_pmsbookingprocess_BookingPrintRecieptData();
        $data->orderId = $_GET['orderid'];
        $data->terminalId = $this->getTerminalId();
        $this->getApi()->getPmsBookingProcess()->printReciept($this->getBookingEngineName(), $data);
    }

}
