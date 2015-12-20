<?php
namespace ns_2059b00f_8bcb_466d_89df_3de79acdf3a1;

class PmsCalendar extends \WebshopApplication implements \Application {
    public function getDescription() {
        return "Calendar view for displaying booked entries in a calendar.";
    }

    public function getName() {
        return "PmsCalendar";
    }

    public function showSettings() {
        $this->includefile("settings");
    }
    
    public function getSelectedName() {
        return $this->getConfigurationSetting("engine_name");
    }
    
    public function saveSettings() {
        foreach($_POST['data'] as $key => $value) {
            $this->setConfigurationSetting($key, $value);
        }
    }    
    
    public function render() {
        if(!$this->getSelectedName()) {
            echo "You need to specify a booking engine first.";
            return;
        }
        if(isset($_GET['day'])) {
            $this->includefile("showbookingonday");
        } else if(isset($_GET['roomName'])) {
            $this->includefile("calendar");
        } else {
            $this->includefile("calendarrooms");
        }
    }

    public function getSelectedDay() {
        return time();
    }
    
    public function getSelectedMonth() {
        return date("m", $this->getSelectedDay());
    }

    public function getSelectedYear() {
        return date("Y", $this->getSelectedDay());
    }
    function getDates($year) {
        $dates = array();

        for($i = 1; $i <= 366; $i++){
            $month = date('m', mktime(0,0,0,1,$i,$year));
            $wk = date('W', mktime(0,0,0,1,$i,$year));
            $wkDay = date('w', mktime(0,0,0,1,$i,$year));
            $day = date('d', mktime(0,0,0,1,$i,$year));

            if($wkDay == 0) {
                $wkDay = 7;
            }
            $dates[$month][$wk][$wkDay] = $day;
        } 
        return $dates;
    }

    public function printDayCalendar($dayToPrint, $roomName) {
        $this->includefile("daycalendar");
    }

}
?>
