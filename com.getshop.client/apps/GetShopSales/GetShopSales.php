<?php
namespace ns_514d1d25_ea8b_4872_b010_e282c3d3db3e;

class GetShopSales extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "GetShopSales";
    }

    public function render() {
        $this->includefile("leadslist");
    }
    
    public function createNewLead() {
        $name = $_POST['data']['name'];
        $email = $_POST['data']['email'];
        $prefix = $_POST['data']['prefix'];
        $phone = $_POST['data']['phone'];
        
        $this->getApi()->getSupportManager()->createLead($name, $email, $prefix, $phone);
    }
    
    public function updateFilter() {
        $_SESSION['salesfilter'] = $_POST['data'];
    }
    
    public function getFilter() {
        $filter = new \stdClass();
        $filter->mineonly = false;
        $filter->needFollowUp = false;
        $filter->state = "";
        if(isset($_SESSION['salesfilter'])) {
            $filter->mineonly = $_SESSION['salesfilter']['mineonly'] == "true";
            $filter->needFollowUp = $_SESSION['salesfilter']['needFollowUp'] == "true";
            $filter->state = $_SESSION['salesfilter']['state'];
        }
        
        return $filter;
    }
    
    public function saveLead() {
        $lead = $this->getApi()->getSupportManager()->getLead($_POST['data']['id']);
        $lead->name = $_POST['data']['name'];
        $lead->email = $_POST['data']['email'];
        $lead->prefix = $_POST['data']['prefix'];
        $lead->phone = $_POST['data']['phone'];
        $lead->rooms = $_POST['data']['rooms'];
        $lead->entrances = $_POST['data']['entrances'];
        $lead->value = $_POST['data']['value'];
        $lead->state = $_POST['data']['state'];
        if($_POST['data']['followupdate']) {
            $lead->followUpDate = $this->convertToJavaDate(strtotime($_POST['data']['followupdate']));
        }
        $lead->comment = $_POST['data']['comment'];
        $this->getApi()->getSupportManager()->saveLead($lead);
    }
}
?>
