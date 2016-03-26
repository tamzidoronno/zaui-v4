<?php
namespace ns_77bd71b1_a0e2_494e_a9e9_6dcee3829c5c;

class ProMeisterCreateAccount extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ProMeisterCreateAccount";
    }

    public function clear() {
        unset($_SESSION['ProMeisterCreateAccount_customer_selectedVatNumber']);
        unset($_SESSION['ProMeisterCreateAccount_selected_group']);
        unset($_SESSION['ProMeisterCreateAccount_customer_reference']);
    }
    
    public function render() {
        if (isset($_POST['event']) && $_POST['event'] == "showModal") {
            $this->clear();
        }
        
        
        if (!isset($_SESSION['ProMeisterCreateAccount_selected_group'])) {
            $this->includefile("step1");
            return;
        }
        
        $group = $this->getSelectedGroup();
        if ($group->usersRequireGroupReference && !isset($_SESSION['ProMeisterCreateAccount_customer_reference'])) {
            $this->includefile("step2");
            return;
        } 
     
        $company = $this->getCompany();
        if ($company) {
            $this->includefile("userinfo");
            return;
        }
        
        $this->includefile("company");
    }
    
    public function searchForCompanies() {
        $this->includefile("companysearch");
    }
    
    public function selectGroup() {
        $_SESSION['ProMeisterCreateAccount_selected_group'] = $_POST['data']['value'];
    }
    
    public function createAccount() {
        $user = new \core_usermanager_data_User();
        $user->fullName = $_POST['data']['fullname'];
        $user->emailAddress = $_POST['data']['email'];
        $user->cellPhone = $_POST['data']['cellphone'];
        $user->password = $_POST['data']['password'];
        $user->wantToBecomeCompanyOwner = isset($_POST['data']['isgarageleader']) && ($_POST['data']['isgarageleader'] == "1" || $_POST['data']['isgarageleader'] == 1);
        $user->prefix = $_POST['data']['cellPrefix'];
        
        $company = $this->getCompany();
        $company->invoiceEmail = $_POST['data']['email'];
        $company->groupId = $_SESSION['ProMeisterCreateAccount_selected_group'];
        
        if (isset( $_SESSION['ProMeisterCreateAccount_customer_reference'])) {
            $company->reference = $_SESSION['ProMeisterCreateAccount_customer_reference'];
            $this->getApi()->getUserManager()->assignReferenceToCompany($company->id,  $_SESSION['ProMeisterCreateAccount_customer_reference']);
        }
        
        $user = $this->getApi()->getUserManager()->createUser($user);
        $this->getApi()->getUserManager()->assignCompanyToUser($company, $user->id);
        
        $loggedOnUser = $this->getApi()->getUserManager()->logOn($user->username, $_POST['data']['password']);
        if ($loggedOnUser) {
            \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::setLoggedOn($loggedOnUser);
            
            if (isset($_POST['data']['otherCompanies'])) {
                $this->getApi()->getUserManager()->addMetaData($loggedOnUser->id, "need_access_to_other_companies", $_POST['data']['otherCompanies']);
            }
        }
        
        
    }

    public function getSelectedGroup() {
        return $this->getApi()->getUserManager()->getGroup($_SESSION['ProMeisterCreateAccount_selected_group']);
    }

    public function setCustomerReference() {
        $_SESSION['ProMeisterCreateAccount_customer_reference'] = $_POST['data']['ref'];
    }

    public function getCompanyByReference() {
        if (isset($_SESSION['ProMeisterCreateAccount_customer_reference'])) {
            return $this->getApi()->getUserManager()->getCompanyByReference($_SESSION['ProMeisterCreateAccount_customer_reference']);
        }
        
        return null;
    }

    public function getSelectedCompany() {
        return $this->getApi()->getUtilManager()->getCompanyFromBrReg($_SESSION['ProMeisterCreateAccount_customer_selectedVatNumber']);
    }

    public function selectCompany() {
        $_SESSION['ProMeisterCreateAccount_customer_selectedVatNumber'] = $_POST['data']['value'];
    }

    public function getCompany() {
        $companyByReference = $this->getCompanyByReference();
        $company = $this->getSelectedCompany();
        
        if ($companyByReference)
            return $companyByReference;
        
        if ($company)
            return $company;
        
        return null;
    }

}
?>
