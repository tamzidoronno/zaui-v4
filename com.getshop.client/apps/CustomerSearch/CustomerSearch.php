<?php
namespace ns_0b125d61_9516_4b24_90bc_16a84cd014b4;

class CustomerSearch extends \ApplicationBase implements \Application {
    var $entries;
    var $dept;
    var $currentMenuEntry;
    
    function __construct() {
    }

    public function getDescription() {
        return "CustomerSearch";
    }

    public function getAvailablePositions() {
        return "left";
    }
    
    public function search() {
        $_SESSION['searchword'] = $_POST['data']['keyword'];
        $this->includefile("searchresult");
    }
    
    public function getName() {
        return "CustomerSearch";
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }
    
    public function getStarted() {
    }

    public function render() {
        $this->includefile("CustomerSearch");
    }
}
?>
