<?php
namespace ns_451d5030_1936_4a8a_95fc_014066c0b24e;

class ProMeisterCreateCompanyAccount extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ProMeisterCreateCompanyAccount";
    }

    public function render() {
        if (isset($_POST['data']['name'])) {
            $this->wrapContentManager("thankyou", "The account has now been created and ready to be used");
        } else {
            $this->includefile("createCompanyAccount");
        }
    }
    
    public function createAccount() {
        $companyId = $this->getModalVariable("companyid");
        $users = $this->getApi()->getUserManager()->getUsersByCompanyId($companyId);
        $company = $this->getApi()->getUserManager()->getCompany($companyId);
        
        $reference = $this->getReference($users);
        $groupId = $this->getGroupId($users);
        
        $user = new \core_usermanager_data_User();
        $user->fullName = $_POST['data']['name'];
        $user->emailAddress = $_POST['data']['email'];
        $user->cellPhone = $_POST['data']['cellphone'];
        $user->password = $_POST['data']['password'];
        
        $this->getApi()->getUserManager()->assignCompanyToGroup($company, $groupId);
        
        if ($reference) {
            $user->metaData['groupreference'] = $reference;
        }
        
        $user = $this->getApi()->getUserManager()->createUser($user);
        $this->getApi()->getUserManager()->assignCompanyToUser($company, $user->id);
    }

    public function getGroupId($users) {
        if (!count($users)) {
            return "";
        }
        
        foreach ($users as $user) {
            if ($user->groups && count($user->groups)) {
                return $user->groups[0];
            }
        }
        
        return "";
    }

    public function getReference($users) {
        if (!count($users)) {
            return "";
        }
        
        foreach ($users as $user) {
            if (isset($user->metaData->{'groupreference'})) {
                return $user->metaData->{'groupreference'};
            }
        }
        
        return "";
    }

}
?>
