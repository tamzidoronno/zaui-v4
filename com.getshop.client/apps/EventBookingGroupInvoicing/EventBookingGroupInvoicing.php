<?php
namespace ns_7214108c_a926_4057_8bc9_6b43cd6917f0;

class EventBookingGroupInvoicing extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "EventBookingGroupInvoicing";
    }

    public function render() {
        if (isset($_POST['event']) && $_POST['event'] === "showModal") {
            $this->clearSessionData();
        }
        
        $this->includefile("editgroup");
    }
    
    public function clearSessionData() {
        unset($_SESSION['EventBookingGroupInvoicing_name']);
        unset($_SESSION['EventBookingGroupInvoicing_price']);
        unset($_SESSION['EventBookingGroupInvoicing_step']);
    }
    
    public function selectCompany() {
        $invoiceGroup = new \core_eventbooking_InvoiceGroup();
        $invoiceGroup->name = $_SESSION['EventBookingGroupInvoicing_name'];
        $invoiceGroup->price = $_SESSION['EventBookingGroupInvoicing_price'];
        $invoiceGroup->vatnumber = $_POST['data']['vatnumber'];
        $invoiceGroup->eventId = $this->getModalVariable("eventid");
        $this->getApi()->getEventBookingManager()->saveGroupInvoicing("booking", $invoiceGroup);
        $this->clearSessionData();
        $this->closeModal();
    }
    
    public function goToStep2() {
        $_SESSION['EventBookingGroupInvoicing_name'] = $_POST['data']['name'];
        $_SESSION['EventBookingGroupInvoicing_price'] = $_POST['data']['price'];
        $_SESSION['EventBookingGroupInvoicing_step'] = "2";
    }
    
    public function serachForCompanies() {}
    
    public function isStep1() {
        if (!isset($_SESSION['EventBookingGroupInvoicing_step']) || $_SESSION['EventBookingGroupInvoicing_step'] == "1") {
            return true;
        }
        
        return false;
    }
    
    public function isStep2() {
        if (isset($_SESSION['EventBookingGroupInvoicing_step']) && $_SESSION['EventBookingGroupInvoicing_step'] == "2") {
            return true;
        }
        
        return false;
    }
}
?>
