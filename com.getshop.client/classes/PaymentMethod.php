<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of PaymentMethod
 *
 * @author ktonder
 */
class PaymentMethod {
    private $name = "";
    private $logo = "";
    private $id = "";
    private $paymentApp;

    public function setName($name) {
        $this->name = $name;
    }

    public function setLogo($logo) {
        $this->logo = $logo;
    }
    
    public function getName() {
        return $this->name;
    }

    public function getLogo() {
        return $this->logo;
    }
    
    public function getId() {
        return $this->id;
    }

    public function setPaymentApplication($paymentApp) {
        $this->paymentApp = $paymentApp;
    }
    
    public function getPaymentApplication() {
        return $this->paymentApp;
    }
    
    public function setId($id) {
        $this->id = $id;
    }
}

?>
