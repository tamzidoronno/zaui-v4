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
    
    function getDates($year) {
        $dates = array();

        for ($i = 1; $i <= 366; $i++) {
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
        $this->booking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedName());
        
        if($this->isStartDate()) {
            $this->booking->sessionStartDate = $this->convertToJavaDate($_POST['data']['time']);
        } else {
            $this->booking->sessionEndDate = $this->convertToJavaDate($_POST['data']['time']);
        }
        
        if(sizeof($this->booking->rooms) > 0) {
            foreach($this->booking->rooms as $room) {
                if($this->isStartDate()) {
                    $room->date->start = $this->convertToJavaDate($_POST['data']['time']);
                } else {
                    $room->date->end = $this->convertToJavaDate($_POST['data']['time']);
                }
            }
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
    
    public function  isStartDate() {
        return $this->getConfigurationSetting("date_type") == "start_date";
    }
    
    public function render() {
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

}
?>
