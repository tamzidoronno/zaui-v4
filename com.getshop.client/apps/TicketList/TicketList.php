<?php
namespace ns_419c41e7_1dd5_4c96_b820_c1b77a2fffa3;

class TicketList extends \MarketingApplication implements \Application {
    public $states = array('CREATED','COMPLETED');
    public $tickettype = array('SUPPORT','BUG', 'FEATURE');
    
    private $filter = null;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "TicketList";
    }

    public function render() {
        $this->includefile("filter");
        $this->renderTable();
    }
    
    public function subMenuChanged() {
        
    }
    
    public function reloadApp() {
        
    }
    
    public function setFilterState() {
        $_SESSION['ns_419c41e7_1dd5_4c96_b820_c1b77a2fffa3_statefilter'] = $_POST['data']['state'];
    }
    
    public function setFilterType() {
        $_SESSION['ns_419c41e7_1dd5_4c96_b820_c1b77a2fffa3_typefilter'] = $_POST['data']['type'];
    }
    
    public function deleteTicket() {
        $this->getApi()->getTicketManager()->deleteTicket($_POST['data']['id']);
    }
    
   
    public function formatState($row) {
        return $row->currentState;
    }
    
    public function formatUser($row) {
        $user = $this->getApi()->getUserManager()->getUserById($row->userId);
        if (!$user) {
            return "N/A";
        }
        return $user->fullName;
    }

    public function TicketManager_getAllTickets() {
        $this->includefile("ticketview");
    }
    
    public function renderTable() {
        $attributes = array(
            array('id', 'gs_hidden', 'id'),
            array('incrementalId', 'TICKETID', 'incrementalId'),
            array('rowCreatedDate', 'REG', 'rowCreatedDate'),
            array('title', 'TITLE', 'title'),
            array('userId', 'USER', 'userId', 'formatUser'),
            array('timeSpent', 'timeSpent', 'timeSpent',null),
            array('timeInvoice', 'timeInvoice', 'timeInvoice',null),
            array('currentState', 'STATE', 'currentState', 'formatState'),
            array('type', 'TYPE', 'type')
        );
        
        if (!$this->filter) {
            $this->filter = new \core_ticket_TicketFilter();
        }
        
        $this->filter->state = isset($_SESSION['ns_419c41e7_1dd5_4c96_b820_c1b77a2fffa3_statefilter']) ? $_SESSION['ns_419c41e7_1dd5_4c96_b820_c1b77a2fffa3_statefilter'] : "";
        $this->filter->type = isset($_SESSION['ns_419c41e7_1dd5_4c96_b820_c1b77a2fffa3_typefilter']) ? $_SESSION['ns_419c41e7_1dd5_4c96_b820_c1b77a2fffa3_typefilter'] : "";
        
        $args = array($this->filter);
        
        $table = new \GetShopModuleTable($this, 'TicketManager', "getAllTickets", $args, $attributes);
        $table->render();
        
        $data = $table->getDate();
        
        $totalTimeSpent = 0;
        $totalTimeInvoice = 0;
        foreach($data as $obj) {
            $totalTimeSpent += $obj->timeSpent;
            $totalTimeInvoice += $obj->timeInvoice;
        }

        echo "Time spent in total: $totalTimeSpent<br>";
        echo "Time spent to invoice: $totalTimeInvoice<br>";
    }
    
    public function addEvent() {
        $event = new \core_ticket_TicketEvent();
        $event->content = $_POST['data']['content'];
        $event->state = $_POST['data']['state'];
        $event->eventType = $_POST['data']['eventType'];
        $this->getApi()->getTicketManager()->updateEvent($_POST['data']['id'], $event);
    }

    /**
     * 
     * @return \core_ticket_Ticket
     */
    public function getCurrentTicket() {
        return $this->getApi()->getTicketManager()->getTicket($_POST['data']['id']);
    }
    
    public function saveTicket() {
        $ticket = $this->getApi()->getTicketManager()->getTicket($_POST['data']['id']);
        $ticket->title = $_POST['data']['title'];
        $ticket->currentState = $_POST['data']['state'];
        $ticket->type = $_POST['data']['type'];
        $ticket->timeSpent = $_POST['data']['timespent'];
        $ticket->timeInvoice = $_POST['data']['timeInvoice'];
        $ticket->rowCreatedDate = $this->convertToJavaDate(strtotime($_POST['data']['createddate']));
        if ($_POST['data']['completeddate']) {
            $ticket->dateCompleted = $this->convertToJavaDate(strtotime($_POST['data']['completeddate']));    
        }
        $this->getApi()->getTicketManager()->saveTicket($ticket);
    }

    public function renderStates($ticket) {
        ?>
        <select class="gsniceselect1" gsname="state">
        <?
        foreach ($this->states as $state) {
            $readable = ucwords($state);
            $selected = $ticket->currentState == $state ? "selected='true'" : "";
            echo "<option $selected value='$state'>$readable</option>";
        }
        ?>
        </select>
        <?
        
    }

    public function setFilter($filter) {
        $this->filter = $filter;
    }
    
    public function unsetTransferred() {
        $ticket = $this->getApi()->getTicketManager()->getTicket($_POST['data']['id']);
        $ticket->transferredToAccounting = false;
        $this->getApi()->getTicketManager()->saveTicket($ticket);
    }
}
?>
