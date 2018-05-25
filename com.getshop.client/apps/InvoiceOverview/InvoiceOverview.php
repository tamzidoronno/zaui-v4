<?php
namespace ns_3746f382_0450_414c_aed5_51262ae85307;

class InvoiceOverview extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "InvoiceOverview";
    }

    public function render() {
        $this->includefile("filterheader");
        $this->includefile("filterresult");
    }

    /**
     * 
     * @return \core_ordermanager_data_OrderResult[]
     */
    public function getFilteredResult() {
        return $this->getApi()->getOrderManager()->getOrdersByFilter($this->getFilter());
    }
    
    /**
     * 
     * @param \core_ordermanager_data_OrderResult $row
     * @return 
     */
    public function formatOrderDate($row) {
        return date("d.m.Y", strtotime($row->orderDate));
    }
    
    /**
     * 
     * @param \core_ordermanager_data_OrderResult $row
     * @return 
     */
    public function formatPaymentDate($row) {
        if(!$row->paymentDate) {
            return "N/A";
        }
        return date("d.m.Y", strtotime($row->paymentDate));
    }
    
    /**
     * 
     * @param \core_ordermanager_data_OrderResult $row
     * @return 
     */
    public function formatDueDate($row) {
        if(!$row->dueDate) {
            return "N/A";
        }
        return date("d.m.Y", strtotime($row->dueDate));
    }
    
    /**
     * 
     * @return \core_ordermanager_data_OrderFilter
     */
    public function getFilter() {
        if(isset($_SESSION['orderfilter'])) {
            return json_decode($_SESSION['orderfilter']);
        }
        return new \core_ordermanager_data_OrderFilter();
    }
    
    public function updatefilter() {
        $filter = new \core_ordermanager_data_OrderFilter();
        $filter->start = $this->convertToJavaDate(strtotime($_POST['data']['startdate']));
        $filter->end = $this->convertToJavaDate(strtotime($_POST['data']['enddate']));
        $filter->state = $_POST['data']['state'];
        $filter->type = $_POST['data']['filtertype'];
        $filter->paymentMethod = $_POST['data']['paymentmethod'];
        
        $_SESSION['orderfilter'] = json_encode($filter);
        
    }
    
    public function formatStatus($row) {
        $states = $this->getStates();
        return $states[$row->status];
    }

    public function getStates() {
        $states = array();
        $states['0'] = "All";
        $states['1'] = "Created";
        $states['2'] = "Waiting for payment";
        $states['3'] = "Payment failed";
        $states['4'] = "Completed";
        $states['5'] = "Canceled";
        $states['6'] = "Sent";
        $states['7'] = "Payment completed";
        $states['8'] = "Collection failed";
        $states['9'] = "Need collecting";
        $states['10'] = "Send to invoice";

        return $states;
    }

}
?>
