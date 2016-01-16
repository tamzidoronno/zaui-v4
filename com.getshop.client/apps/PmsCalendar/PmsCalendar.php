<?php

namespace ns_2059b00f_8bcb_466d_89df_3de79acdf3a1;

class PmsCalendar extends \WebshopApplication implements \Application {

    private $types = false;

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
        foreach ($_POST['data'] as $key => $value) {
            $this->setConfigurationSetting($key, $value);
        }
    }

    public function render() {
        if (!$this->getSelectedName()) {
            echo "You need to specify a booking engine first.";
            return;
        }
        $this->includefile("roomlist");
    }
    
    /**
     * @return \core_bookingengine_data_BookingItemType[]
     */
    public function getAllTypes() {
        $types = $this->getApi()->getBookingEngine()->getBookingItemTypes($this->getSelectedName());
        return $this->indexList($types);
    }

    /**
     * @return \core_bookingengine_data_BookingItem[]
     */
    public function getAllRooms() {
        $list = $this->getApi()->getBookingEngine()->getBookingItems($this->getSelectedName());
        return $this->indexList($list);
    }

    public function getStartTime() {
        return strtotime("01.01.1970 07:00:00");
    }
    
    public function getEndTime() {
        return strtotime("01.01.1970 22:00:00");
    }
    
    public function printBlocks($day, $type) {
        $size = "";
        $start = $this->getStartTime();
        $hours = $this->getHoursAtDay();

        $startTime = $start;
        if($type == "day") {
            $numberOfSlots = $hours * 2;
        } else {
            $size = "small";
            $numberOfSlots = $hours;
        }
        echo "<span class='timeslots'>";
        for($i = 1; $i <= $numberOfSlots; $i++) {
        if($type == "day") {
            $endTime = $start + ((60*30)*$i); 
        } else {
            $endTime = $start + (60*60*$i); 
        }
            echo "<span class='timeblock $size available' startTime='$startTime' endTime='$endTime' title='".date("H:i", $startTime)." - ".date("H:i", $endTime)."'></span>";
            $startTime = $endTime;
        }
        echo '</span>';
    }

    public function setDayType() {
        $_SESSION['calendardaytype'] = $_POST['data']['type'];
    }
    
    public function getDayType() {
        if(isset($_SESSION['calendardaytype'])) {
            return $_SESSION['calendardaytype'];
        }
        return "day";
    }
    
    
    function getDates($year) {
        $dates = array();

        for ($i = 1; $i <= 366; $i++) {
            $month = date('m', mktime(0, 0, 0, 1, $i, $year));
            $wk = date('W', mktime(0, 0, 0, 1, $i, $year));
            $wkDay = date('w', mktime(0, 0, 0, 1, $i, $year));
            $day = date('d', mktime(0, 0, 0, 1, $i, $year));

            if ($wkDay == 0) {
                $wkDay = 7;
            }
            $dates[$month][$wk][$wkDay] = $day;
        }
        return $dates;
    }
    
    public function getSelectedDay() {
        if(isset($_SESSION['calday'])) {
            return $_SESSION['calday'];
        }
        return time();
    }
    
    public function printHeader($day, $type) {
        if($type == "day") {
            for($i = 7; $i <= 22; $i++) {
                $text = $i;
                if($i < 10) {
                    $text = "0" . $i;
                }
                echo "<span class='timeheader'>" . $text . ".00</span>";
            }
        }
        
        if($type == "week") {
            echo "<span class='weektimeheader'>";
            echo "<div>" . date('l', $day) . "</div>";
            echo "<span style='float:left; padding-left: 5px; font-size: 8px;'>".date("H.i", $this->getStartTime()) ."</span>";
            echo "<span style='float:right;padding-right: 5px; font-size: 8px;'>".date("H.i", $this->getEndTime()) ."</span>";
            echo "</span>";
        }
    }

    public function printCalendar() {
        /* @var $this \ns_d925273e_b9fc_480f_96fa_8fb8df6edbbe\PmsBookingCalendar */
       $selectedDate = $this->getSelectedDay();
       $month = $this->getSelectedMonth();
       $year = $this->getSelectedYear();

       $number = cal_days_in_month(CAL_GREGORIAN, $month, $year);

       if(!$this->getConfigurationSetting("date_type")) {
           echo "Warning no date type set yet.. configure it";
       }
       ?>

       <div class='calendar_header'>
           <i class='fa fa-arrow-left'></i>
           <? echo $month . "/" . $year; ?>
           <i class='fa fa-arrow-right'></i>
       </div>
       <?php
       for($i = 1;$i <= $number; $i++) {
           $dayToPrint = strtotime($i.".".$month.".".$year);
           if($this->isSelectedDate($dayToPrint)) {
               echo "<span class='day selected' time='$dayToPrint'>$i</span>";
           } else {
               echo "<span class='day' time='$dayToPrint'>$i</span>";
           }
        }
    }
    public function isSelectedDate($date) {
        $current = $this->getSelectedDay();
        if(date("Y-m-d", $date) == date("Y-m-d", $current)) {
            return true;
        }
        return false;
    }
    

    public function isSelectedWeek($time) {
        $current = $this->getSelectedDay();
        if(date("Y-W", $time) == date("Y-W", $current)) {
            return true;
        }
        return false;
        
    }
    
    
    public function setNext() {
        $time = $this->getSelectedDay();
        if($this->getDayType() == "day") {
            $time += 86400;
        }
        if($this->getDayType() == "week") {
            $time += (86400 * 7);
        }
        $_SESSION['calday'] = $time;
    }
    
    public function setPrevious() {
        $time = $this->getSelectedDay();
        if($this->getDayType() == "day") {
            $time -= 86400;
        }
        if($this->getDayType() == "week") {
            $time -= (86400 * 7);
        }
        $_SESSION['calday'] = $time;
    }
    
    public function setCalendarDay() {
        $_SESSION['calday'] = strtotime($_POST['data']['day']);
    }
    
    public function changeMonth() {
        if($_POST['data']['type'] == "prev") {
           $_SESSION['calday'] = strtotime("-1 month", $this->getSelectedDay());
        } else {
           $_SESSION['calday'] = strtotime("+1 month", $this->getSelectedDay());
        }
    }
    
    public function getSelectedMonth() {
        return date("m", $this->getSelectedDay());
    }

    public function getSelectedYear() {
        return date("Y", $this->getSelectedDay());
    }

    public function getHoursAtDay() {
        $diff = $this->getEndTime() - $this->getStartTime();
        return ($diff / 3600)+1;
    }

}

?>
