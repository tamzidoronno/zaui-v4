<?php
namespace ns_e8fedc44_b227_400b_8f4d_52d52e58ecfe;

class DayViewCalendarEntry {
    public $id;
    public $start;
    public $end;
    public $title;
    //Closed, reserved, confirmed
    public $type;
    
    //Room1, room2, room3, etc.
    public $roomId;
    
    /**
     * Is used for displaying name in the calendar.
     * 
     * @var String
     */
    public $shortDisplayTitle="";
    
    /**
     * When filled out the events will be clickable in the calendar
     */
    public $link = "";
}

?>