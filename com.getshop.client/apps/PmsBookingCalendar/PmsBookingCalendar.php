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
    
    public function getSelectedName() {
        return $this->getConfigurationSetting("booking_engine_name");
    }
    
    public function getSelectedDate() {
        /* @var $booking \core_pmsmanager_PmsBooking */
        $booking = $this->booking;
        if(sizeof($booking->rooms) > 0) {
            if($this->isStartDate()) {
                return strtotime($booking->rooms[0]->date->start);
            } else {
                return strtotime($booking->rooms[0]->date->end);
            }
        }
        
        return time();
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
        if(sizeof($this->booking->rooms) == 0) {
            return;
        }
        
        if($this->isStartDate()) {
            $this->booking->rooms[0]->date->start = $this->convertToJavaDate($_POST['data']['time']);
        } else {
            $this->booking->rooms[0]->date->end = $this->convertToJavaDate($_POST['data']['time']);
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
        if(isset($_SESSION[$this->getAppInstanceId()]['selected_month'])) {
            return $_SESSION[$this->getAppInstanceId()]['selected_month'];
        }
        return date("m", time());
    }
    public function getSelectedYear() {
        if(isset($_SESSION[$this->getAppInstanceId()]['selected_year'])) {
            return $_SESSION[$this->getAppInstanceId()]['selected_year'];
        }
        return date("Y", time());
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
}
?>
