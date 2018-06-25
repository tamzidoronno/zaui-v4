<?php
namespace ns_d925273e_b9fc_480f_96fa_8fb8df6edbbe;

class PmsBookingCalendar extends \WebshopApplication implements \Application {
    private $booking;
    
    public function getDescription() {
        return "A calendar for selecting start and dates while booking";
    }

    public function getName() {
        return "PmsBookingCalendar";
    }

    public function showSettings() {
        $this->includefile("settings");
    }
    
    
    public function initBooking() {
        $booking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedName());
        /* @var $booking \core_pmsmanager_PmsBooking */
        $range = new \core_pmsmanager_PmsBookingDateRange();
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedName());
        $start = strtotime(date("d.m.Y", strtotime($_GET['start'])) . " " . $config->defaultStart);
        $range->start = $this->convertToJavaDate($start);
        if(isset($_GET['end'])) {
            $end = strtotime(date("d.m.Y", strtotime($_GET['end'])) . " " . $config->defaultEnd);
            $range->end = $this->convertToJavaDate($end);
        }
        
        $room = new \core_pmsmanager_PmsBookingRooms();
        if(isset($_GET['type'])) {
            $room->bookingItemTypeId = $_GET['type'];
        }
        $room->date = $range;
        
        $booking->sessionStartDate = $range->start;
        $booking->sessionEndDate = $range->end;
        $booking->rooms = array();
        if($room->bookingItemTypeId) {
            $count = $this->getApi()->getBookingEngine()->getAvailbleItems($this->getSelectedName(), 
                    $room->bookingItemTypeId, 
                    $this->convertToJavaDate(strtotime($range->start)), 
                    $this->convertToJavaDate(strtotime($range->end)));
            if($count > 0) {
                $booking->rooms[] = $room;
            }
        }
        
        if(isset($_GET['coupon'])) {
            $booking->couponCode = $_GET['coupon'];
        }
        
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedName(), $booking);
    }
    
    function getDates($year) {
        $dates = array();
        
        $leap = date('L', mktime(0, 0, 0, 1, 1, $year));
        $counter = 365;
        if($leap) {
            $counter = 366;
        }

        for ($i = 1; $i <= $counter; $i++) {
            $month = date('m', mktime(0, 0, 0, 1, $i, $year));
            $wk = date('W', mktime(0, 0, 0, 1, $i, $year));
            $wkDay = date('N', mktime(0, 0, 0, 1, $i, $year));
            $day = date('d', mktime(0, 0, 0, 1, $i, $year));

            $dates[$month][$wk][$wkDay] = $day;
        }
        return $dates;
    }
    
    public function getSelectedName() {
        return $this->getConfigurationSetting("booking_engine_name");
    }
    
    public function getSelectedDate() {
        /* @var $booking \core_pmsmanager_PmsBooking */
        $booking = $this->getBooking();
        if($this->isStartDate()) {
            return strtotime($booking->sessionStartDate);
        } else {
            return strtotime($booking->sessionEndDate);
        }
    }
    
    public function getText($key) {
        return $this->getConfigurationSetting($key);
    }
    
    public function saveSettings() {
        foreach($_POST['data'] as $key => $value) {
            $this->setConfigurationSetting($key, $value);
        }
    }
    
    
    public function selectDay() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedName());
        $this->booking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedName());
        if($this->isStartDate()) {
            $this->booking->sessionStartDate = $this->convertToJavaDate($_POST['data']['time']);
            if($config->bookingTimeInterval == 2) {
                //Daily time interval
                if(strtotime($this->booking->sessionStartDate) >= strtotime($this->booking->sessionEndDate)) {
                    $this->booking->sessionEndDate = $this->convertToJavaDate(strtotime($this->booking->sessionStartDate)+86400);
                }
                if(date("d.m.Y", strtotime($this->booking->sessionStartDate)) == date("d.m.Y", strtotime($this->booking->sessionEndDate))) {
                    $this->booking->sessionEndDate = $this->convertToJavaDate(strtotime($this->booking->sessionEndDate)+86400);
                }
            }
        } else {
            $this->booking->sessionEndDate = $this->convertToJavaDate($_POST['data']['time']);
            if($config->bookingTimeInterval == 2) {
                if(strtotime($this->booking->sessionStartDate) >= strtotime($this->booking->sessionEndDate)) {
                    $this->booking->sessionStartDate = $this->convertToJavaDate(strtotime($this->booking->sessionEndDate)-86400);
                }
                if(date("d.m.Y", strtotime($this->booking->sessionStartDate)) == date("d.m.Y", strtotime($this->booking->sessionEndDate))) {
                    $this->booking->sessionStartDate = $this->convertToJavaDate(strtotime($this->booking->sessionStartDate)-86400);
                }
            }
        }
        
        $this->booking->sessionStartDate = $this->convertToJavaDate(strtotime(date("d.m.Y ", strtotime($this->booking->sessionStartDate)) . $config->defaultStart));
        $this->booking->sessionEndDate = $this->convertToJavaDate(strtotime(date("d.m.Y ", strtotime($this->booking->sessionEndDate)) . $config->defaultEnd));
        
        if($config->hasNoEndDate) {
            $this->booking->sessionEndDate = null;
        }
        
        
        if(sizeof($this->booking->rooms) > 0) {
            $this->booking->rooms = array();
        }
        
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedName(), $this->booking);
    }
    
    public function isSelectedDate($date) {
        $current = $this->getSelectedDate();
        if(date("Y-m-d", $date) == date("Y-m-d", $current)) {
            return true;
        }
        return false;
    }
    
    public function getSelectedMonth() {
        $month = "";
        if(isset($_SESSION[$this->getAppInstanceId()]['selected_month'])) {
            $month = $_SESSION[$this->getAppInstanceId()]['selected_month'];
        } else {
            $month = date("m", $this->getSelectedDate());
        }
        
        if($month < 10) {
            $month = "0" . (int)$month;
        }

        return $month;
    }
    public function getSelectedYear() {
        if(isset($_SESSION[$this->getAppInstanceId()]['selected_year'])) {
            return $_SESSION[$this->getAppInstanceId()]['selected_year'];
        }
        return date("Y",$this->getSelectedDate());
    }
    public function changeMonth() {
        $month = $_POST['data']['month'];
        $curMonth = $this->getSelectedMonth();
        $curYear = $this->getSelectedYear();
        if($month == "-1") {
            $curMonth -= 1;
        }
        if($month == "+1") {
            $curMonth += 1;
        }
        
        if($curMonth > 12) {
            $curYear += 1;
            $curMonth = 1;
        }
        if($curMonth < 1) {
            $curYear -= 1;
            $curMonth = 12;
        }

        $_SESSION[$this->getAppInstanceId()]['selected_month'] = $curMonth;
        $_SESSION[$this->getAppInstanceId()]['selected_year'] = $curYear;
        
        $this->includefile("calendar");
    }
    
    public function changeMonthSpecified() {
        $month = explode("-", $_POST['data']['month']);
        $_SESSION[$this->getAppInstanceId()]['selected_month'] = $month[0];
        $_SESSION[$this->getAppInstanceId()]['selected_year'] = $month[1];
        $this->includefile("calendar");
    }
    
    public function  isStartDate() {
        return $this->getConfigurationSetting("date_type") == "start_date";
    }
    
    public function render() {
        if($this->needToBeCleared()) {
            $this->getApi()->getPmsManager()->startBooking($this->getSelectedName());
        }
        if(isset($_GET['start'])) {
            $this->initBooking();
        }
        if(!$this->getSelectedName()) {
            echo "Please specify a booking engine first";
            return;
        }

        $this->booking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedName());
        echo "<div class='calendar'>";
        $this->includefile("calendar");
        echo "</div>";
    }

    public function getBooking() {
        if(!$this->booking) {
            $this->booking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedName());
        }
        return $this->booking;
    }

    public function needToBeCleared() {
        $booking = $this->getBooking();
        $bookingTime = strtotime($booking->rowCreatedDate);
        $diff = time() - $bookingTime;

        if($diff > (60*60)) {
            return true;
        }
        $res = (array)$booking->registrationData->resultAdded;
        if(sizeof($res) > 0) {
            return true;
        }
        if(@$booking->userId) {
            return true;
        }
        return false;
    }

}
?>
