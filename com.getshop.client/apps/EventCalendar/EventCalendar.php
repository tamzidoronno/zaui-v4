<?php
namespace ns_6997a8f0_1724_403b_ab4e_493592ba7665;

class EventCalendar extends \MarketingApplication implements \Application {
       /** @var core_calendarmanager_data_Entry */
    public $currentEntry;
    public $monthObjeBct;
    public $year;
    public $month; 
    public $day;
    public $downloadingListAsPdf = false;
    private $activeFilters;
    
    public function getDescription() {
        return $this->__("Keep your customers up to date of what events you are hosting with this application");
    }
    
    public function getName() {
        return $this->__("Event calendar");
    }
    
    public function postProcess() {
        
    }
    
    private function initializeDate() {
        if (!isset($_SESSION['calendar_year'])) {
            $_SESSION['calendar_year'] = $this->year = date('Y');
        }
            
        if (!isset($_SESSION['calendar_month'])) {
            $_SESSION['calendar_month'] = date('m');
        }
            
        if (!isset($_SESSION['calendar_day'])) {
            $_SESSION['calendar_day'] = date('d');
        }

        if (isset($_GET['year']))
            $_SESSION['calendar_year'] = $_GET['year'];
        
        if (isset($_GET['month']))
            $_SESSION['calendar_month'] = $_GET['month'];
        
        if (isset($_GET['day']))
            $_SESSION['calendar_day'] = $_GET['day'];
        
        $this->year = $_SESSION['calendar_year'];
        $this->month = $_SESSION['calendar_month'];
        $this->day = $_SESSION['calendar_day'];

        $this->year = (isset($_POST['data']['year'])) ? $_POST['data']['year']  : $this->year;
        $this->month = (isset($_POST['data']['month'])) ? $_POST['data']['month']  : $this->month;
        $this->day = (isset($_POST['data']['day'])) ? $_POST['data']['day']  : $this->day;
        
        $this->year = (int)$this->year;
        $this->month = (int)$this->month;
        $this->day = (int)$this->day;

    }
    
    public function preProcess() {
        $this->initializeDate();
    }
    
    public function render() {
        $this->includefile('calendar');
    }
    

    public function getNextLink() {
        $page = $this->getPage()->getId();
        $month = $this->month + 1;
        $year = $this->year;
        if ($month > 12) {
            $month = 1;
            $year = $this->year + 1;
        }
        return "?page=$page&year=$year&month=$month";
    }
    
    public function getPrevLink() {
        $page = $this->getPage()->getId();
        $month = $this->month - 1;
        $year = $this->year;
        if ($month < 1) {
            $month = 12;
            $year = $this->year - 1;
        }
        return "?page=$page&year=$year&month=$month";
    }
    
    public function getEventsForDay($year, $month, $day) {
        $events = $this->getApi()->getEventBookingManager()->getEventsForDay("booking", $year, $month, $day);
        if (!$events)
            return [];
        
        return $events;
    }
    
    
}

class checkListComment {
    public $commentId;
    public $userId;
    public $date;
    public $omment;
}
?>
