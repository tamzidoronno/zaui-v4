<?php
namespace ns_d925273e_b9fc_480f_96fa_8fb8df6edbbe;

class PmsBookingCalendar extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsBookingCalendar";
    }

    public function getSelectedDate() {
        if(isset($_SESSION[$this->getAppInstanceId()]['selected_day'])) {
            return $_SESSION[$this->getAppInstanceId()]['selected_day'];
        }        
        return time();
    }
    
    public function selectDay() {
        $_SESSION[$this->getAppInstanceId()]['selected_day'] = $_POST['data']['time'];
        $this->includefile("calendar");
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
    public function render() {
        $this->includefile("calendar");
    }
}
?>
