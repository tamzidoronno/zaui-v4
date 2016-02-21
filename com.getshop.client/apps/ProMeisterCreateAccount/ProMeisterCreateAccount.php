<?php
namespace ns_77bd71b1_a0e2_494e_a9e9_6dcee3829c5c;

class ProMeisterCreateAccount extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ProMeisterCreateAccount";
    }

    public function render() {
        $this->includefile("createAccount");
    }
    
    public function searchForCompanies() {
        $this->includefile("companysearch");
    }
    
    public function createAccount() {
        $user = new \core_usermanager_data_User();
        $user->fullName = $_POST['data']['fullname'];
        $user->emailAddress = $_POST['data']['email'];
        $user->cellPhone = $_POST['data']['cellphone'];
        $user->password = $_POST['data']['password'];
        if (isset($_POST['data']['groupreference'])) {
            $user->metaData['groupreference'] = $_POST['data']['groupreference'];
        }
        
        $company = $this->getApi()->getUtilManager()->getCompanyFromBrReg($_POST['data']['company']);
        $company->invoiceEmail = $_POST['data']['email'];
        
        $user = $this->getApi()->getUserManager()->createUser($user);
        $this->getApi()->getUserManager()->assignCompanyToUser($company, $user->id);
    }
}
?>
