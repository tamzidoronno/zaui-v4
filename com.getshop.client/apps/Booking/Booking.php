<?php
namespace ns_74ea4e90_2d5a_4290_af0c_230a66e09c78;
use \MarketingApplication;
use \Application;

class Booking extends MarketingApplication implements Application {
    public $entries;
    private $company;
    
    public function getAllowAdd() {
        $settings = $this->getConfiguration()->settings;
        if (isset($settings->allowadd)) {
            return $settings->allowadd->value == "true";
        }
        return false;
    }
    
    public function GetEntries() {
        return $this->entries;
    }
    
    function getAvailablePositions() {
        return "middle";
    }
    
    public function getDescription() {
        return $this->__("give your customers ability to book into your events or appointments");
    }
    
    public function getName() {
        return $this->__("Booking");
    }
    
    public function postProcess() {
        
    }
    
    public function renderConfig() {
        $this->includefile('config');
    }
    
    public function showSettings() {
        $this->includefile('config');
    }

    public function getStarted() {
        echo $this->__f("Create this application and start book events to given dates.");
    }
    
    public function isGetCompanyInformationRemoteEnabled() {
        return $this->getConfigurationSetting("useBrRegToGetCompanyInformation") == "true";
    }
    
    public function preProcess() {
        $this->entries = $this->getApi()->getCalendarManager()->getEntries((int)date('Y'), (int)date('m'), (int)date('d'), array());
    }
    
    public function render() {
        $this->includefile('schema');
    }
    
    public function createUser($data, $password) {
        $user = $this->getApiObject()->core_usermanager_data_User();
        $user->fullName =  $data['name'];
        $user->emailAddress = $data['email'];
        $user->type = 10;
        $user->password = $password;
        
        if (isset($_POST['data']['invoiceemail'])) {
            $user->emailAddressToInvoice = $_POST['data']['invoiceemail'];
        }
        
        $user->birthDay = isset($data['birthday']) ? $data['birthday'] : "";
        if ($this->isGetCompanyInformationRemoteEnabled()) {
            $this->setCompany($user->birthDay);
            $user->company = $this->company;
            $user->companyName = "";
        } else {
            $user->companyName = isset($data['company']) ? $data['company'] : "";
        }
        
        $user->cellPhone = $data['cellphone'];
        
        if (isset($_SESSION['group'])) {
            $user->groups = array();
            $user->groups[] = $_SESSION['group'];
        }
        
        return $this->getApi()->getUserManager()->createUser($user);
    }
    
    
    public function registerEvent($data) {
        $password = rand(11820, 98440);
        $user = $this->createUser($data, $password);
        if ($this->isConnectedToCurrentPage()) {
            $this->getApi()->getCalendarManager()->addUserToPageEvent($user->id, $this->getConfiguration()->id);
        } else {
            $this->getApi()->getCalendarManager()->addUserToEvent($user->id, $data['eventid'], $password, $user->username);
        }
        unset($_SESSION['group']);
    }
    
    private function registerCalenderEvent($data) {
        if (isset($this->getConfiguration()->settings->description_for_allowaddon)) {
            $text = $this->getConfiguration()->settings->description_for_allowaddon->value;
        } else {
            $text = " please configure this";
        }
        
        if (isset($this->getConfiguration()->settings->location_for_allowaddon)) {
            $location = $this->getConfiguration()->settings->location_for_allowaddon->value;
        } else {
            $location = " please configure this";
        }
        
        $date = explode("/", $_POST['data']['date']);
        $month = (int)$date[0];
        $day = (int)$date[1];
        $year = (int)$date[2];
        $time = $_POST['data']['time'];
                
        $event = $this->getApi()->getCalendarManager()->createEntry($year, $month, $day);
        $event->title = "$time - $text";
        $event->maxAttendees = 1;
        $event->description = "";
        $event->starttime = $time;
        $event->location = $location;
        $event->needConfirmation = true;
        $event->color = "#018abd";
        $this->getApi()->getCalendarManager()->saveEntry($event);
        return $event;
    }
    
    private function sendMail($event, $user) {
        $settings = $this->getConfiguration()->settings;
        
        if (!isset($settings->email_for_allowaddon)) {
            return;
        }
        
        $to = $settings->email_for_allowaddon->value;
        if ($to == "" || $to == null) {
            return;
        }
        
        $subject = "A new booking request needs to be confirmed";
        $message = "<br>Title: ".$event->title;
        $message .= "<br>Date: ".$event->day." / ".$event->month . " - ". $event->year;
        $message .= "<br>Start: ".$event->starttime;
        $message .= "<br>";
        $message .= "<br>User details:";
        $message .= "<br>Name: ".$user['name'];
        $message .= "<br>Email: ".$user['email'];
        $message .= "<br>Cellphone: ".$user['cellphone'];
        $message .= "<br>";
        $message .= "<br>Please log in to your webpage, go to the calander and confirm or delete the event";
        
        $this->getApi()->getMessageManager()->sendMail($to, "Calander event request", $subject, $message, "post@getshop.com", "Getshop Calander");
    }
    
    public function runRegisterEvent() {
        $data['name'] = $_POST['data']['name'];
        $data['email'] = $_POST['data']['email'];
        $data['cellphone'] = $_POST['data']['cellphone'];
        
        if ($this->getAllowAdd()) {
            $event = $this->registerCalenderEvent($data);
            $data['eventid'] = $event->entryId;
            $this->sendMail($event, $data);
        } else {
            $data['birthday'] = $_POST['data']['birthday'];
            $data['company'] = $_POST['data']['company'];
            $data['eventid'] = isset($_POST['data']['eventid']) ? $_POST['data']['eventid'] : ""; 
        }
        
        $this->registerEvent($data);
    }
    
    public function setGroup() {
        $_SESSION['group'] = $_POST['data']['groupid'];
    }
    
    public function unsetGroup() {
        unset($_SESSION['group']);
    }
    
    private function setCompany($orgNumber) {
        $this->company = $this->getApi()->getUtilManager()->getCompanyFromBrReg($orgNumber);
    }
    
    public function findCompanies() {
        $name = $_POST['data']['name'];
        $result = $this->getApi()->getUtilManager()->getCompaniesFromBrReg($name);
        echo "<bR>";
        foreach($result as $orgnr => $name) {
            echo "<b>".$this->__w("orgnummer")."</b>: ".$orgnr;
            echo "<br>".$name;
            echo "<span class='gs_button select_searched_company' orgnr='$orgnr'>".$this->__w("select") . "</span><br><br>";
            echo "<hr>";
        }
    }
    
    public function getCompanyInformation() {
        $this->setCompany($_POST['data']['vatnumber']);
        $this->includefile('company');
    }
    
    public function getCompany() {
        return $this->company;
    }
    
    public function isInvoiceEmailActivated() {
        return $this->getConfigurationSetting("addExtraEmailToBeSentTo") == "true";
    }
    
    public function isConnectedToCurrentPage() {
        return $this->getConfigurationSetting("connectedtopage") == "true";
    }
    
    public function getUsersConnectedToSchema() {
        return $this->getApi()->getUserManager()->getAllUsersWithCommentToApp($this->getConfiguration()->id);
    }
    
    public function getComment($user) {
        foreach ($user->comments as $comment) {
            if ($comment->appId == $this->getConfiguration()->id) {
                return $comment;
            }
        }
        return null;
    }
    
    public function markAsDone() {
        $userId = $_POST['data']['userId'];
        $user = $this->getApi()->getUserManager()->getUserById($userId);
        $comment = $this->getComment($user);
        $comment->extraInformation = "processed";
        $this->getApi()->getUserManager()->addComment($userId, $comment);
    }
}
?>
