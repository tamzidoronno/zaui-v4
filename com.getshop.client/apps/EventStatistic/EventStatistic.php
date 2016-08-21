<?php
namespace ns_a7cb1957_bbc9_42d3_8f79_fa91260c3ebf;

class EventStatistic extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "EventStatistic";
    }

    public function render() {
        $this->includefile("statistic");
    }

    public function getInterval() {
        if (isset($_SESSION['EventStatistic_interval']))
            return $_SESSION['EventStatistic_interval'];
        
        
        return "month";
    }
    
    public function setInterval() {
        $_SESSION['EventStatistic_interval'] = $_POST['data']['value'];
    }

    public function doSearch() {
        
    }
    
    public function getStats() {
        if (isset($_POST['data']['starttime']) && isset($_POST['data']['stoptime'])) {
            $start = $this->convertToJavaDate(strtotime($_POST['data']['starttime']));
            $stop = $this->convertToJavaDate(strtotime($_POST['data']['stoptime']));
            return $this->getApi()->getEventBookingManager()->getStatistic("booking", $start, $stop);
        }
        
        return null;
    }
}
?>
