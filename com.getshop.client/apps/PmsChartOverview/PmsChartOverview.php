<?php
namespace ns_f8d72daf_97d8_4be2_84dc_7bec90ad8462;

class PmsChartOverview extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsChartOverview";
    }

    public function render() {
        $this->includefile("criticalinfo");
        $this->includefile("warningnotsetupyet");
        $this->includeFile("overview");
    }
    
    public function lazyLoadOverviewData() {
        $res = array();
        
        switch($_POST['data']['view']) {
            case "Arrivals":
                $res = $this->getRoomFilter("checkin");
                break;
            case "DEPARTURES":
                $res = $this->getRoomFilter("checkout");
                break;
            case "NEW_BOOKINGS":
                $res = $this->getRoomFilter("registered");
                break;
            case "CLEANING":
                $res = $this->getCleaningStats();
                break;
            case "ECONOMY":
                $res = $this->getEconomyReport();
                break;
            case "Occupancy":
                $res = $this->getCoverage();
                break;
            case "Janitor":
                $res = $this->getJanitor();
                break;
            case "GUEST_COMMENTS":
                $res = $this->getComments();
                break;
            default:
                $res['today'] = 0;
                $res['tomorrow'] = 0;
                break;
        }
        
        echo json_encode($res);
    }
    
    public function getCleaningStats() {
        $start = $this->convertToJavaDate(time());
        $end = $this->convertToJavaDate(time()+(86400*2));
        $stats = $this->getApi()->getPmsManager()->getSimpleCleaningOverview($this->getSelectedMultilevelDomainName(), $start, $end);
        
        $res = array();
        $res['today'] = ($stats[0]->checkoutCleaningCount + $stats[0]->intervalCleaningCount);
        $res['tomorrow'] =  ($stats[1]->checkoutCleaningCount + $stats[1]->intervalCleaningCount);
        return $res;
    }
    

    public function getRoomFilter($type) {
        $filter = new \core_pmsmanager_PmsBookingFilter();
        $filter->startDate = $this->convertToJavaDate(strtotime(date("d.m.Y 00:00", time())));
        $filter->endDate = $this->convertToJavaDate(strtotime(date("d.m.Y 23:59", time())));
        $filter->filterType = $type;
        $filter->includeDeleted = false;
        $rooms = (array)$this->getApi()->getPmsManager()->getSimpleRooms($this->getSelectedMultilevelDomainName(), $filter);
        
        if($type == "registered") {
            $filter->startDate = $this->convertToJavaDate(strtotime(date("d.m.Y 00:00", time()-86400)));
            $filter->endDate = $this->convertToJavaDate(strtotime(date("d.m.Y 23:59", time()-86400)));
        } else {
            $filter->startDate = $this->convertToJavaDate(strtotime(date("d.m.Y 00:00", time()+86400)));
            $filter->endDate = $this->convertToJavaDate(strtotime(date("d.m.Y 23:59", time()+86400)));
        }
        $filter->includeDeleted = false;
        $roomsTomorrow = $this->getApi()->getPmsManager()->getSimpleRooms($this->getSelectedMultilevelDomainName(), $filter);
        
        $res = array();
        if($type == "checkin" || $type == "checkout") {
            $res['today'] = 0;
            foreach($rooms as $r) {
                $res['today']++;
            }
            $res['tomorrow'] = 0;
            foreach((array)$roomsTomorrow as $r) {
                $res['tomorrow']++;
            }
        } else {
            $res['today'] = sizeof($rooms);
            $res['tomorrow'] = sizeof($roomsTomorrow);
        }
        return $res;
    }

    public function getEconomyReport() {
        $filter = new \core_pmsmanager_PmsBookingFilter();
        $filter->startDate = $this->convertToJavaDate(strtotime(date("d.m.Y 00:00", time())));
        $filter->endDate = $this->convertToJavaDate(strtotime(date("d.m.Y 00:00", time()+86400)));
        $filter->includeVirtual = false;
        
        $stats = $this->getApi()->getPmsManager()->getStatistics($this->getSelectedMultilevelDomainName(), $filter);
        $_SESSION['savedcoveragestats'] = json_encode($stats);
        $res = array();
        if(sizeof($stats->entries) > 0) {
            $res['today'] = $stats->entries[0]->totalPrice;
            $res['tomorrow'] = $stats->entries[1]->totalPrice;
        } else {
            $res['today'] = 0;
            $res['tomorrow'] = 0;
        }
        return $res;
    }

    public function getCoverage() {
        $stats = json_decode($_SESSION['savedcoveragestats']);
        if(sizeof($stats->entries) > 0) {
            $res['today'] = $stats->entries[0]->coverage;
            $res['tomorrow'] = $stats->entries[1]->coverage;
        } else {
            $res['today'] = 0;
            $res['tomorrow'] = 0;
        }
        return $res;
        
    }

    public function getJanitor() {
        $tasks = (array)$this->getApi()->getPmsManager()->getCareTakerJobs($this->getSelectedMultilevelDomainName());
        $notCompleted = 0;
        $completed = 0;
        foreach($tasks as $task) {
            if($task->completed) {
                $completed++;
            } else {
                $notCompleted++;
            }
        }
        $res = array();
        $res['today'] = $notCompleted;
        $res['tomorrow'] = $completed;
        return $res;
    }

    public function getComments() {
        $filter = new \core_pmsmanager_PmsBookingFilter();
        $filter->startDate = $this->convertToJavaDate(strtotime(date("d.m.Y 00:00", time())));
        $filter->endDate = $this->convertToJavaDate(strtotime(date("d.m.Y 23:59", time())));
        $filter->filterType = "checkin";
        $filter->includeDeleted = false;
        $rooms = (array)$this->getApi()->getPmsManager()->getSimpleRooms($this->getSelectedMultilevelDomainName(), $filter);
        
        $today = 0;
        foreach($rooms as $room) {
            if(sizeof((array)$room->bookingComments) > 0) {
                $today++;
            }
        }

        $filter->startDate = $this->convertToJavaDate(strtotime(date("d.m.Y 00:00", time()+86400)));
        $filter->endDate = $this->convertToJavaDate(strtotime(date("d.m.Y 23:59", time()+86400)));
        $rooms = (array)$this->getApi()->getPmsManager()->getSimpleRooms($this->getSelectedMultilevelDomainName(), $filter);
        $tomorrow = 0;
        foreach($rooms as $room) {
            if(sizeof((array)$room->bookingComments) > 0) {
                $tomorrow++;
            }
        }

        
        $res = array();
        $res['today'] = $today;
        $res['tomorrow'] = $tomorrow;
        
        return $res;
        
    }

}
?>
