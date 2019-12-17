<?php

namespace ns_f1706b4c_f779_4eb7_aec3_ee08f182e090;

class GetShopInbox extends \MarketingApplication implements \Application {

    var $admins;
    var $isCompletedEmails;
    
    public function getDescription() {
        
    }
    
    public function getAllTypes() {
        $types = array();
        $types[0] = "Undefined";
        $types[1] = "Support";
        $types[2] = "Bug";
        $types[3] = "Feature";
        $types[4] = "Meeting";
        $types[5] = "Setup";
        return $types;
    }

    public function fetchemails() {
        $this->getApi()->getGmailApiManager()->fetchAllMessages();
    }
    
    public function getName() {
        return "GetShopInbox";
    }

    public function markEmailAsCompleted() {
        $this->getApi()->getGmailApiManager()->updateTimeSpentOnMessage($_POST['data']['msgid'], 0, true);
    }
    
    public function changeTypeOnMessage() {
        $this->getApi()->getGmailApiManager()->changeTypeOnMessage($_POST['data']['msgid'], $_POST['data']['type']);
    }
    
    public function getSelectedStart() {
        if(!isset($_SESSION['ns_f1706b4c_f779_4eb7_aec3_ee08f182e090_start'])) {
            return date("m/01/Y", time());
        }
        return $_SESSION['ns_f1706b4c_f779_4eb7_aec3_ee08f182e090_start'];
    }
    
    public function getSelectedEnd() {
        if(!isset($_SESSION['ns_f1706b4c_f779_4eb7_aec3_ee08f182e090_end'])) {
            return date("m/t/Y", time());
        }
        return $_SESSION['ns_f1706b4c_f779_4eb7_aec3_ee08f182e090_end'];
    }
    
    
    public function updateDateRange() {
        $_SESSION['ns_f1706b4c_f779_4eb7_aec3_ee08f182e090_end'] = $_POST['data']['enddate'];
        $_SESSION['ns_f1706b4c_f779_4eb7_aec3_ee08f182e090_start'] = $_POST['data']['startdate'];
    }
    
    
    public function quickSearchCustomer() {
        if(!$_POST['data']['keyword']) {
            return;
        }
        $systems = $this->getApi()->getSystemManager()->findSystem($_POST['data']['keyword']);
        echo "<table cellpadding='1' cellspacing='1' style='margin:10px;' width='100%'>";
        foreach($systems as $res) {
            echo "<tr>";
            echo "<td>".$res->systemName."</td>";
            echo "<td>".$res->webAddresses."</td>";
            $company = $this->getApi()->getUserManager()->getCompany($res->companyId);
            echo "<td>".$company->name."</td>";
            echo "<td><span class='shop_button startticket' systemid='".$res->id."' address='".$res->webAddresses."'>Start ticket</span> <span class='shop_button' onclick='window.open(\"https://".$res->webAddresses."/totp.php\", \"fdsafasf\")'>Open system</span></td>";
            echo "</tr>";
        }
        echo "</table>";
        
    }
    
    public function render() {
        
        if(isset($_GET['displayCustomerTickets'])) {
            $_POST['data']['tab'] = "customerinbox";
            $_SESSION['ns_f1706b4c_f779_4eb7_aec3_ee08f182e090_currentcustomer'] = $_GET['displayCustomerTickets'];
            $this->changeMenu();
        }
        
        
        echo "<div class='leftmenu'>";
            $this->includefile("leftmenu");
        echo "</div>";

        echo "<div class='workarea'>";
        $this->includefile("quicksearchsystem");
//            $this->includefile("topmenu");
            echo "<div class='emaillistarea'>";
                if ($this->getCurrentTab() == "notifications") {
                    $this->includefile("notifications");
                } else if($this->getCurrentTab() == "pretickets") {
                    $this->includefile("pretickets");
                } else if($this->getCurrentTab() == "customerinbox") {
                    $this->includefile("customerstats");
                } else if($this->getCurrentTab() == "statistics") {
                    $this->includefile("statistics");
                } else if($this->getCurrentTab() == "inprogress") {
                    $this->includefile("inprogress");
                } else if($this->getCurrentTab() == "search") {
                    $this->includefile("searchTickets");
                } else {
                    $this->includefile("ticketlist");
                }
                
            echo "</div>";
        echo "</div>";
    }
    
    public function searchTickets() {
    }

    public function isCompletedEmails() {
        return $this->isCompletedEmails;
    }
    
    public function getSelectedType() {
        if(isset($_SESSION['selectetypeinbox'])) {
            return $_SESSION['selectetypeinbox'];
        }
        return -1;
    }
    
    public function changeSelectedType() {
        $_SESSION['selectetypeinbox'] = $_POST['data']['typeid'];
    }
    
    /**
     * @return \core_googleapi_GmailMessage[]
     */
    public function getEmails() {
        $filter = new \core_googleapi_GmailMessageFilter();
        $filter->type = $this->getSelectedType();
        $filter->completed = $this->isCompletedEmails();
        
        if (!$this->isUnassignedView()) {
            $filter->userId = $this->getApi()->getUserManager()->getLoggedOnUser()->id;
        }
        
        return $this->getApi()->getGmailApiManager()->getEmails($filter);
    }

    public function doConnection() {
        $this->getApi()->getGmailApiManager()->connectMessageToCompany($_POST['data']['msgid'], $_POST['data']['companyid']);
    }

    public function changeMenu() {
        $_SESSION['ns_f1706b4c_f779_4eb7_aec3_ee08f182e090_current_view'] = $_POST['data']['tab'];
    }

    public function assignTo() {
        $userId = $_POST['data']['userid'];
        foreach ($_POST['data']['msgs'] as $msgId) {
            $this->getApi()->getGmailApiManager()->assignMessageToUser($msgId, $userId);
        }
    }

    public function markAsIgnored() {
        foreach ($_POST['data']['msgs'] as $msgId) {
            $this->getApi()->getGmailApiManager()->markAsArchived($msgId);
        }
    }

    public function createMailView() {
        $_SESSION['ns_f1706b4c_f779_4eb7_aec3_ee08f182e090_currentmail'] = $_POST['data']['msgid'];
        $this->includefile("mailview");
        die();
    }
    
    public function updateTimeSpent() {
        $msgId = $_POST['data']['msgid'];
        $timeSpent = $_POST['data']['time'];
        $msg = $this->getApi()->getGmailApiManager()->getMessageLight($msgId);
        $this->getApi()->getGmailApiManager()->updateTimeSpentOnMessage($msgId, $timeSpent, $msg->completed);
    }

    public function sendReply() {
        $lightMsg = $this->getApi()->getGmailApiManager()->getMessageLight($_POST['data']['msgid']);
        $response = $_POST['data']['content'];
        $timeSpent = $_POST['data']['timespent'];
        $response .= "On ".date('d.m.Y H:i', strtotime($lightMsg->date)).", ".htmlentities($lightMsg->from)." wrote:";
        $response .= "<div style='border-left: solid 2px #DDD; padding-left: 20px; '>".$this->getContent($lightMsg)."</div>";
        $this->getApi()->getGmailApiManager()->replyEmail($lightMsg->id, $response);
        $this->getApi()->getGmailApiManager()->updateTimeSpentOnMessage($lightMsg->id, $timeSpent, true);
    }

    public function getContent($lightMsg) {
        $parts = $this->getApi()->getGmailApiManager()->getMessageParts($lightMsg->messageId);

        foreach ($parts as $part) {
            if ($part->base64 && $part->contentId) {
                $cid = str_replace("<", "", $part->contentId);
                $cid = str_replace(">", "", $cid);
                $imgtypes = explode(";", $part->contentType);
                $imgtype = $imgtypes[0];

                foreach ($parts as $ipart) {
                    $ipart->html = str_replace("cid:" . $cid, "data:" . $imgtype . ";base64," . $part->base64, $ipart->html);
                }
            }
        }

        $htmlPartFound = false;
        $content = "";
        
        foreach ($parts as $part) {
            if ($part->html) {
                $htmlPartFound = true;
                $content = $part->html;
            }
        }

        if (!$htmlPartFound) {
            foreach ($parts as $part) {
                if ($part->text) {
                    $content = nl2br($part->text);
                }
            }
        }
        
        return $content;
    }

    public function setCurrentEmail($email) {
        $this->currentEmail = $email;
    }
    public function getCurrentEmail() {
        return $this->currentEmail;
    }
    
    public function getAdmins() {
        if(!$this->admins) {
            $this->admins = $this->getApi()->getUserManager()->getUsersByType(100);
        }
        return $this->admins;
    }

    public function isUnassignedView() {
        return isset($_SESSION['ns_f1706b4c_f779_4eb7_aec3_ee08f182e090_current_view']) && $_SESSION['ns_f1706b4c_f779_4eb7_aec3_ee08f182e090_current_view'] == "unassigned";
    }

    public function getTicketFilter() {
        $filter = new \core_ticket_TicketFilter();
        return $filter;
    }

    public function getTickets() {
        $filter = new \core_ticket_TicketFilter();
        
        if ($this->getCurrentTab() == "unassigned") {
            $filter->state = "CREATED";
//            $filter->type = "UNKOWN";
            $filter->uassigned = true;
        }
        
        if ($this->getCurrentTab() == "yourcases") {
            $filter->assignedTo = \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->id;
        }
        
        if ($this->getCurrentTab() == "billing") {
            $filter->checkForBilling = true;
            $filter->state = "COMPLETED";
        }
        if ($this->getCurrentTab() == "pretickets") {
            $filter->type = "SETUP";
            $filter->state = "INITIAL";
        }
        if ($this->getCurrentTab() == "inprogress") {
            $filter->type = "SETUP";
        }
        
        if ($this->getCurrentTab() == "backlog") {
            $filter->type = "BACKLOG";
        }

        $result = $this->getApi()->getTicketManager()->getAllTickets($filter);
        
        if ($this->getCurrentTab() == "unassigned") {
            $filter->state = "REPLIED";
            $repliedresult = (array)$this->getApi()->getTicketManager()->getAllTickets($filter);
            $result = array_merge((array)$result, (array)$repliedresult);
            
            $filteredresult = array();
            foreach($result as $k => $obj) {
                if($obj->type == "BACKLOG") {
                    continue;
                }
                if($obj->type == "SETUP") {
                    continue;
                }
                $filteredresult[] = $obj;
            }
            $result = $filteredresult;
            
        }
        if ($this->getCurrentTab() == "inprogress") {
            $newResult = array();
            foreach($result as $r) {
                if($r->currentState == "INITIAL") {
                    continue;
                }
                $newResult[] = $r;
            }   
            $result = $newResult;
        }
        
        return $result;
    }

    public function getSetupTickets() {
        $filter = new \core_ticket_TicketFilter();
        $filter->type = "SETUP";
        return $this->getApi()->getTicketManager()->getAllTickets($filter);
    }
    
    
    public function getCurrentTab() {
        if (!isset($_SESSION['ns_f1706b4c_f779_4eb7_aec3_ee08f182e090_current_view'])) {
            return "yourcases";
        }
        
        return $_SESSION['ns_f1706b4c_f779_4eb7_aec3_ee08f182e090_current_view'];
    }

    public function getRightList() {
        if ($this->getCurrentTab() == "yourcases") {
            return $this->getApi()->getTicketManager()->getLastTicketContent(\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->id);
        }
        
        return $this->getApi()->getTicketManager()->getLastTicketContent("");
    }

    public function savePushOver() {
        $obj = new \core_ticket_TicketUserPushover();
        $obj->userId = \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->id;
        $obj->receiveMessagesForUnassignedTickets = $_POST['data']['receiveMessagesForUnassignedTickets'];
        $this->getApi()->getTicketManager()->savePushOverSettings($obj, $_POST['data']['pushovertoken']);
    }
    
    public function createSetupTicket() {
        $title = $_POST['data']['title'];
        $ticketId = $this->getApi()->getTicketManager()->createSetupTicket($title);
    }

    public function getTicketSearchResult() {
        return $this->getApi()->getTicketManager()->searchTicket($_POST['data']['keyword']);
    }

    public function getInProgressTickets() {
        
    }

}

?>

