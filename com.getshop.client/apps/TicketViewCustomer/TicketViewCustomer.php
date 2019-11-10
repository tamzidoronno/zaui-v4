<?php
namespace ns_f5e525cc_f11e_4611_93bb_1afacd9aade5;

class TicketViewCustomer extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "TicketViewCustomer";
    }

    public function render() {
        $this->includefile("ticketview");
        $this->markTicketAsReadByGetShopUser();
    }
    
    public function fileUploaded() {
        $this->setGetVariables();
        
        if ($this->isGetShop()) {
            $this->getApi()->getTicketManager()->addAttachmentToTicket($_GET['ticketId'], $_POST['data']['uuid']);
        } else {
            $this->getSystemGetShopApi()->getCustomerTicketManager()->addAttachmentToTicket($this->getFactory()->getStore()->id, $_GET['ticketToken'], $_POST['data']['uuid']);
        }
    }
    
    public function ignorehideticket() {
        $ticketId = $_POST['data']['ticketid'];
        $this->getApi()->getTicketManager()->toggleIgnoreTicket($ticketId);
    }
    
    public function updateResponseText() {
        $_GET['ticketId']  = $_POST['data']['ticketid'];
        $content = $_POST['data']['content'];
        $ticketId = $_POST['data']['ticketid'];
        $contentId = $_POST['data']['contentid'];
        $this->getApi()->getTicketManager()->updateContent($ticketId, $contentId, $content);
    }
    
    public function getStates() {
        $states = array();
        $states['CREATED'] = "Created";
        $states['COMPLETED'] = "Completed";
        $states['CLOSED'] = "Closed";
        $states['REPLIED'] = "Replied";
        $states['INITIAL'] = "Initial";
        return $states;
    }
    
    public function getTypes() {
        $types = array();
        $types['SUPPORT'] = "SUPPORT";
        $types['MEETING'] = "MEETING";
        $types['PHONE'] = "PHONE";
        $types['BUG'] = "BUG";
        $types['ACCOUNTING'] = "ACCOUNTING";
        $types['HARDWARE'] = "HARDWARE";
        $types['BACKLOG'] = "BACKLOG";
        $types['SETUP'] = "SETUP";
        $types['EXTRAORDINARY'] = "EXTRAORDINARY";
        $types['UNKOWN'] = "UNKOWN";
        return $types;
    }
    
    public function replyTicket() {
        $this->setGetVariables();
        
        $content = new \core_ticket_TicketContent();
        $content->content = nl2br($_POST['data']['content']);

        if ($this->isGetShop()) {
            $this->getApi()->getTicketManager()->addTicketContent($_GET['ticketId'], $content);
            $this->getApi()->getTicketManager()->markTicketAsRead($_GET['ticketId']);
            $this->getApi()->getTicketManager()->markAsRepied($_GET['ticketId']);
            
            $ticket = $this->getApi()->getTicketManager()->getTicket($_GET['ticketId']);
            $ticket->timeSpent += (($_POST['data']['secondsused']/60)/60);
            $ticket->timeSpentAtDate->{time()*1000} = (($_POST['data']['secondsused']/60)/60);
            $ticket->type = $_POST['data']['submit'];
            $ticket->currentState = $_POST['data']['state'];
            if($_POST['data']['ignoreinvoice'] != "true") {
                $ticket->timeInvoice += (($_POST['data']['secondsused']/60)/60);
                $ticket->timeInvoiceAtDate->{time()*1000} = (($_POST['data']['secondsused']/60)/60);
            }
            $this->getApi()->getTicketManager()->saveTicket($ticket);
            $this->getApi()->getTicketManager()->markTicketAsUnread($ticket->id);

        } else {
            $this->getSystemGetShopApi()->getCustomerTicketManager()->addContent($this->getFactory()->getStore()->id, $_GET['ticketToken'], $content);
        }
    }
    
    public function updateTimeOnTicket() {
        $ticket = $this->getApi()->getTicketManager()->getTicket($_POST['data']['ticketId']);
        if($_POST['data']['timetype'] == "billing") {
            $ticket->timeInvoiceAtDate->{$_POST['data']['time']} = $_POST['data']['value'];
        } else {
            $ticket->timeSpentAtDate->{$_POST['data']['time']} = $_POST['data']['value'];
        }
        $this->getApi()->getTicketManager()->saveTicket($ticket);
    }
    
    public function isGetShop() {
        return $this->getFactory()->getStore()->id == "13442b34-31e5-424c-bb23-a396b7aeb8ca";
    }

    public function setGetVariables() {
        $_GET['ticketToken'] = $_POST['data']['ticketToken'];
        
        if (isset($_POST['data']['tickettoken'])) {
            $_GET['ticketToken'] = $_POST['data']['tickettoken'];
        }
        
        if (isset($_POST['data']['ticketid'])) {
            $_GET['ticketId'] = $_POST['data']['ticketid'];
        }
        
        if (!isset($_GET['ticketId']) && $this->isGetShop()) {
            $_GET['ticketId'] = $this->getApi()->getTicketManager()->getTicketIdByToken($_GET['ticketToken']);
        }
        
    }

    public function changeState() {
        $this->setGetVariables();
        $this->getApi()->getTicketManager()->changeStateOfTicket($_GET['ticketId'], $_POST['data']['gsvalue']);
    }
    
    public function changeAssignment() {
        $this->setGetVariables();
        $this->getApi()->getTicketManager()->assignTicketToUser($_GET['ticketId'], $_POST['data']['gsvalue']);
    }
    
    public function setTimeSpent() {
        $this->setGetVariables();
        $ticket = $this->getApi()->getTicketManager()->getTicket($_GET['ticketId']);
        $ticket->timeSpent = $_POST['data']['gsvalue'];
        $this->getApi()->getTicketManager()->saveTicket($ticket);
    }
    
    public function setTimeInvoice() {
        $this->setGetVariables();
        $ticket = $this->getApi()->getTicketManager()->getTicket($_GET['ticketId']);
        $ticket->timeInvoice = $_POST['data']['gsvalue'];
        $this->getApi()->getTicketManager()->saveTicket($ticket);
    }
    
    public function changeType() {
        $this->setGetVariables();
        $this->getApi()->getTicketManager()->changeType($_GET['ticketId'], $_POST['data']['gsvalue']);
    }
    
    public function validationCompleted() {
        $this->setGetVariables();
        $ticket = $this->getApi()->getTicketManager()->getTicket($_GET['ticketId']);
        $ticket->hasBeenValidedForTimeUsage = true;
        $this->getApi()->getTicketManager()->saveTicket($ticket);
    }

    public function markTicketAsReadByGetShopUser() {
        $this->setGetVariables();
        if ($this->isGetShop()) {
            $this->getApi()->getTicketManager()->markTicketAsRead($_GET['ticketId']);
        }
    }

    public function reconnect() {
        $this->setGetVariables();
        $this->getApi()->getTicketManager()->reconnectTicket($_GET['ticketId']);
    }
    
    public function reOpenTicket() {
        $this->setGetVariables();
        $this->getSystemGetShopApi()->getCustomerTicketManager()->reOpenTicket($this->getFactory()->getStore()->id, $_GET['ticketToken']);
    }
    
    public function createSubTask() {
        $_GET['ticketId'] = $_POST['data']['ticketid'];
        $this->getApi()->getTicketManager()->addSubTask($_POST['data']['ticketid'], $_POST['data']['title']);
    }
    
    public function toggleSubClass() {
        $this->getApi()->getTicketManager()->toggleSubTask($_POST['data']['ticketid'], $_POST['data']['subtaskid']);
        $_GET['ticketId'] = $_POST['data']['ticketid'];
    }
    
}
?>
