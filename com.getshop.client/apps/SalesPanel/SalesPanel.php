<?php
namespace ns_84420130_b401_44e8_a9ab_52d6458f2c0c;

class SalesPanel extends \MarketingApplication implements \Application {
    var $orgId;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "SalesPanel";
    }

    public function render() {
        $this->includefile("salesdashboard");
    }
    
    public function searchCustomer() {
        $this->includefile("searchresult");
    }
    
    public function registerCustomer() {
        $this->includefile("registercustomer");
    }
    
    public function displayCustomer() {
        $this->setCustomer($_POST['data']['orgId']);
        $this->displayCustomerArea();
    }
    
    public function setCustomer($orgid) {
        $this->orgId = $orgid;
    }
    
    public function getSelectedCustomer() {
        return $this->getApi()->getSalesManager()->getCustomer($this->orgId);
    }
    
    public function addCustomer() {
        $orgId = $_POST['data']['orgid'];
        $this->setCustomer($orgId);
        $customer = $this->getApi()->getSalesManager()->getCustomer($orgId);
        
        foreach($_POST['data'] as $key => $value) {
            $customer->{$key} = $value;
        }
        
        $this->getApi()->getSalesManager()->saveCustomer($customer);
    }

    public function getCustomerTypes() {
        $customerTypes = array();
        $customerTypes[0] = "Website";
        $customerTypes[1] = "Booking";
        $customerTypes[2] = "Hotel";
        $customerTypes[3] = "WebShop";
        return $customerTypes;
    }
    
    public function displayCustomerArea() {
        $this->includefile("displaycustomer");
    }
    
    public function registerEvent() {
        $this->includefile("registerevent");
    }
    
    public function editEvent() {
        $this->includefile("registerevent");
    }
    
    public function printEvents($events, $printCustomer = false) {
        echo "<table width='100%'>";
        foreach($events as $event) {
            /* @var $event \core_salesmanager_SalesEvent */
            echo "<tr>";
            echo "<td align='top'>" . $event->createdByName . " - <span class='editevent' style='color:blue; cursor:pointer;' eventId='".$event->id."'>edit</span><bR>" . date("d.m.Y h:i", strtotime($event->rowCreatedDate)). "</td><bR>";
            echo "<td align='top' width='50%'>" . $event->comment . "</td>";
            echo "<td valign='right'>";
            if($event->date) {
                echo date("d.m.Y h:i", $event->date);
            } else {
                echo "No date set";
            }
            echo "</td>";
            if($printCustomer) {
                echo "<td>" . $event->orgName . "</td>";
            }
            echo "</tr>";
        }
        echo "</table>";
    }
    
    public function createEvent() {
        $event = new \core_salesmanager_SalesEvent();
        foreach($_POST['data'] as $key => $val) {
            $event->{$key} = $val;
        }
        $event->date = strtotime($event->date);
        $this->setCustomer($event->orgId);
        
        $this->getApi()->getSalesManager()->saveEvent($event);
    }
    
    public function getEventTypes() {
        $events = array();
        $events[0] = "Ring tilbake";
        $events[1] = "Ikke interresert";
        $events[2] = "Annet";
        return $events;
    }

    public function displayDashBoard() {
        $this->includefile("dashboard");
    }

}
?>
