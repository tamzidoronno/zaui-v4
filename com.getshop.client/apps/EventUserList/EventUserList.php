<?php
namespace ns_bd751f7e_5062_4d0d_a212_b1fc6ead654f;

class EventUserList extends \ns_d5444395_4535_4854_9dc1_81b769f5a0c3\EventCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "EventUserList";
    }

    public function render() {
        $this->includeFile("userlist");
    }
    
    public function removeUserFromEvent() {
        $this->getApi()->getEventBookingManager()->removeUserFromEvent($this->getBookingEngineName(), $_POST['data']['eventid'], $_POST['data']['userid'], false);
    }
    
    public function setGroupInvoiceing() {
        $this->getApi()->getEventBookingManager()->setGroupInvoiceingStatus($this->getBookingEngineName(), $_POST['data']['eventId'], $_POST['data']['userId'], $_POST['data']['groupId']);
    }
    
    public function showComments() {
        $this->includefile("showcomments");
    }
    
    public function addComment() {
        $this->getApi()->getEventBookingManager()->addUserComment($this->getBookingEngineName(), $_POST['data']['userId'], $_POST['data']['eventId'], $_POST['data']['comment']);
    }
    
    public function showSettings() {
        $this->includefile("usersettings");
    }
    
    public function setParticiationStatus() {
        $this->getApi()->getEventBookingManager()->setParticipationStatus($this->getBookingEngineName(), $_POST['data']['eventId'], $_POST['data']['userId'], $_POST['data']['status']);
    }
    public function markAsReady() {
        $this->getApi()->getEventBookingManager()->markAsReady($this->getBookingEngineName(), $_POST['data']['eventid']);
    }
    
    public function removeComment() {
        $this->getApi()->getEventBookingManager()->deleteUserComment($this->getBookingEngineName(), $_POST['data']['userid'], $_POST['data']['eventid'], $_POST['data']['commentid']);
    }

    /**
     * 
     * @param \core_usermanager_data_User $user
     */
    public function getUserAdditionalInformation($user) {
        $text1 = @$user->metaData->{"event_signon_alergic"};
        $text2 = @$user->metaData->{"event_signon_specialfoodrequest"};
        $text3 = @$user->metaData->{"event_signon_additionalinfo"};
        
        $retText = "";
        $retText .= $text1 ? "<br/> Alergic: ".nl2br($text1) : "";
        $retText .= $text2 ? "<br/> Food request: ".nl2br($text2) : "";
        $retText .= $text3 ? "<br/><br/> Comment: <br/>".nl2br($text3) : "";
        
        return $retText;
    }

    public function gsEmailSetup($model) {
        if (!$model) {
            $this->includefile("emailsettings");
            return;
        } 
        
        $this->setConfigurationSetting("crontab", $_POST['crontab']);
        $this->setConfigurationSetting("signupemail", $_POST['signupemail']);
        $this->setConfigurationSetting("removedemail", $_POST['removedemail']);
        $this->setConfigurationSetting("canceleventmail", $_POST['canceleventmail']);
        $this->setConfigurationSetting("signup_mailcontent", $_POST['signup_mailcontent']);
        $this->setConfigurationSetting("reminder_on_email_activated", $_POST['reminder_on_email_activated']);
        $this->setConfigurationSetting("signupsms", $_POST['signupsms']);
        $this->setConfigurationSetting("removedsms", $_POST['removedsms']);
        $this->setConfigurationSetting("canceleventsms", $_POST['canceleventsms']);
        $this->setConfigurationSetting("reminder_on_sms_activated", $_POST['reminder_on_sms_activated']);
        $this->setConfigurationSetting("signup_subject", $_POST['signup_subject']);
        $this->setConfigurationSetting("signup_sms_content", $_POST['signup_sms_content']);
        $this->setConfigurationSetting("removed_mail_subject", $_POST['removed_mail_subject']);
        $this->setConfigurationSetting("removed_mailcontent", $_POST['removed_mailcontent']);
        $this->setConfigurationSetting("removed_sms_content", $_POST['removed_sms_content']);
        $this->setConfigurationSetting("event_canceled_mail_content", $_POST['event_canceled_mail_content']);
        $this->setConfigurationSetting("event_canceled_sms_conent", $_POST['event_canceled_sms_conent']);
        $this->setConfigurationSetting("automatic_reminder_template_email", $_POST['automatic_reminder_template_email']);
        $this->setConfigurationSetting("automatic_reminder_template_sms", $_POST['automatic_reminder_template_sms']);
        $this->setConfigurationSetting("event_canceled_mail_subject", $_POST['event_canceled_mail_subject']);
        $this->setConfigurationSetting("automatic_reminder_template_frequence", $_POST['automatic_reminder_template_frequence']);
        
        // Transfer
        $this->setConfigurationSetting("transfermail", $_POST['transfermail']);
        $this->setConfigurationSetting("transfersms", $_POST['transfersms']);
        $this->setConfigurationSetting("transferemail_subject", $_POST['transferemail_subject']);
        $this->setConfigurationSetting("transferemail_content", $_POST['transferemail_content']);
        $this->setConfigurationSetting("transfersms_content", $_POST['transfersms_content']);
        
        // Waitinglist removed
        $this->setConfigurationSetting("removedemail_waitinglist", $_POST['removedemail_waitinglist']);
        $this->setConfigurationSetting("removedsms_waitinglist", $_POST['removedsms_waitinglist']);
        $this->setConfigurationSetting("removed_mail_subject_waitinglist", $_POST['removed_mail_subject_waitinglist']);
        $this->setConfigurationSetting("removed_mailcontent_waitinglist", $_POST['removed_mailcontent_waitinglist']);
        $this->setConfigurationSetting("removed_sms_content_waitinglist", $_POST['removed_sms_content_waitinglist']);
        
        // Waitinglist signup
        $this->setConfigurationSetting("signupemail_waitinglist", $_POST['signupemail_waitinglist']);
        $this->setConfigurationSetting("signupsms_waitinglist", $_POST['signupsms_waitinglist']);
        $this->setConfigurationSetting("signup_subject_waitinglist", $_POST['signup_subject_waitinglist']);
        $this->setConfigurationSetting("signup_mailcontent_waitinglist", $_POST['signup_mailcontent_waitinglist']);
        $this->setConfigurationSetting("signup_sms_content_waitinglist", $_POST['signup_sms_content_waitinglist']);
        
        $this->setConfigurationSetting("requestConfirmationSubject", $_POST['requestConfirmationSubject']);
        $this->setConfigurationSetting("requestConfirmationMail", $_POST['requestConfirmationMail']);
        
        $this->getApi()->getEventBookingManager()->startScheduler("booking", $_POST['crontab']);
    }

    public function setCurrentUser($user) {
        $this->currentUser = $user;
    }
    
    public function getCurrentUser() {
        return $this->currentUser;
    }

    public function setViewingWaitinglist($param0) {
        $this->inWaitingList = $param0;
    }
    
    public function isInWaitinglist() {
        return $this->inWaitingList;
    }

    public function moveUserFromWaitinglist() {
        $this->getApi()->getEventBookingManager()->transferUserFromWaitingToEvent($this->getBookingEngineName(), $_POST['data']['userid'], $_POST['data']['eventid']);
    }
    
    public function downloadReportGroupedReport() {
        $event = $this->getApi()->getEventBookingManager()->getEvent($this->getBookingEngineName(), $_POST['data']['eventid']);
        $users = $this->getApi()->getEventBookingManager()->getUsersForEvent($this->getBookingEngineName(), $event->id);
        $usersWaitingList = $this->getApi()->getEventBookingManager()->getUsersForEventWaitinglist($this->getBookingEngineName(), $event->id);
        $group = $this->getApi()->getUserManager()->getGroup($_POST['data']['groupid']);
        $groupName = "All";
        
        
        $usersFiltered = $this->filterUsers($users, $group);
        
        $usersWaitingListFiltered = $this->filterUsers($usersWaitingList, $group);
        
        $excel = array();
        $excel[] = $this->getHeaderColumns();
        $excel[] = $this->getEventInformation($event, $groupName);
        
        $excel[] = array();
        
        $groupInfos = $this->getGroupedInvoiceInformation($event, $groupName, $_POST['data']['groupid']);
        
        foreach ($groupInfos as $groupInfo) {
            $excel[] = $groupInfo;
        }
        
        $excel[] = array();
        $excel[] = ["VAT", "Reference", "Company Information", "Company Invoice Address", "Company email", "Company invoice email", "Candiate name", "Candidate email", "Comment", "Group", "Status"];
        
        foreach ($usersFiltered as $user) {
            $invoiceGroupId = @$event->groupInvoiceStatus->{$user->id};
            if ($invoiceGroupId != $_POST['data']['groupid'])
                continue;
            
            $excel[] = $this->createUserRow($user, $event, false, true);
        }
        
        $excel[] = array();
        if (count($usersWaitingListFiltered)) {
            $excel[] = ["Waitinglist"];

            foreach ($usersWaitingListFiltered as $user) {
                $excel[] = $this->createUserRow($user, $event, true, true);
            }
        }
        
        echo json_encode($excel);
    }
    
    public function deleteInvoiceGroup() {
        $this->getApi()->getEventBookingManager()->deleteInvoiceGroup($this->getBookingEngineName(), $_POST['data']['groupid']);
    }
    
    public function isInvoiceGroupActivated() {
        // Only activated for promeister norway.
        return $this->getFactory()->getStore()->id == "17f52f76-2775-4165-87b4-279a860ee92c";
    }
    
    public function downloadReport() {
        $event = $this->getApi()->getEventBookingManager()->getEvent($this->getBookingEngineName(), $_POST['data']['eventid']);
        $users = $this->getApi()->getEventBookingManager()->getUsersForEvent($this->getBookingEngineName(), $event->id);
        $usersWaitingList = $this->getApi()->getEventBookingManager()->getUsersForEventWaitinglist($this->getBookingEngineName(), $event->id);
        $group = $this->getApi()->getUserManager()->getGroup($_POST['data']['groupid']);
        $groupName = $group == null ? "All" : $group->groupName;
        
        
        $usersFiltered = $this->filterUsers($users, $group);
        
        $usersWaitingListFiltered = $this->filterUsers($usersWaitingList, $group);
        
        $excel = array();
        $excel[] = $this->getHeaderColumns();
        $excel[] = $this->getEventInformation($event, $groupName);
        
        $excel[] = array();
        $excel[] = ["VAT", "Reference", "Company Information", "Company Invoice Address", "Company email", "Company invoice email", "Candiate name", "Candidate email", "Comment", "Group", "Status", "Price"];
        
        foreach ($usersFiltered as $user) {
            $invoiceGroupId= @$event->groupInvoiceStatus->{$user->id};
            if ($invoiceGroupId)
                continue;
            
            $excel[] = $this->createUserRow($user, $event, false, false);
        }
        
        $excel[] = array();
        if (count($usersWaitingListFiltered)) {
            $excel[] = ["Waitinglist"];

            foreach ($usersWaitingListFiltered as $user) {
                $excel[] = $this->createUserRow($user, $event, true, false);
            }
        }
        
        echo json_encode($excel);
    }

    public function getHeaderColumns() {
        return ["Event", "Location", "Date", "Group", "Days"];
    }

    /**
     * 
     * @param \core_eventbooking_Event $event
     * @param \core_usermanager_data_Group $group
     * @return type
     */
    public function getEventInformation($event, $groupName) {
        return [$event->bookingItemType->name, $event->location->name." ".$event->subLocation->name, $event->mainStartDate, $groupName, count($event->days)];
    }
    
    public function getGroupedInvoiceInformation($event, $groupName, $groupId) {
        $groupInfo = $this->getApi()->getEventBookingManager()->getInvoiceGroup($this->getBookingEngineName(), $groupId);
        $company = $this->getApi()->getUserManager()->getCompany($groupInfo->companyId);
                
        $ret = array();
        $ret[] = array("Grouped invoice information");
        $ret[] = array("Group: ", $groupInfo->name);
        $ret[] = array("Price", $groupInfo->price);
        $ret[] = array("Company", $company->name, $company->vatNumber, $company->invoiceEmail);
        
        return $ret;
    }

    /**
     * 
     * @param \core_usermanager_data_User $user
     * @param \core_eventbooking_Event $event
     */
    public function createUserRow($user, $event, $waitinglist, $dropPrice) {
        $row = [];
        
        $row[] = $user->companyObject ? $user->companyObject->vatNumber : "-";
        $row[] = $user->companyObject ? $user->companyObject->reference : "-";
        $row[] = $user->companyObject ? $user->companyObject->name."\n".$user->companyObject->address->address."\n".$user->companyObject->address->postCode." - ".$user->companyObject->address->city : "-";
        $row[] = $user->companyObject && $user->companyObject->invoiceAddress ? $user->companyObject->name."\n".$user->companyObject->invoiceAddress->address."\n".$user->companyObject->invoiceAddress->postCode." - ".$user->companyObject->invoiceAddress->city : "-";
        $row[] = $user->companyObject ? $user->companyObject->email : "-";
        $row[] = $user->companyObject ? $user->companyObject->invoiceEmail : "-";
        $row[] = $user->fullName;
        $row[] = $user->emailAddress;
        $row[] = $this->createComment($user, $event);
        
        $groupName = "";
        
        if ($user->companyObject != null && $user->companyObject->groupId) {
            $group = $this->getApi()->getUserManager()->getGroup($user->companyObject->groupId);
            if ($group) {
                $groupName = $group->groupName;
            }
        }
        
        $row[] = $groupName;
        
        if (!$waitinglist) {
            $row[] = $this->getParticiationText(@$event->participationStatus->{$user->id});
            if (!$dropPrice) {
                $row[] = $this->getPriceForEvent($event, $user);
            }
        }
        
        return $row;
    }

    /**
     * 
     * @param \core_usermanager_data_User[] $users
     * @param type $group
     * @return type
     */
    public function filterUsers($users, $group) {
        if (!$group) {
            return $users;
        }
        
        $retUsers = [];
        
        if (!count($users) || !is_array($users)) {
            return $users;
        }
        
        foreach ($users as $user) {
            if ($user->companyObject && $user->companyObject->groupId == $group->id) {
                $retUsers[] = $user;
            }
        }
        
        return $retUsers;
    }

    public function createComment($user, $event) {
        $comments = @$event->comments->{$user->id};
        if ($comments) {
            $useComment = "";
            foreach ($comments as $comment) {
                $useComment .= $comment->comment." \n ";
            }
            
            return $useComment;
        } 
        
        return "";
    }

    public function getParticiationText($participatedStatus) {
        if (!$participatedStatus || $participatedStatus == "participated") {
            return $this->__w("Participated");
        }
        
        if ($participatedStatus == "participated_free") {
            return $this->__w("Participated free");
        }
        
        if ($participatedStatus == "participated_50") {
            return $this->__w("Not participated illegal reason");
        }
        
        if ($participatedStatus == "not_participated") {
            return $this->__w("Not participated legal reason");
        }
        
        return "Unkown";
        
    }

    /**
     * 
     * @param \core_eventbooking_Event $event
     * @param \core_usermanager_data_User $user
     */
    public function getPriceForEvent($event, $user) {
        $price = $this->getApi()->getEventBookingManager()->getPriceForEventTypeAndUserId("booking", $event->id, $user->id);
        if (!$price) {
            return "";
        }
        
        $status = @$event->participationStatus->{$user->id};
        if (!$status || $status == "participated") {
            return $price;
        }
        
        if ($status == "participated_free") {
            return "0,-";
        }
        
        if ($status == "participated_50") {
            return $price/2;
        }
        
        if ($status == "not_participated") {
            return "0,-";
        }
        
        return "";
    }

    public function getCompanyOwners($company) {
        $users = $this->getApi()->getUserManager()->getUsersByCompanyId($company->id);
        $owners = "";
        foreach ($users as $user) {
            if ($user->isCompanyOwner) {
                $owners .= $user->fullName." / ".$user->emailAddress . " / " .$user->cellPhone."<br/>";
            }
        }
        
        if (!$owners) {
            $owners = "None";
        }
        
        return $owners;
    }

}
?>