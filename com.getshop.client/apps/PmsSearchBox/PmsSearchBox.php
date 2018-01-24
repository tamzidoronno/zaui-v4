<?php
namespace ns_1ba01a11_1b79_4d80_8fdd_c7c2e286f94c;

class PmsSearchBox extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsSearchBox";
    }

    public function render() {
        $this->includefile("searchbox");
    }
    
    public function search() {
        $pms = new \ns_961efe75_e13b_4c9a_a0ce_8d3906b4bd73\PmsSearchBooking();
        $pms->searchBooking();
    }
    
    public function clearFilter() {
        $pms = new \ns_961efe75_e13b_4c9a_a0ce_8d3906b4bd73\PmsSearchBooking();
        $pms->clearFilter();
    }
    
    public function showCheckins() {
        $pms = new \ns_961efe75_e13b_4c9a_a0ce_8d3906b4bd73\PmsSearchBooking();
        $pms->showCheckins();
    }
    
    public function showCheckOuts() {
        $pms = new \ns_961efe75_e13b_4c9a_a0ce_8d3906b4bd73\PmsSearchBooking();
        $pms->showCheckouts();
    }

    public function getEndDate() {
        $app = new \ns_28886d7d_91d6_409a_a455_9351a426bed5\PmsAvailability();
        return date('d.m.Y', $app->getEndDate());
    }
    
    public function getStartDate() {
        $app = new \ns_28886d7d_91d6_409a_a455_9351a426bed5\PmsAvailability();
        return date('d.m.Y', $app->getStartDate());
    }
    
    public function getSearchTypes() {
        $searchtypes = array();
        $searchtypes['registered'] = "Registered";
        $searchtypes['active'] = "Active";
        $searchtypes['uncofirmed'] = "Unconfirmed";
        $searchtypes['checkin'] = "Checking in";
        $searchtypes['checkout'] = "Checking out";
        $searchtypes['waiting'] = "Waiting";
        $searchtypes['activecheckin'] = "Checkin + stayover";
        $searchtypes['activecheckout'] = "Checkout + stayover";
        $searchtypes['inhouse'] = "Inhouse";
        $searchtypes['stats'] = "Coverage";
        $searchtypes['summary'] = "Summary";
        $searchtypes['orderstats'] = "Income report";
        $searchtypes['afterstayorder'] = "Order created after stay";
        $searchtypes['unsettled'] = "Bookings with unsettled amounts";
        return $searchtypes;
    }

}
?>
