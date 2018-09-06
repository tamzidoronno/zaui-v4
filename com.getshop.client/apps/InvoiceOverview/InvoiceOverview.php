<?php
namespace ns_3746f382_0450_414c_aed5_51262ae85307;

class InvoiceOverview extends \WebshopApplication implements \Application,\ns_b5e9370e_121f_414d_bda2_74df44010c3b\GetShopQuickUserCallback {

    private $userToCreateOrderOn = null;

    public function getDescription() {
        
    }

    public function getName() {
        return "InvoiceOverview";
    }
    
    public function changeUser($user) {
    }
    
    public function createCompany() {
    }
    
    public function saveUser($user) {
        $_SESSION['invoiceoverviewcreateorderonuser'] = $user->id;
    }
    
    public function loadQuickUser() {
        $quser = new \ns_b5e9370e_121f_414d_bda2_74df44010c3b\GetShopQuickUser();
        
        $xtraargs = array();
        $xtraargs['savebtnname'] = "Create order";
        $xtraargs['avoidprintuseraftersave'] = "true";
        
        $quser->printEditDirect = true;
        $quser->hideWarning = true;
        $quser->setExtraArgs($xtraargs);
        $quser->renderApplication(true, $this);
    }
    
    public function createNewUser() {
        
    }


    public function render() {
        $this->includefile("filterheader");
        if(isset($_SESSION['invoiceoverviewcreateorderonuser']) && $_SESSION['invoiceoverviewcreateorderonuser']) {
            echo "Create order on user";
            echo "<pre>";
            print_r($_SESSION['invoiceoverviewcreateorderonuser']);
            echo "</pre>";
        } else {
            $this->includefile("filterresult");
        }
        $_SESSION['invoiceoverviewcreateorderonuser']=null;
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
        if(!$row->orderDate) {
            return "N/A";
        }
        
        return "<span getshop_sorting='".strtotime($row->orderDate)."'>" . date("d.m.Y", strtotime($row->orderDate)) . "</span>";
    }
    
    public function InvoiceOverview_loadOrder() {
        $app = new \ns_bce90759_5488_442b_b46c_a6585f353cfe\EcommerceOrderView();
        $app->loadOrder($_POST['data']['id']);
        $app->renderApplication(true, $this);
    }    
    
    public function addCustomerToFilter() {
        $filter = $this->getFilter();
        $filter->customer[] = $_POST['data']['userid'];
        $_SESSION['orderfilter'] = json_encode($filter);
        $this->printCustomersOnFilter();
    }
    
    public function removeCustomerFromFilter() {
        $filter = $this->getFilter();
        $newFilter = array();
        foreach($filter->customer as $id) {
            if($id == $_POST['data']['userid']) {
                continue;
            }
            $newFilter[] = $id;
        }
        $filter->customer = $newFilter;
        $_SESSION['orderfilter'] = json_encode($filter);
        $this->printCustomersOnFilter();
    }
    
    public function searchCustomer() {
        $search = $_POST['data']['customer'];
        $result = $this->getApi()->getUserManager()->findUsers($search);
        echo "<table>";
        foreach($result as $usr) {
            echo "<tr>";
            echo "<td>" . $usr->fullName . "</td>";
            echo "<td>" . $usr->emailAddress . "</td>";
            echo "<td>" . "+" . $usr->prefix . $usr->cellPhone . "</td>";
            echo "<td><input type='button' class='addusertofilter' value='Select' userid='".$usr->id."'></td>";
            echo "</tR>";
        }
        echo "</table>";
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
        return "<span getshop_sorting='".strtotime($row->paymentDate)."'>" . date("d.m.Y", strtotime($row->paymentDate)) . "</span>";
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
        return "<span getshop_sorting='".strtotime($row->dueDate)."'>" . date("d.m.Y", strtotime($row->dueDate)) . "</span>";
    }
    
    
    /**
     * 
     * @param \core_ordermanager_data_OrderResult $row
     * @return 
     */
    public function formatStartDate($row) {
        if(!$row->start) {
            return "N/A";
        }
        return "<span getshop_sorting='".strtotime($row->start)."'>" . date("d.m.Y", strtotime($row->start)) . "</span>";
    }
    
    
    /**
     * 
     * @param \core_ordermanager_data_OrderResult $row
     * @return 
     */
    public function formatEndDate($row) {
        if(!$row->end) {
            return "N/A";
        }
        return "<span getshop_sorting='".strtotime($row->end)."'>" . date("d.m.Y", strtotime($row->end)) . "</span>";
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
        $filter = $this->getFilter();
        $filter->start = $this->convertToJavaDate(strtotime($_POST['data']['startdate']));
        $filter->end = $this->convertToJavaDate(strtotime($_POST['data']['enddate']));
        $filter->state = $_POST['data']['state'];
        $filter->type = $_POST['data']['filtertype'];
        $filter->paymentMethod = $_POST['data']['paymentmethod'];
        $filter->searchWord = $_POST['data']['searchword'];
        
        $_SESSION['orderfilter'] = json_encode($filter);
        
    }
    
    public function formatStatus($row) {
        $states = $this->getStates();
        if(!isset($states[$row->status])) {
            return "";
        }
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

    public function printCustomersOnFilter() {
        $filter = $this->getFilter();
        foreach((array)$filter->customer as $userId) {
            $usr = $this->getApi()->getUserManager()->getUserById($userId);
            echo "<span class='addeduser' userid='".$usr->id."'><i class='fa fa-close'></i> " . $usr->fullName . "</span>";
        }
    }
    
    public function downloadExcelFile() {
        $result = $this->getFilteredResult();
        $rows = array();
        $header = array();
        $header[] = "ID";
        $header[] = "DATE";
        $header[] = "P-DATE";
        $header[] = "DUE";
        $header[] = "START";
        $header[] = "END";
        $header[] = "USER";
        $header[] = "STATUS";
        $header[] = "EX TAX";
        $header[] = "INC TAX";
        $header[] = "PAID";
        $header[] = "REST";
        $rows[] = $header;
        $states = $this->getStates();
         
        foreach($result as $statentry) {
            /* @var $statentry \core_ordermanager_data_OrderResult */
            $row = array();
            $row[] = $statentry->incOrderId;
            $row[] = date("d.m.Y", strtotime($statentry->orderDate));
            $row[] = date("d.m.Y", strtotime($statentry->paymentDate));
            $row[] = date("d.m.Y", strtotime($statentry->dueDate));
            $row[] = date("d.m.Y", strtotime($statentry->start));
            $row[] = date("d.m.Y", strtotime($statentry->end));
            $row[] = $statentry->user;
            if(isset($states[$statentry->status])) {
                $state = $states[$statentry->status];
            } else {
                $state = $statentry->status;
            }
            $row[] = $state;
            $row[] = $statentry->amountExTaxes;
            $row[] = $statentry->amountIncTaxes;
            $row[] = $statentry->amountPaid;
            $row[] = $statentry->restAmount;
            $rows[] = $row;
        }
        echo json_encode($rows);
    }

}
?>
