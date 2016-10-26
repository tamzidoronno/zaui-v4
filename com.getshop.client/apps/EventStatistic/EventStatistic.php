<?php
namespace ns_a7cb1957_bbc9_42d3_8f79_fa91260c3ebf;

class EventStatistic extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "EventStatistic";
    }

    public function render() {
        if (isset($_POST["data"]["submit"]) && $_POST["data"]["submit"] == "locations") {
            $this->includefile("locations");
        } else {
            $this->includefile("statistic");
        }
        
    }
    
    public function downloadStatistic() {
        $_POST = $_SESSION['searchcriterias'];
        $stats = $this->getStats();
        $data = [];
        
        foreach ($stats as $stat) {
            $yrdata= strtotime("$stat->year-$stat->month-1");
            $monthName = date('M-Y', $yrdata);
            $data[] = [$monthName,$stat->count];
        }
        
        echo json_encode($data);
        die();
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
        $_SESSION['searchcriterias'] = $_POST;
    }
    
    public function getStats($byLocation=false) {
        $groups = [];
        $allGroups = $this->getApi()->getUserManager()->getAllGroups();
        
        foreach ($allGroups as $group) {
            if (isset($_POST['data']['groupid_'.$group->id]) && $_POST['data']['groupid_'.$group->id] == "true") {
                $groups[] = $group->id;
            }
        }
        
        $events = [];
        $allEvents = $this->getApi()->getBookingEngine()->getBookingItemTypes("booking");
        
        foreach ($allEvents as $eventType) {
            if (isset($_POST['data']['eventid_'.$eventType->id]) && $_POST['data']['eventid_'.$eventType->id] == "true") {
                $events[] = $eventType->id;
            }
        }
        
        if (isset($_POST['data']['starttime']) && isset($_POST['data']['stoptime'])) {
            $start = $this->convertToJavaDate(strtotime($_POST['data']['starttime']));
            $stop = $this->convertToJavaDate(strtotime($_POST['data']['stoptime']));
            
            if ($byLocation) {
                return $this->getApi()->getEventBookingManager()->getStatisticGroupedByLocations("booking", $start, $stop, $groups, $events);
            }
            
            return $this->getApi()->getEventBookingManager()->getStatistic("booking", $start, $stop, $groups, $events);
        }
        
        return null;
    }

    public function getGroupedByLocations() {
        $stats = $this->getStats(true);
        $loc = [];
        
        foreach ($stats as $stat) {

            $locationId = $stat->locationId;
            
            if (!$stat->count)
                continue;
            
            if (isset($loc[$locationId])) {
                $loc[$locationId] = $loc[$locationId] + $stat->count;
            } else {
                $loc[$locationId] = $stat->count;
            }
        }
        
        return $loc;
    }

}
?>
