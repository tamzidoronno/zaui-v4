<?php
namespace ns_d37cbec7_fbb1_4903_a7af_e372c16ac7dd;

class NettMannenBestilling extends \ApplicationBase implements \Application {
    var $entries;
    var $dept;
    var $currentMenuEntry;
    
    function __construct() {
    }

    public function getDescription() {
        return "NettMannenBestilling";
    }

    public function getAvailablePositions() {
        return "left";
    }
    
    public function submitForm() {
        $content = "Navn: " . $_POST['data']['name'] . "<br>";
        $content .= "Tlf: " . $_POST['data']['phone'] . "<br>";
        $content .= "Type: " . $_POST['data']['type'] . "<br>";
        $content .= "E-post: " . $_POST['data']['email'] . "<br>";
        
        $this->getApi()->getMessageManager()->sendMail("post@getshop.com", "Getshop", "Henvendelse nettmannen", $content, "system@getshop.com", "Nettmannen side");
    }
    
    public function getName() {
        return "NettMannenBestilling";
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }
    
    public function getStarted() {
    }

    public function render() {
        $this->includefile("NettMannenBestilling");
    }
}
?>
