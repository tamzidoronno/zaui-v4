<?php
namespace ns_bea0c467_dd4d_4066_891c_172adc42bb9f;

class SimpleEventBookingSchemaFrigo extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SimpleEventBookingSchemaFrigo";
    }

    public function render() {
        $this->includefile("schema");
        if (\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::isEditor() || \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::isAdministrator()) {
            $this->includefile("signedup");
        }
    }
    
    public function signup() {
        $user = new \core_usermanager_data_User();
        $user->fullName = $_POST['data']['name'];
        $user->cellPhone = $_POST['data']['cellphone'];
        $user->emailAddress = $_POST['data']['email'];
        $user->address = new \core_usermanager_data_Address();
        $user->address->address = $_POST['data']['street'];
        $user->address->postCode = $_POST['data']['postcode'];
        @$user->metaData->{'parentName'} = $_POST['data']['parentname'];
        @$user->metaData->{'parentcell'} = $_POST['data']['parentcell'];
        
        $craetedUser  =  $this->getApi()->getUserManager()->createUser($user);
        
        $pageIdToUse = $this->getModalVariable("pageid") ? $this->getModalVariable("pageid") : $this->getPage()->getId();
        $this->getApi()->getSimpleEventManager()->addUserToEvent($pageIdToUse , $craetedUser->id);
    }
    
    public function sendMailTilAll() {
        $pageIdToUse = $this->getModalVariable("pageid") ? $this->getModalVariable("pageid") : $this->getPage()->getId();
        $event = $this->getApi()->getSimpleEventManager()->getEventByPageId($pageIdToUse);
        
        $subject = $_POST['data']['subject'];
        $content = $_POST['data']['message'];
        
        foreach ($event->userIds as $userId) {
            $user = $this->getApi()->getUserManager()->getUserById($userId);
            if ($user) {
                $this->getApi()->getMessageManager()->sendMail($user->emailAddress, $user->fullName, $subject, $content, null, null);
            }
        }
    }
}
?>
