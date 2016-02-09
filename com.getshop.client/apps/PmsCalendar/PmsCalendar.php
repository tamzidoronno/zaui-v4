<?php

namespace ns_2059b00f_8bcb_466d_89df_3de79acdf3a1;

class PmsCalendar extends \WebshopApplication implements \Application {

    private $types = false;
    private $isItemPage = false;
    private $bookingresultforday = array();
    private $currentTitle = "";
    private $currentBooking = "";
    private $adminInstanceId = null;
    private $bookingRules = null;
    
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
            $minutes = 30;
            $numberOfSlots = $hours * 2;
        } else {
            $size = "small";
            $minutes = 60;
            $numberOfSlots = $hours;
        }
        
        $end = $startTime + ($numberOfSlots * $minutes * 60);
        
        $lines = $this->getApi()->getPmsManager()->getAvailabilityForRoom($this->getSelectedName(), $room, $this->convertToJavaDate($start), $this->convertToJavaDate($end), $minutes);
        $width = 100 / $numberOfSlots;
        for($i = 1; $i <= $numberOfSlots; $i++) {
            $endTime = $start + (60*$minutes*$i);
            $state = $this->getBlockState($room, $day, $startTime, $endTime);
            if(!$lines[$i-1]) {
                $state = "not_available";
            }
            
            $title = date("H:i", $startTime)." - ".date("H:i", $endTime);
            $bookingId = "";
            $loadBookingOnClick = "";
            $instanceId = "";
            if($this->isEditorMode() && $this->currentTitle) {
                $loadBookingOnClick = "loadbookingonclick";
                $instanceId = $this->getAdminInstance();
                $title = $this->currentTitle;
                $bookingId = $this->currentBooking;
            }
            
            echo "<span class='$loadBookingOnClick outerblock $size' style='width: $width%' bookingid='$bookingId' instanceid='$instanceId'>";
            echo "<span class='timeblock $state' startTime='$startTime' "
                    . "endTime='$endTime' "
                    . "title='".$title."' "
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
        
        $lastItemPage = false;
        if(isset($_SESSION['lastItemPage'])) {
            $lastItemPage = $_SESSION['lastItemPage'];
        }
        
        if(isset($_SESSION['calendardaytype'])) {
            $day = $_SESSION['calendardaytype'];
        }
        if($day == "month" && !$this->isItemPage()) {
            $day = "day";
        }
        
        if($this->isItemPage() && !$lastItemPage) {
            $day = "month";
        }
        
        $_SESSION['lastItemPage'] = $this->isItemPage();
        
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
            echo "<div>" . date('d', $day) . ". " . $this->__w(date('l', $day)) . "</div>";
            echo "<span style='float:left; padding-left: 5px; font-size: 8px;'>".date("H.i", $this->getStartTime($day)) ."</span>";
            echo "<span style='float:right;padding-right: 5px; font-size: 8px;'>".date("H.i", $this->getEndTime($day)) ."</span>";
            echo "</span>";
            echo "</span>";
        }
    }

    public function setCalendarWeek() {
        $week = $_POST['data']['week'];
        $year = $this->getSelectedYear();
        $_SESSION['calday'] = $this->getStartOfWeek($week, $year);
        $_SESSION['calendardaytype'] = "week";
    }
    
    function getStartOfWeek($week, $year) {
        return strtotime($year."W".$week."1"); // First day of week
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
        if($_POST['data']['type'] == "today") {
           $_SESSION['calday'] = time();
        } else if($_POST['data']['type'] == "prev") {
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
        if($this->isEditorMode()) {
            $bookings = $this->getApi()->getPmsManager()->getAllBookings($this->getSelectedName(), $filter);
        } else {
            $bookings = $this->getApi()->getPmsManager()->getAllBookingsUnsecure($this->getSelectedName(), $filter);
        }
        $this->bookingresultforday[$key] = $bookings;
        return $bookings;
    }
    
    public function getBlockState($roomId, $day, $startTime, $endTime) {
        $bookings = $this->getBookingsForDay($day);
        if(!$bookings) {
            $bookings = array();
        }
        $rules = $this->getFormRules();

        foreach($bookings as $booking) {
            /* @var $booking \core_pmsmanager_PmsBooking */
            if($this->isEditorMode()) {
                $this->currentTitle = "<table>";
                foreach($booking->registrationData->data as $key => $val) {
                    if(!$rules->data->{$key}->visible) {
                        continue;
                    }
                    if(isset($booking->registrationData->resultAdded->{$val->name})) {
                        $this->currentTitle .= "<tr><td>" . $val->title . "</td><td>" . $booking->registrationData->resultAdded->{$val->name} . "</td></tr>";
                    }
                }
                $this->currentTitle .= "</table>";
            }
            
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
                
                $state = "";
                if($booking->confirmed) {
                    $this->currentBooking = $booking->id;
                    $state = "occupied";
                } else {
                    $this->currentBooking = $booking->id;
                    $state = "notconfirmed";
                }
                $this->currentTitle .= date("H:i", strtotime($room->date->start)) . " - ". date("H:i",strtotime($room->date->end));
                return $state;
            }
        }
        
        $this->currentBooking = "";
        $this->currentTitle = "";
        
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

    public function getAdminInstance() {
        if($this->adminInstanceId) {
            return $this->adminInstanceId;
        }
        
        $instances = $this->getApi()->getStoreApplicationInstancePool()->getApplicationInstances("7e828cd0-8b44-4125-ae4f-f61983b01e0a");
        if(!$instances) {
            $instances = array();
        }
        foreach($instances as $instance) {
            if($instance->settings->{"engine_name"}->value == $this->getSelectedName()) {
                $this->adminInstanceId = $instance->id;
            }
        }
        
        return $this->adminInstanceId;
    }

    public function getFormRules() {
        if(!$this->bookingRules) {
            $this->bookingRules = $this->getApi()->getBookingEngine()->getDefaultRegistrationRules($this->getSelectedName());
        }
        return $this->bookingRules;
    }

    public function getAllRoomsSorted() {
        $rooms = $this->getAllRooms();
        $sortList = array();
        $unsorted = array();
        foreach($rooms as $item) {
            if($item->order == 0) {
                $unsorted[] = $item;
            } else {
                $sortList[$item->order] = $item;
            }
        }

        ksort($sortList);

        foreach($unsorted as $item) {
            $sortList[] = $item;
        }
        return $sortList;
    }

}

?>
