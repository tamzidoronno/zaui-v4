<?php
namespace ns_a5599ed1_60be_43f4_85a6_a09d5318638f;

class PmsAvailabilityDateSelector extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsAvailabilityDateSelector";
    }

    public function render() {
        if($_SERVER['PHP_SELF'] == "/json.php") {
            $app = new \ns_28886d7d_91d6_409a_a455_9351a426bed5\PmsAvailability();
            $app->setStartDate(date("d.m.Y", time()-86400));
            $app->setEndDate(date("d.m.Y", time()+(86400*7)));
        }
        
        $this->includefile("selector");
    }
    
    public function closeForPeriode() {
        $start = $this->convertToJavaDate(strtotime($_POST['data']['start'] . " 00:00"));
        $end = $this->convertToJavaDate(strtotime($_POST['data']['end'] . " 23:59"));
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
    }
    
    public function getStartDate() {
        $app = new \ns_28886d7d_91d6_409a_a455_9351a426bed5\PmsAvailability();
        return date('d.m.Y', $app->getStartDate());
    }
    
    public function getEndDate() {
        $app = new \ns_28886d7d_91d6_409a_a455_9351a426bed5\PmsAvailability();
        return date('d.m.Y', $app->getEndDate());
    }
}
?>
