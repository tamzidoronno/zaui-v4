<?php
namespace ns_8ad8243c_b9c1_48d4_96d5_7382fa2e24cd;

class Mail extends \MarketingApplication implements \Application {
    public $singleton = true;

    public function getDescription() {
        return $this->__f("You need this application if you want this webshop send emails from your email account");
    }
    
    public function getName() {
        return $this->__("Mail");
    }
    
    public function postProcess() {
        
    }
    
    public function preProcess() {
        
    }
    
    public function render() {
        
    }
    
    public function renderConfig() {
        $this->includefile("config");
    }
}
?>
