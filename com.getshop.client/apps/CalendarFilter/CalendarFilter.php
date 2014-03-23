<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of CalendarFilter
 *
 * @author ktonder
 */

namespace ns_cc996344_602c_4a44_b542_b2cdb71b73bb;

class CalendarFilter extends \WebshopApplication implements \Application {
    public function getDescription() {
        return $this->__f("With this application you can easilly get an overview of your customers that has booked something into the calendar.");
    }

    public function getName() {
        return $this->__f("Calendar Filter");
    }

    public function render() {
        $this->includefile("overview");
    }    
    
    public function showSearchResult() {
        $allEvents = [];
        if (isset($_POST['data']['pages']) && count($_POST['data']['pages'])) {
            foreach ($_POST['data']['pages'] as $pageId) {
                $events = $this->getApi()->getCalendarManager()->getAllEventsConnectedToPage($pageId);
                echo "<table>";
                foreach ($events as $event) {
                    $users = $this->getApi()->getUserManager()->getUserList($event->attendees);
                    if (count($users)) {
                        foreach ($users as $user) {
                            $name = $user->fullName;
                            $eventTitle = $event->title;
                            echo "<tr><td>$name</td><td>$eventTitle</td>";
                        }
                    }
                }
                echo "</table>";
            }
        }
        
        if (isset($_POST['data']['vatNumber']) && $_POST['data']['vatNumber'] != "") {
            echo "<hr>";
            $users = $this->getApi()->getUserManager()->findUsers($_POST['data']['vatNumber']);
            
            $found = false;
            if (count($users)) {
                foreach ($users as $user) {
                    $entries = $this->getApi()->getCalendarManager()->getEntriesByUserId($user->id);
                    if (count($entries)) {
                        foreach ($entries as $entry) {
                            $found = true;
                            $id = $user->id;
                            echo "<a href='?page=users_all_users&userid=$id'>".$entry->title." - ".$user->fullName."</a><br/>";
                        }
                    }
                }
            } 
            
            if (!$found) {
                echo $this->__f("No result found");
            }
        }
    }
}

?>