<?php
namespace ns_9f8483b1_eed4_4da8_b24b_0f48b71512b9;

class CrmListFilter extends \WebshopApplication implements \Application {
    public $createdCustomer = false;
    public $createdCustomerFailed = false;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "CrmListFilter";
    }

    public function render() {
        $this->includefile("filter");
    }
    
    public function searchbrreg() {
        $company = $this->getApi()->getUtilManager()->getCompaniesFromBrReg($_POST['data']['name']);
        echo json_encode($company);
    }
    public function createNewPrivateCustomer() {
        $name = $_POST['data']['name'];
        $user = new \core_usermanager_data_User();
        $user->fullName = $name;
        $this->getApi()->getUserManager()->createUser($user);
        $this->setNewCustomerFilter();
        if($user) {
            $this->createdCustomer = true;
        }
    }
    
    public function createNewCompanyCustomer() {
        $vatnumber = $_POST['data']['vatnumber'];
        $name = $_POST['data']['name'];
        
        $user = $this->getApi()->getUserManager()->createCompany($vatnumber, $name);
        if($user) {
            $this->createdCustomer = true;
        } else {
            $this->createdCustomerFailed = true;
        }
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

    public function setNewCustomerFilter() {
        $filter = array();
        $filter['when'] = "whenregistered"; 
        $filter['sorttype'] = "regdesc";
        $_SESSION['crmlistfilter'] = json_encode($filter);
    }

    public function gsAlsoUpdate() {
        $ret = array();
        $ret[] = "dcc56763-43cf-470f-87c3-ee305a5a517b";
        return $ret;
    }
}
?>
