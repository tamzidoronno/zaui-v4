<?php
namespace ns_6e415852_c023_4ffe_a49f_990a521841cf;

class TermsAndConditions extends \ApplicationBase implements \Application {
    var $entries;
    var $dept;
    var $currentMenuEntry;
    
    function __construct() {
    }

    public function getDescription() {
        return $this->__f("Allows you to create a terms and condition page which has to be accepted by your customers before ordering.");
    }

    public function getAvailablePositions() {
        return "left";
    }
    
    public function getName() {
        return $this->__f("Terms and Conditions");
    }

    public function postProcess() {
        
    }
    
    public function renderConfig() {
        $this->includefile("termsconfig");
    }

    public function preProcess() {
        
    }
    
    public function getStarted() {
    }

    public function render() {
        echo "<div class='termsarea'>";
        echo $this->getTerms();
        echo "</div>";
    }
    
    public function saveContent() {
        $data = $_POST['data']['content'];
        $this->setConfigurationSetting("content", $data);
    }
    
    public function getTerms() {
        return $this->getConfigurationSetting("content");
    }
}
?>
