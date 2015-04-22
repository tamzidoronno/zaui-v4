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

    public function isExtraDepActivated() {
        if ($this->getConfigurationSetting("extradep") == "true" && isset($_SESSION['group'])) {
            if ($_SESSION['group'] == "1cdd1d93-6d1b-4db3-8e91-3c30cfe38a4a" || $_SESSION['group'] == "ddcdcab9-dedf-42e1-a093-667f1f091311" || $_SESSION['group'] == "608c2f52-8d1a-4708-84bb-f6ecba67c2fb") {
                return true;
            }
        }

        return false;
    }

    public function getTextForExtraDep() {
        if ($_SESSION['group'] == "1cdd1d93-6d1b-4db3-8e91-3c30cfe38a4a") {
            return "Meko-Id";
        }

        if ($_SESSION['group'] == "ddcdcab9-dedf-42e1-a093-667f1f091311" || $_SESSION['group'] == "608c2f52-8d1a-4708-84bb-f6ecba67c2fb") {
            return "Kundnummer";
        }

        return "";
    }

    public function getCheckType() {
        if ($_SESSION['group'] == "1cdd1d93-6d1b-4db3-8e91-3c30cfe38a4a") {
            return 1;
        }

        if ($_SESSION['group'] == "ddcdcab9-dedf-42e1-a093-667f1f091311" || $_SESSION['group'] == "608c2f52-8d1a-4708-84bb-f6ecba67c2fb") {
            return 2;
        }

        return 0;
    }
    
    public function isGetCompanyInformationRemoteEnabled() {
        return $this->getConfigurationSetting("useBrRegToGetCompanyInformation") == "true" || $this->getConfigurationSetting("useEniroSearchEngine") == "true";
    }

    public function getOrgNumberLength() {
        if ($this->getConfigurationSetting("useBrRegToGetCompanyInformation") == "true") {
            return 9;
        }

        if ($this->getConfigurationSetting("useEniroSearchEngine") == "true") {
            return 10;
        }

        return 0;
    }

    public function preProcess() {
        $this->entries = $this->getApi()->getCalendarManager()->getEntries((int) date('Y'), (int) date('m'), (int) date('d'), array());
    }

    public function render() {
        $this->includefile('schema');
    }

    public function createUser($data, $password) {
        $user = $this->getApiObject()->core_usermanager_data_User();
        $user->fullName = $data['name'];
        $user->emailAddress = $data['email'];
        $user->type = 10;
        $user->password = $password;

        if (isset($data['referenceKey'])) {
            $user->referenceKey = $data['referenceKey'];
        } else {
            $user->referenceKey = "-";
        }

        if (isset($_POST['data']['invoiceemail'])) {
            $user->emailAddressToInvoice = $_POST['data']['invoiceemail'];
        }

        $user->birthDay = isset($data['birthday']) ? $data['birthday'] : "";
        if ($this->isGetCompanyInformationRemoteEnabled()) {
            $this->setCompany($user->birthDay, true);
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
            $this->getApi()->getCalendarManager()->addUserToEvent($user->id, $data['eventid'], $password, $user->username, "webpage");
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
        $month = (int) $date[0];
        $day = (int) $date[1];
        $year = (int) $date[2];
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
        $message = "<br>Title: " . $event->title;
        $message .= "<br>Date: " . $event->day . " / " . $event->month . " - " . $event->year;
        $message .= "<br>Start: " . $event->starttime;
        $message .= "<br>";
        $message .= "<br>User details:";
        $message .= "<br>Name: " . $user['name'];
        $message .= "<br>Email: " . $user['email'];
        $message .= "<br>Cellphone: " . $user['cellphone'];
        $message .= "<br>";
        $message .= "<br>Please log in to your webpage, go to the calander and confirm or delete the event";

        $this->getApi()->getMessageManager()->sendMail($to, "Calander event request", $subject, $message, "post@getshop.com", "Getshop Calander");
    }

    public function runRegisterEvent() {
        $data['name'] = $_POST['data']['name'];
        $data['email'] = $_POST['data']['email'];
        $data['cellphone'] = $_POST['data']['cellphone'];

        if (isset($_POST['data']['extradep'])) {
            $data['referenceKey'] = $_POST['data']['extradep'];
        }

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

    private function setCompany($orgNumber, $fetch) {
        if ($fetch) {
            $this->company = $this->getApi()->getUtilManager()->getCompanyFromBrReg($orgNumber);
        } else {
            $this->company = $this->getApi()->getUtilManager()->getCompanyFree($orgNumber);
        }
    }

    public function findCompanies() {
        $name = $_POST['data']['name'];
        $result = $this->getApi()->getUtilManager()->getCompaniesFromBrReg($name);
        if (!is_array($result) || sizeof($result) == 0) {
            return;
        }

        echo "<table width='100%'>";
        foreach ($result as $company) {
            $orgnr = $company->vatNumber;
            $name = $company->name;
            echo "<tr orgnr='$orgnr' class='company_selection'>";
            echo "<td>" . $orgnr . "</td>";
            echo "<td class='selected_name'>" . $name . "</td>";
            echo "<td><span class='gs_button small select_searched_company'>" . $this->__w("select") . "</span></td>";
            echo "</tr>";
        }
        echo "</table>";
    }

    public function getCompanyInformation() {
        $this->setCompany($_POST['data']['vatnumber'], false);
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

    private function getInvoicedArray() {
        $invoiced = $this->getConfigurationSetting("invoiced");

        if ($invoiced == null) {
            $invoiced = new \stdClass();
        } else {
            $invoiced = json_decode($invoiced);
        }

        if (!($invoiced instanceof \stdClass)) {
            return new \stdClass();
        }

        return $invoiced;
    }

    public function isInvoiced($userId) {
        $invoiced = $this->getInvoicedArray();

        if (!property_exists($invoiced, $userId)) {
            return false;
        }

        $value = $invoiced->{$userId};
        return $value;
    }

    public function toggleInvoiced() {
        $userId = $_POST['data']['userId'];
        $value = $this->isInvoiced($userId);
        $invoiced = $this->getInvoicedArray();

        $invoiced->$userId = !$value;
        $this->setConfigurationSetting("invoiced", json_encode($invoiced));
    }

}

?>
