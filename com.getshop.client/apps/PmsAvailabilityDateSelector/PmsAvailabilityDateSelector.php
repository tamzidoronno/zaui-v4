<?php
namespace ns_a5599ed1_60be_43f4_85a6_a09d5318638f;

class PmsAvailabilityDateSelector extends \MarketingApplication implements \Application {
    var $errorMessage;
    
    public function getDescription() {
        
    }
    
    public function quickDateSelection() {
        
        $app = new \ns_28886d7d_91d6_409a_a455_9351a426bed5\PmsAvailability();
        
        $date = $_POST['data']['date'];
        if($date == "+1day") {
            $start = $app->getStartDate();
            $end = $app->getEndDate();
            $app->setStartDate(date("d.m.Y", strtotime("+1 day", $start)));
            $app->setEndDate(date("d.m.Y", strtotime("+1 day", $end)));
        } else if($date == "+1week") {
            $start = $app->getStartDate();
            $end = $app->getEndDate();
            $app->setStartDate(date("d.m.Y", strtotime("+1 week", $start)));
            $app->setEndDate(date("d.m.Y", strtotime("+1 week", $end)));
        } else {
            $dates = explode("-", $date);
            $month = $dates[0];
            $year = $dates[1];
            $start = "01.$month.$year";
            $end = date("t.m.Y", strtotime($start));
            $app->setStartDate($start);
            $app->setEndDate($end);
        }
    }
    
    public function getQuickDates() {
        $res = array();
        $res['+1day'] = "+1 day";
        $res['+1week'] = "+1 week";
        
        $time = time();
        for($i = 0; $i <= 12; $i++) {
            $res[date("m-Y", $time)] = date("M Y", $time);
            $time = strtotime("+1 month", $time);
        }
        return $res;
    }
    

    public function getName() {
        return "PmsAvailabilityDateSelector";
    }
    
    public function addevent() {
        $event = new \core_pmseventmanager_PmsEvent();
        $event->title = $_POST['data']['title'];
        $event->start = $this->convertToJavaDate(strtotime($_POST['data']['startdate']));
        $event->end = $this->convertToJavaDate(strtotime($_POST['data']['endate']));
        $event->content = $_POST['data']['content'];
        $this->getApi()->getPmsEventManager()->saveEvent($this->getSelectedMultilevelDomainName(), $event);
    }
    
    public function render() {
        if($_SERVER['PHP_SELF'] == "/json.php" || isset($_SESSION['firstloadpage'])) {
            $app = new \ns_28886d7d_91d6_409a_a455_9351a426bed5\PmsAvailability();
            $app->setStartDate(date("d.m.Y", time()-86400));
            $app->setEndDate(date("d.m.Y", time()+(86400*14)));
            $app->setPmsBookingIdsFilter();
        }
        
        $this->includefile("selector");
        if($this->errorMessage) {
            echo $this->errorMessage;
        }
    }
    
    public function closeForPeriode() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        $start = $this->convertToJavaDate(strtotime($_POST['data']['start'] . " " . $config->defaultStart));
        $end = $this->convertToJavaDate(strtotime($_POST['data']['end'] . " "  . $config->defaultEnd));
        
        if(strtotime($start) > strtotime($end)) {
            $this->errorMessage = "If you want to close for one day, please specify start as check in and end as check out.";
            return;
        }
        
        $data = new \core_pmsmanager_TimeRepeaterData();
        $data->firstEvent = new \core_pmsmanager_TimeRepeaterDateRange();
        $data->firstEvent->start = $start;
        $data->firstEvent->end = $end;
        $data->timePeriodeType = 0;
        $data->endingAt = $data->firstEvent->end;
        
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        $config->closedOfPeriode[] = $data;
        $this->getApi()->getPmsManager()->saveConfiguration($this->getSelectedMultilevelDomainName(), $config);
    }
    public function update() {
        $app = new \ns_28886d7d_91d6_409a_a455_9351a426bed5\PmsAvailability();
        $app->setStartDate($_POST['data']['from']);
        $app->setEndDate($_POST['data']['to']);
        $app->setSortType($_POST['data']['sorting']);
        if(isset($_POST['data']['categories'])) {
            $app->setCategoryFilter($_POST['data']['categories']);
        } else {
            $app->setCategoryFilter(array());
        }
    }
    
    public function getStartDate() {
        $app = new \ns_28886d7d_91d6_409a_a455_9351a426bed5\PmsAvailability();
        return date('d.m.Y', $app->getStartDate());
    }
    
    public function getEndDate() {
        $app = new \ns_28886d7d_91d6_409a_a455_9351a426bed5\PmsAvailability();
        return date('d.m.Y', $app->getEndDate());
    }
    
    public function gsAlsoUpdate() {
        $ret = array();
        $ret[] = "28886d7d-91d6-409a-a455-9351a426bed5";
        return $ret;
    }
}
?>
