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
        if(isset($_POST['event']) && $_POST['event'] == "reserveBooking") {
            return;
        }
        
        if (!$this->getSelectedName()) {
            echo "You need to specify a booking engine first.";
            return;
        }
        if (isset($_GET['day'])) {
            $this->includefile("showbookingonday");
        } else if (isset($_GET['roomName'])) {
            $this->includefile("calendar");
        } else {
            $this->includefile("calendarrooms");
        }
    }

    public function getSelectedDay() {
        if (isset($_GET['day'])) {
            return strtotime($_GET['day'] . " 00:00:00");
        }
        if(isset($_SESSION['calendarselectedday'])) {
            return $_SESSION['calendarselectedday'];
        }
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

    public function printDayCalendar($dayToPrint, $roomName) {
        $this->includefile("daycalendar");
    }

    public function reserveBooking() {
        $items = $this->getApi()->getBookingEngine()->getBookingItems($this->getSelectedName());

        $start = $_POST['data']['startday'] . "." . $_POST['data']['startmonth'] . "." . $_POST['data']['startyear'] . " " . $_POST['data']['starthour'] . ":" . $_POST['data']['startminute'] . ":00";
        $end = $_POST['data']['endday'] . "." . $_POST['data']['endmonth'] . "." . $_POST['data']['endyear'] . " " . $_POST['data']['endhour'] . ":" . $_POST['data']['endminute'] . ":00";
        $itemName = $_POST['data']['roomname'];
        $bookedItem = null;
        foreach ($items as $item) {
            if ($item->bookingItemName == $itemName) {
                $bookedItem = $item;
            }
        }

        $booking = $this->getApi()->getPmsManager()->startBooking($this->getSelectedName());

        $range = new \core_pmsmanager_PmsBookingDateRange();
        $range->start = $this->convertToJavaDate(strtotime($start));
        $range->end = $this->convertToJavaDate(strtotime($end));

        $room = new \core_pmsmanager_PmsBookingRooms();
        $room->bookingItemId = $bookedItem->id;
        $room->date = $range;

        $booking->rooms = array();
        $booking->rooms[] = $room;

        $this->getApi()->getPmsManager()->setBooking($this->getSelectedName(), $booking);
    }
    
    public function changeCalendarMonth() {
        $_GET['roomName'] = $_POST['data']['roomName'];
        $_GET['page'] = $_POST['data']['page'];
        $direction = $_POST['data']['direction'];
        $curtime = $this->getSelectedDay();
        if($direction == "up") {
            $curtime = strtotime("+1 month", $curtime);
        } else {
            $curtime = strtotime("-1 month", $curtime);
        }
        $_SESSION['calendarselectedday'] = $curtime;
        
    }

    /**
     * 
     * @param type $day
     * @param \core_pmsmanager_PmsBooking[] $bookingsForMonth
     */
    public function printEventsAtDay($day, $bookingsForMonth, $roomId, $printName = false) {
        $day = strtotime($day);
        $found = false;
        if($bookingsForMonth) {
            foreach ($bookingsForMonth as $booking) {
                if($this->printEventsAtDayFromRooms($day, $booking->rooms, $roomId, $printName)) {
                    $found = true;
                }
            }
        }
        return $found;
    }

    /**
     * 
     * @param type $day
     * @param \core_pmsmanager_PmsBookingRooms $rooms
     */
    public function printEventsAtDayFromRooms($day, $rooms, $roomId, $printName) {
        $found = false;
        foreach ($rooms as $room) {
            if ($this->isAddon($room)) {
                continue;
            }
            if($roomId != $room->bookingItemId) {
                continue;
            }
            
            if (($day >= strtotime($room->date->start) && $day <= strtotime($room->date->end)) || (date("m.d.Y", $day) == date("m.d.Y", strtotime($room->date->start))) || (date("m.d.Y", $day) == date("m.d.Y", strtotime($room->date->end)))
            ) {
                $found = true;
                if($printName) {
                    echo $room->guests[0]->name;
                } else {
                    echo "<span class='bookingentyrincal' style='top: 30px;' title='" . $room->guests[0]->name . "'></span>";
                }
            }
        }
        return $found;
    }

    /**
     * 
     * @param \core_pmsmanager_PmsBookingRooms $room
     */
    public function isAddon($room) {
        $types = $this->getTypes();
        return $types[$room->bookingItemTypeId]->addon > 0;
    }

    public function getTypes() {
        if ($this->types) {
            return $this->types;
        }

        $types = $this->getApi()->getBookingEngine()->getBookingItemTypes($this->getSelectedName());
        $types2 = array();
        foreach ($types as $type) {
            $types2[$type->id] = $type;
        }
        $this->types = $types2;
        return $types2;
    }

    /**
     * 
     * @param type $time
     * @param \core_pmsmanager_PmsBooking[] $bookings
     * @param type $room
     * @return boolean
     */
    public function isBookedAtSlot($time, $bookings, $roomId) {
        if(!$bookings) {
            return "";
        }
        foreach ($bookings as $booking) {
            foreach ($booking->rooms as $room) {
                if ($room->bookingItemId == $roomId) {
                    if ($time >= strtotime($room->date->start) &&
                            $time < strtotime($room->date->end)) {
                        return $room->guests[0]->name;
                        }
                    }
                }
            }
        return "";
        }

    /**
     * 
     * @return \core_bookingengine_data_BookingItem[]
     */
    public function getRooms() {
        $rooms = $this->getApi()->getBookingEngine()->getBookingItems($this->getSelectedName());
        $rooms2 = array();
        foreach ($rooms as $room) {
            if($this->isAddon($room)) {
                continue;
            }
            $rooms2[$room->id] = $room;
        }
        return $rooms2;
    }

    /**
     * 
     * @param type $name
     * @return \core_bookingengine_data_BookingItem
     */
    public function getRoomFromName($name) {
        $rooms = $this->getRooms();
        foreach ($rooms as $room) {
            if ($room->bookingItemName == $name) {
                return $room;
            }
        }
        return null;
    }

    public function printExistingBookingsList($roomId) {
        $bookings = $this->getConfirmedBookingsForDay();
        $res = "<table width='100%'>";
        $res .= "<tr>";
        if(!$roomId) {
            $res .= "<th align='left'>Room</th>";
        }
        $res .= "<th align='left'>Start</th>";
        $res .= "<th align='left'>End</th>";
        $res .= "<th></th>";
        $res .= "</tr>";
        
        $found = false;
        foreach($bookings as $booking) {
            foreach($booking->rooms as $room) {
                if($roomId && ($roomId != $room->bookingItemId)) {
                    continue;
                }
                $found = true;
                $res .= "<tr>";
                if(!$roomId) {
                    $res .= "<td>" . $this->getRoomFromId($room->bookingItemId)->bookingItemName . "</td>";
                }
                $res .= "<td>" . date("d.m.Y H:i", strtotime($room->date->start)) . "</td>";
                $res .= "<td>" . date("d.m.Y H:i", strtotime($room->date->end)) . "</td>";
                $res .= "<td>" . $room->guests[0]->name  . "</td>";
                $res .= "</tr>";
            }
        }
        $res .= "</table>";
        
        if($found) {
            echo $res;
        } else {
            echo $this->__w("No bookings registered so far");
        }
        
    }

    public function getConfirmedBookingsForDay() {
        $filter = new \core_pmsmanager_PmsBookingFilter();
        $filter->filterType = "active";
        $filter->startDate = $this->convertToJavaDate($this->getSelectedDay());
        $filter->endDate = $this->convertToJavaDate($this->getSelectedDay()+86400);
        $filter->needToBeConfirmed = true;
        
        $bookings = $this->getApi()->getPmsManager()->getAllBookingsUnsecure($this->getSelectedName(), $filter);
        if(!$bookings) {
            $bookings = array();
        }
        return $bookings;
    }

    /**
     * 
     * @param type $id
     * @return \core_bookingengine_data_BookingItem
     */
    public function getRoomFromId($id) {
        $rooms = $this->getRooms();
        foreach($rooms as $room) {
            if($room->id == $id) {
                return $room;
            }
        }
        return null;
    }

    public function getViewType() {
        $viewtype = "";
        if(isset($_GET['viewtype'])) {
            $viewtype = $_GET['viewtype'];
            $_SESSION['viewtypesetforcalendar'] = $viewtype;
        } else if(isset($_SESSION['viewtypesetforcalendar'])) {
            $viewtype = $_SESSION['viewtypesetforcalendar'];
        }
        
        return $viewtype;
    }

}

?>
