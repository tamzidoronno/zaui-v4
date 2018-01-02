<?php
namespace ns_9c29f94a_6c42_4590_aa1c_7736c58ff0a5;

class TicketCreateNew extends \MarketingApplication implements \Application, \ns_b5e9370e_121f_414d_bda2_74df44010c3b\GetShopQuickUserCallback {
    public function getDescription() {
        
    }

    public function getName() {
        return "TicketCreateNew";
    }

    public function render() {
        $this->includefile("createTicket");
        
        if (isset($_POST['data']['title'])) {
            echo "<h2><i class='fa fa-check'></i> ".$this->__f("Ticket created")."</h2>";
        }
    }

    public function changeUser($user) {}

    public function saveUser($user) {}
    
    public function createCompany() {
        // Not implemented yet.
    }

   
    public function createNewUser() {
        $user = new \core_usermanager_data_User();
        $user->fullName = $_POST['data']['name'];
        $user = $this->getApi()->getUserManager()->createUser($user);
        return $user;
    }

    public function createNewTicket() {
        
        if (!isset($_POST['data']['userid']) || !$_POST['data']['userid']) {
            $obj = $this->getStdErrorObject(); // Get a default error message
            $obj->fields->errorMessage = "Please select a valid user"; // The message you wish to display in the gserrorfield
            $obj->gsfield->userid = 1; 
            $this->doError($obj); 
        }
        
        if (!isset($_POST['data']['title']) || !$_POST['data']['title']) {
            $obj = $this->getStdErrorObject(); // Get a default error message
            $obj->fields->errorMessage = "Title can not be empty"; // The message you wish to display in the gserrorfield
            $obj->gsfield->title = 1; 
            $this->doError($obj); 
        }

        $ticket = new \core_ticket_Ticket();
        $ticket->title = $_POST['data']['title'];
        $ticket->userId = $_POST['data']['userid'];
        $ticket->type = $_POST['data']['type'];
        $this->getApi()->getTicketManager()->saveTicket($ticket);
    }
    
    public function setUser($user) {
        $this->user = $user;
    }
    
}
?>
