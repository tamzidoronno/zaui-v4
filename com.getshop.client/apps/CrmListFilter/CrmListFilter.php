<?php
namespace ns_9f8483b1_eed4_4da8_b24b_0f48b71512b9;

class CrmListFilter extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "CrmListFilter";
    }

    public function render() {
        $this->includefile("filter");
    }
    
    public function applyFilter() {
        $_SESSION['crmlistfilter'] = json_encode($_POST['data']);
    }
    
    public function getSelectedFilter() {
        if(isset($_SESSION['crmlistfilter'])) {
            return json_decode($_SESSION['crmlistfilter'],true);
        }
        return array();
    }
}
?>
