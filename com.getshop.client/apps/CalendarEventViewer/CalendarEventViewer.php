<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
namespace ns_ab5378d0_9d6c_11e3_a5e2_0800200c9a66;

use WebshopApplication;

class CalendarEventViewer extends WebshopApplication implements \Application {
    private $events = array();
    
    public function getDescription() {
        return "";
    }

    public function getName() {
        return $this->__f("CalendarEventViewer");
    }

    public function getEvents() {
        return $this->events;
    }
    
    private function loadEvents() {
        $this->events = $this->getApi()->getCalendarManager()->getAllEventsConnectedToPage($this->getPage()->id);
    }
    
    public function render() {
        $this->loadEvents();
        $this->includefile("eventviewertemplate");
    }    
}
?>
