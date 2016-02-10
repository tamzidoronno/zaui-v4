<?php
namespace ns_139d9335_2353_48fe_aae5_abc4268fc14e;

class PmsDayOverview extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsDayOverview";
    }

    public function render() {
        $this->includefile("checkinoutoverview");
    }

    public function getSelectedName() {
        return "hybelhotell";
    }
    
    public function changeDate() {
        $date = $_POST['data']['date'];
        $_SESSION['pmsdayoverview_date'] = strtotime($date);
    }
    
    public function getSelectedDate() {
        if(isset($_SESSION['pmsdayoverview_date'])) {
            return $_SESSION['pmsdayoverview_date'];
        }
        return time();
    }

    public function getSelectedFilter($checkin) {
        $filter = new \core_pmsmanager_PmsBookingFilter();
        $filter->state = 0;
        $start = strtotime(date("d.m.Y 07:00", $this->getSelectedDate()));
        $filter->startDate = $this->formatTimeToJavaDate($start);
        $filter->endDate = $this->formatTimeToJavaDate($start+(86400*1));
        if($checkin) {
            $filter->filterType = "checkin";
        } else {
            $filter->filterType = "checkout";
        }
        return $filter;
    }
    
    public function updatecode() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedName());
        $config->extraField = $_POST['data']['code'];
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedName(), $config);
    }

}
?>
