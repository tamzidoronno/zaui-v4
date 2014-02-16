<?php
namespace ns_a109b0f1_97c3_4292_a46f_4c6894a9841c;

class PartnerApplicationSelection extends \ApplicationBase implements \Application {
    var $entries;
    var $dept;
    var $currentMenuEntry;
    
    function __construct() {
    }

    public function getDescription() {
        return "PartnerApplicationSelection";
    }

    public function getAvailablePositions() {
        return "left";
    }
    
    public function getName() {
        return "PartnerApplicationSelection";
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }
    
    public function getStarted() {
    }
    
    public function getPartnerData() {
        return $this->getFactory()->getApi()->getGetShop()->getPartnerData($this->getPartnerId(), $this->getPassword());
    }
    
    public function savePartnerList() {
        $ids = array();
        
        if($_POST['data']['ids']) {
            foreach($_POST['data']['ids'] as $id) {
                $ids[] = $id;
            }
        }
        
        $this->getApi()->getGetShop()->setApplicationList($ids, $this->getPartnerId(), $this->getPassword());
        print_r($this->getApi()->transport->errors);
    }
    
    public function getAllApplications() {
        return $this->getFactory()->getApplicationPool()->getAllApplicationSettings();
    }
    
    public function getPassword() {
        $pw = file_get_contents(dirname(__FILE__)."/password");
        return trim($pw);
    }
    
    public function getPartnerId() {
        return $this->getUser()->partnerid;
    }

    public function render() {
        $this->includefile("PartnerApplicationSelection");
    }
}
?>
