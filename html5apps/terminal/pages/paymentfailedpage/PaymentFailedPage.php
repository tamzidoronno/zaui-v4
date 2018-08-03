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
class PaymentFailedPage extends Page {
    public function getId() {
        
    }

    public function render() {
        $this->includeFile("failed");
    }
}
