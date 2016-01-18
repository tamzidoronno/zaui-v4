<?php

namespace ns_2059b00f_8bcb_466d_89df_3de79acdf3a1;

class PmsCalendar extends \WebshopApplication implements \Application {

    private $types = false;
    private $isItemPage = false;
    private $bookingresultforday = array();

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

    public function getStartTime($day) {
        return strtotime(date("d.m.Y", $day) . " 07:00:00");
    }
    
    public function getEndTime($day) {
        return strtotime(date("d.m.Y", $day) . " 22:00:00");
    }
    
    public function printBlocks($day, $type, $room, $withSideBar) {
        $size = "";
        $start = $this->getStartTime($day);
        $hours = $this->getHoursAtDay();

        $startTime = $start;
        if($type == "day") {
            $numberOfSlots = $hours * 2;
        } else {
            $size = "small";
            $numberOfSlots = $hours;
        }
        $width = 100 / $numberOfSlots;
        for($i = 1; $i <= $numberOfSlots; $i++) {
            if($type == "day") {
                $endTime = $start + ((60*30)*$i); 
            } else {
                $endTime = $start + (60*60*$i); 
            }
            $state = $this->getBlockState($room, $day, $startTime, $endTime);
            echo "<span class='outerblock $size' style='width: $width%'>";
            echo "<span class='timeblock $state' startTime='$startTime' "
                    . "endTime='$endTime' "
                    . "day='".date("d.m.Y", $day)."'"
                    . " starttimehuman='".date("H:i", $startTime)."' endtimehuman='".date("H:i", $endTime)."'></span>";
            echo "</span>";
            $startTime = $endTime;
        }
    }

    public function setDayType() {
        $_SESSION['calendardaytype'] = $_POST['data']['type'];
    }
    
    public function getDayType() {
        $day = "day";
        if(isset($_SESSION['calendardaytype'])) {
            $day = $_SESSION['calendardaytype'];
        }
        if($day == "month" && !$this->isItemPage()) {
            $day = "day";
        }
        
        return $day;
    }
    
    function continueToForm() {
        $booking = $this->getApi()->getPmsManager()->startBooking($this->getSelectedName());
        
        $room = new \core_pmsmanager_PmsBookingRooms();
        $room->date = new \core_pmsmanager_PmsBookingDateRange();
        $room->date->start = $this->convertToJavaDate($_POST['data']['start']);
        $room->date->end = $this->convertToJavaDate($_POST['data']['end']);
        $room->bookingItemId = $_POST['data']['room'];
        
        $booking->rooms = array();
        $booking->rooms[] = $room;
        
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedName(), $booking);
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
                $width = 100 / ($this->getHoursAtDay());
                echo "<span class='outerblock headerouterblock ' style='width:$width%;'><span class='timeheader available'>" . $text . ".00</span></span>";
            }
        }
        
        if($type == "week" || $type == "month") {
            echo "<span class='weekdaycontainer weektimeheader'>";
            echo "<span class='weektimeheaderinner'>";
            echo "<div>" . $this->__w(date('l', $day)) . "</div>";
            echo "<span style='float:left; padding-left: 5px; font-size: 8px;'>".date("H.i", $this->getStartTime($day)) ."</span>";
            echo "<span style='float:right;padding-right: 5px; font-size: 8px;'>".date("H.i", $this->getEndTime($day)) ."</span>";
            echo "</span>";
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
        if($this->getDayType() == "month") {
            $time = strtotime("+1 month", $this->getSelectedDay());
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
        if($this->getDayType() == "month") {
            $time = strtotime("-1 month", $this->getSelectedDay());
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
        $diff = $this->getEndTime($this->getSelectedDay()) - $this->getStartTime($this->getSelectedDay());
        return ($diff / 3600)+1;
    }

    /**
     * @return \core_pmsmanager_PmsBooking[]
     */
    public function getBookingsForDay($day) {
        $key = date("dmY", $day);

        if(key_exists($key, $this->bookingresultforday)) {
            return $this->bookingresultforday[$key];
        }
        
        $filter = new \core_pmsmanager_PmsBookingFilter();
        $filter->startDate = $this->convertToJavaDate(strtotime(date("d.m.Y 00:00:00", $day)));
        $filter->endDate = $this->convertToJavaDate(strtotime(date("d.m.Y 23:59:59", $day)));
        $filter->filterType = "active";
        $bookings = $this->getApi()->getPmsManager()->getAllBookingsUnsecure($this->getSelectedName(), $filter);
        $this->bookingresultforday[$key] = $bookings;
        return $bookings;
    }
    
    public function getBlockState($roomId, $day, $startTime, $endTime) {
        $bookings = $this->getBookingsForDay($day);
        if(!$bookings) {
            $bookings = array();
        }
        foreach($bookings as $booking) {
            /* @var $booking \core_pmsmanager_PmsBooking */
            foreach($booking->rooms as $room) {
                $roomStart = strtotime($room->date->start);
                $roomEnd = strtotime($room->date->end);
                if($room->bookingItemId != $roomId) {
                    continue;
                }
                if($roomStart >= $endTime) {
                    continue;
                }
                if($roomEnd <= $startTime) {
                    continue;
                }
                
                if($booking->confirmed) {
                    return "occupied";
                } else {
                    return "notconfirmed";
                }
            }
        }
        return "available";
    }

    public function isItemPage() {
        if($this->isItemPage) {
            return true;
        }
        $rooms = $this->getAllRooms();
        $page = $this->getPage();
        foreach($rooms as $id => $test) {
            if($page->javapage->id == $id) {
                return $this->isItemPage = true;
            }
        }
        return false;
    }

    public function getImageFromPage($pageId) {
        $apps = $this->getApi()->getPageManager()->getApplicationsForPage($pageId);
        $imgId = "";
        foreach($apps as $app) {
            $res = $this->getFactory()->getApplicationPool()->createAppInstance($app);
            if($res instanceOf \ns_831647b5_6a63_4c46_a3a3_1b4a7c36710a\ImageDisplayer) {
                /* @var $res \ns_831647b5_6a63_4c46_a3a3_1b4a7c36710a\ImageDisplayer */
                $imgId = $res->getImageId();
            }
        }
        return $imgId;
    }

}

?>
