<?php
namespace ns_a6d68820_a8e3_4eac_b2b6_b05043c28d78;

class Company extends \SystemApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "Company";
    }

    public function renderConfig() {
        $this->includefile("overview");
    }
    
    public function updateCompanyInformation() {
        $company = $this->getApi()->getUserManager()->getCompany($_POST['companyid']);
        foreach($_POST as $key => $value) {
            $company->{$key} = $value;
        }
        
        $company->address->address = $_POST['address'];
        $company->address->postCode = $_POST['postcode'];
        $company->address->city = $_POST['city'];
        
        $company->invoiceAddress->address = $_POST['invoice_address'];
        $company->invoiceAddress->postCode = $_POST['invoice_postcode'];
        $company->invoiceAddress->city = $_POST['invoice_city'];
        
        
        $this->getApi()->getUserManager()->saveCompany($company);
    }
    
    public function render() {
        
    }
    
    public function deleteCompany() {
        $companyId = $_POST['companyid'];
        $this->getApi()->getUserManager()->deleteCompany($companyId);
    }
    
    public function createCompany() {
        $company = new \core_usermanager_data_Company();
        $company->vatNumber = $_POST['orgid'];
        $comp = $this->getApi()->getUserManager()->saveCompany($company);
        $_POST['value'] = $comp->id;
    }
}
?>
