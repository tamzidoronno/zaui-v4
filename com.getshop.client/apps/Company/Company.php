<?php
namespace ns_a6d68820_a8e3_4eac_b2b6_b05043c28d78;

class Company extends \ns_27716a58_0749_4601_a1bc_051a43a16d14\GSTableCommon implements \Application {
    /**
     * @var \core_usermanager_data_User
     */
    public $selectedUser;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "Company";
    }
    
    public function getBackendName() {
        // C3 
        if ($this->isC3()) {
            return "Partners";
        }
        
        return $this->getName();
    }
    
    public function updateUser() {
        $user = $this->getApi()->getUserManager()->getUserById($_POST['userid']);
        $user->company = array();
        $user->company[] = $_POST['companyid'];
        $this->getApi()->getUserManager()->saveUser($user);
    }

    public function renderConfig() {
        $this->includefile("overview");
    }
    
    /**
     * @param \core_usermanager_data_User $user
     */
    public function renderUserSettings($user) {
        $this->selectedUser = $user;
        $this->includefile("companyUserSelection");
    }
    
    public function updateCompanyInformation() {
        $company = $this->getApi()->getUserManager()->getCompany($_POST['companyid']);
        $company->vatNumber = $_POST['vatNumber'];
        foreach($_POST as $key => $value) {
            if($key == "address" || $key == "invoiceAddress") {
                continue;
            } 
            $company->{$key} = $value;
        }
        
        
        $company->address->address = $_POST['address'];
        $company->address->postCode = $_POST['postCode'];
        $company->address->city = $_POST['city'];
        $company->group = $_POST['groupId'];
        
        $company->invoiceAddress->address = $_POST['invoice_address'];
        $company->invoiceAddress->postCode = $_POST['invoice_postCode'];
        $company->invoiceAddress->city = $_POST['invoice_city'];
        
        
        $this->getApi()->getUserManager()->saveCompany($company);
    }
    
    public function render() {
        
    }
    public function loadData() {
        $this->filteredData = $this->getApi()->getUserManager()->getAllCompanyFiltered($this->createFilter());
    }

    public function deleteCompany() {
        $companyId = $_POST['companyid'];
        $this->getApi()->getUserManager()->deleteCompany($companyId);
    }   
    
    public function removeCompanyFromUser() {
        $this->getApi()->getUserManager()->removeUserFromCompany($_POST['value2'], $_POST['value']);
    }
    
    public function createCompany() {
        $company = new \core_usermanager_data_Company();
        $company->vatNumber = $_POST['orgid'];
        $comp = $this->getApi()->getUserManager()->saveCompany($company);
        $_POST['value'] = $comp->id;
    }
    
    public function searchForCompanies() {
        return $this->getApi()->getUserManager()->searchForCompanies($_POST['value']);
    }
    
    public function addCompanyToUser() {
        $company = $this->getApi()->getUserManager()->getCompany($_POST['value2']);
        $this->getApi()->getUserManager()->assignCompanyToUser($company, $_POST['value']);
    }
    
    public function renderExtraGroupList($group) {
        $count = $this->getApi()->getUserManager()->getCompaniesConnectedToGroupCount($group->id);
        echo "<div style='font-size: 13px; font-style: italic;'>Companies: $count </div>";
    }
    
    public function getNewText() {
        if ($this->isC3()) {
            return "New partner";
        }
        return $this->__f("New company");
    }
    
    public function getListNames() {
        if ($this->isC3()) {
            return "All partners";
        }
        return $this->__f("New partner");
    }

    public function isC3() {
        return $this->getFactory()->getStore()->id == "f2d0c13c-a0f7-41a7-8584-3c6fa7eb68d1";
    }

    public function getCompanySearchText() {
        if ($this->isC3()) {
            return "Search for partners";
        }
        
        return "Search for company";
    }
    
    public function getAppsWithPrintingToCompany() {
        $apps = $this->getApi()->getStoreApplicationPool()->getApplications();
        $retApps = [];
        foreach ($apps as $appSetting) {
            $instance = $this->getFactory()->getApplicationPool()->createInstace($appSetting);
            if (method_exists($instance, "renderCompanySettings")) {
                $retApps[] = $instance;
            }
        }
        
        return $retApps;
    }

    
}
?>
