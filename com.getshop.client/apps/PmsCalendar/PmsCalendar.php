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

    /**
     * 
     * @param type $day
     * @param \core_pmsmanager_PmsBooking[] $bookingsForMonth
     */
    public function printEventsAtDay($day, $bookingsForMonth, $roomId) {
        $day = strtotime($day);
        foreach ($bookingsForMonth as $booking) {
            if (!$booking->confirmed) {
                continue;
            }
            $this->printEventsAtDayFromRooms($day, $booking->rooms, $roomId);
        }
    }

    /**
     * 
     * @param type $day
     * @param \core_pmsmanager_PmsBookingRooms $rooms
     */
    public function printEventsAtDayFromRooms($day, $rooms, $roomId) {
        foreach ($rooms as $room) {
            if ($this->isAddon($room)) {
                continue;
            }
            if($roomId != $room->bookingItemId) {
                continue;
            }
            
            if (($day >= strtotime($room->date->start) && $day <= strtotime($room->date->end)) || (date("m.d.Y", $day) == date("m.d.Y", strtotime($room->date->start))) || (date("m.d.Y", $day) == date("m.d.Y", strtotime($room->date->end)))
            ) {
                echo "<span class='bookingentyrincal' style='top: 30px;' title='" . $room->guests[0]->name . "'></span>";
            }
        }
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
            if (!$booking->confirmed) {
                continue;
            }
//            echo $roomId;
//            print_r($booking);
//            exit(0);
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
        foreach ($rooms as $room) {
            $rooms[$room->id] = $room;
        }
        return $rooms;
    }

    public function getRoomFromName($name) {
        $rooms = $this->getRooms();
        foreach ($rooms as $room) {
            if ($room->bookingItemName == $name) {
                return $room;
            }
        }
        return null;
    }

}

?>
