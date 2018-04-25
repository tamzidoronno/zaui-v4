<?php
namespace ns_e8fedc44_b227_400b_8f4d_52d52e58ecfe;

class DayViewCalendar extends \WebshopApplication implements \Application {

    public $start;
    public $end;
    public $events;
    public $dayEvents = array();
    public $numberOfRooms = 1;
    public $roomIndex = array();
    public $calendarId = "";
    private $columnWidth = 150;

    public function getDescription() {
    }
    
    public function setCalendarId($id) {
        $this->calendarId = $id;
    }
    
    public function getEventsForDay($day) {
        if(isset($this->dayEvents[$day])) {
            return $this->dayEvents[$day];
        }

        return array();
    }
    
    /**
     * 
     * @param \ns_e8fedc44_b227_400b_8f4d_52d52e58ecfe\DayViewCalendarEntry $event
     */
    
    function random_color_part() {
        return str_pad( dechex( mt_rand( 0, 255 ) ), 2, '0', STR_PAD_LEFT);
    }

    function random_color() {
        return $this->random_color_part() . $this->random_color_part() . $this->random_color_part();
    }

    /**
     * 
     * @param DayViewCalendarEntry $event
     * @param type $day
     */
    public function printEvent($event, $day) {
        $start = $event->start;
        $end = $event->end;
        $title = $event->title;
        
        $daytime = strtotime(date("d.m.Y 00:00", strtotime($day)));
        $dayEnd = strtotime(date("d.m.Y 23:59", strtotime($day)));
        $secondPixel = (13/(30*60));
        
        $secondsBetween = $start - $daytime;
        $top = ($secondsBetween*$secondPixel);
        
        if($top < 0) {
            $top = 0;
        }
        $top += 26;
        
        $height = 0;
        $type = "";
        if(date("dmy", $end) == date("dmy",$start)) {
            //Everything same day
            $height = ($end - $start) * $secondPixel;
            $type = "sameday";
        } else if($dayEnd < $end && $start < $daytime) {
            //Overlapping day.
            $height = 86400 * $secondPixel;
            $type = "overlapping";
        } else if($dayEnd > $end) {
            $height = ($end - $daytime) * $secondPixel;
            $height += 26;
            $type = "ending today";
        } else if($dayEnd < $end) {
            $height = ($daytime - $end) * $secondPixel;
            $type = "starting today";
        }
        
        $index = $this->getRoomIndex($event->roomId);
        $color = $this->getColorForIndex($index);
        $width = $this->getColumnWidth() / $this->numberOfRooms;
        $left = ($index * $width);
        echo "<span class='gsdayevent' style='top:".$top."px; height:".$height."px; width:".$width."px;left:".$left."px; background-color:".$color."' event='".json_encode($event)."'>";
            echo "<span class='titlearea'></span>";
            if ($event->link) {
                echo "<a href='$event->link'>";
            }
            echo "<div class='gsdayevent_inner'>";
                if ($event->shortDisplayTitle) {
                    echo "<span class='gsdayevent_shorttitle'>".$event->shortDisplayTitle."</span>";
                }
            echo "</div>";
            
            if ($event->link) {
                echo "</a>";
            }
        
        echo "</span>";
    }
    
    /**
     * 
     * @param DayViewCalendarEntry $eventsToPrint
     */
    public function setEvents($eventsToPrint) {
        $this->events = $eventsToPrint;
        $eventsIndex = array();
        
        foreach($this->events as $event) {
            $eventsIndex[$event->roomId] = 1;
        }
        
        $this->roomIndex = array();
        $count = 0;
        foreach($eventsIndex as $idx => $val) {
            $this->roomIndex[$idx] = $count;
            $count++;
        }
    }
    
    public function getColorForIndex($idx) {
        $colors = array();
        $colors[0] = "green";
        $colors[1] = "blue";
        $colors[2] = "orange";
        $colors[3] = "pink";
        $colors[4] = "yellow";
        $colors[5] = "brown";
        $colors[6] = "black";
        return $colors[$idx];
    }
    
    public function getRoomIndex($roomId) {
        return $this->roomIndex[$roomId];
    }
    
    public function getEvents() {
        return $this->events;
    }
    
    public function getStart() {
        return $this->start;
    }
    
    public function getEnd() {
        return $this->end;
    }
    
    public function setDateRange($start, $end) {
        $this->start = $start;
        $this->end = $end;
    }

    public function getName() {
        return "DayViewCalendar";
    }

    
    public function render() {
        $this->sortEvents();
        $this->includefile("daycalendar");
    }

    public function sortEvents() {
        
        foreach($this->getEvents() as $event) {
            $time = strtotime(date("d.m.Y 00:00", $event->start));
            
            while(true) {
                $day = date("d.m.Y", $time);
                if(!isset($this->dayEvents[$day])) {
                    $this->dayEvents[$day] = array();
                }
                $this->dayEvents[$day][] = $event;
                $time += 86400;
                if($time > $event->end) {
                    break;
                }
            }
        }
    }

    public function setNumberOfRooms($numberOfRooms) {
        $this->numberOfRooms = $numberOfRooms;
    }

    public function getColorForRoomId($roomId) {
        $idx = $this->getRoomIndex($roomId);
        if(!$idx && $idx != 0) {
            return "";
        }
        return $this->getColorForIndex($idx);
    }

    public function getColumnWidth() {
        return $this->columnWidth;
    }
    
    public function setColumnWidth($width) {
        $this->columnWidth = $width;
    }

}

?>
