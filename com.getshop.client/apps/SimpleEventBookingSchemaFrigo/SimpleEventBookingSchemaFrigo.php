<?php
namespace ns_bea0c467_dd4d_4066_891c_172adc42bb9f;

class SimpleEventBookingSchemaFrigo extends \MarketingApplication implements \Application {
    public $hasInvalidFields = false;
    
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
        if(isset($_POST['event']) && $_POST['event'] == "signup" && !$this->hasInvalidFields) {
            $this->dosignup();
        }
    }
    
    public function isValidField($field) {
        if (isset($_POST['event']) && $_POST['event'] == "signup") {
            if(!$_POST['data'][$field] || $_POST['data'][$field] == "false" || $_POST['data'][$field] == "") {
                $this->hasInvalidFields = true;
                return false;
            }
        }
        return true;
    }
    
    public function dosignup() {
        $user = new \core_usermanager_data_User();
            $user->fullName = $_POST['data']['name'];
            $user->birthDay = $_POST['data']['birthDay'];
            $user->cellPhone = $_POST['data']['cellphone'];
            $user->emailAddress = $_POST['data']['email'];
            $user->address = new \core_usermanager_data_Address();
            $user->address->address = $_POST['data']['street'];
            $user->address->postCode = $_POST['data']['postcode'];
            $user->address->city = $_POST['data']['city'];
            @$user->metaData->{'parentName'} = $_POST['data']['parentname'];
            @$user->metaData->{'parentcell'} = $_POST['data']['parentcell'];
            @$user->metaData->{'parentcell2'} = $_POST['data']['parentcell2'];
            @$user->metaData->{'sex'} = $_POST['data']['sex'];
            @$user->metaData->{'school'} = $_POST['data']['school'];
            @$user->metaData->{'schoolclass'} = $_POST['data']['schoolclass'];
            @$user->metaData->{'usepictures'} = $_POST['data']['usepicutres'];
            @$user->metaData->{'vacation'} = $_POST['data']['vacation'];
            @$user->metaData->{'comments'} = $_POST['data']['comments'];
            @$user->metaData->{'overAge'} = $_POST['data']['overAge'] && $_POST['data']['overAge'] == "true" ? "Ja" : "Nei";

            $craetedUser  =  $this->getApi()->getUserManager()->createUser($user);
            $pageIdToUse = $this->getModalVariable("pageid") ? $this->getModalVariable("pageid") : $this->getPage()->getId();
            $this->getApi()->getSimpleEventManager()->addUserToEvent($pageIdToUse , $craetedUser->id);
    }
    
    public function signup() {
            
    }
    
    public function downloadUserList() {
        $rows = array();
        $pageIdToUse = $this->getModalVariable("pageid") ? $this->getModalVariable("pageid") : $this->getPage()->getId();
        $event = $this->getApi()->getSimpleEventManager()->getEventByPageId($pageIdToUse);
        
        $header = array();
        $header[] = "Navn";
        $header[] = "Fødselsdato";
        $header[] = "Mobilnr";
        $header[] = "Epost";
        
        $header[] = "Skole";
        $header[] = "Klasse";
        $header[] = "Kjønn";
        
        $header[] = "Addresse";
        $header[] = "Postnr";
        $header[] = "By";
        
        $header[] = "Foresatte navn";
        $header[] = "Foresatte mobilnr";
        $header[] = "Valgfri mobilnr";
        $header[] = "Tilatt bildebruk";
        $header[] = "Over 18år";
        $header[] = "Annen ferie";
        $header[] = "Kommentar";
        $rows[] = $header;
        
        foreach ($event->userIds as $userId) {
            $user = $this->getApi()->getUserManager()->getUserById($userId);
            
            $xuser = array();
            $xuser[] = $user->fullName;
            $xuser[] = $user->birthDay;
            $xuser[] = $user->cellPhone;
            $xuser[] = $user->emailAddress;
            
            $xuser[] = @$user->metaData->{'school'};
            $xuser[] = @$user->metaData->{'schoolclass'};
            $xuser[] = @$user->metaData->{'sex'};
            
            $xuser[] = $user->address->address;
            $xuser[] = $user->address->postCode;
            $xuser[] = $user->address->city;
            
            $xuser[] = @$user->metaData->{'parentName'};
            $xuser[] = @$user->metaData->{'parentcell'};
            $xuser[] = @$user->metaData->{'parentcell2'};
            $xuser[] = @$user->metaData->{'usepictures'};
            $xuser[] = @$user->metaData->{'overAge'};
            $xuser[] = @$user->metaData->{'vacation'};
            $xuser[] = @$user->metaData->{'comments'};
            
            $rows[] = $xuser;
        }
        
        echo json_encode($rows);
        
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
