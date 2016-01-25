<?php
namespace ns_27e174dc_b08c_4bf7_8179_9ea8379c91da;

class PmsEventCalendar extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }
    
    /**
     * @return \ns_831647b5_6a63_4c46_a3a3_1b4a7c36710a\ImageDisplayer
     */
    public function getImageDisplayer($id) {
        $imgInstance = $this->getConfigurationSetting("imgapp_".$id);
        if(!$imgInstance) {
            $newInstance = $this->getFactory()->getApplicationPool()->createNewInstance("831647b5-6a63-4c46-a3a3-1b4a7c36710a");
            $this->setConfigurationSetting("imgapp_".$id, $newInstance->id);
            $imgInstance = $newInstance->id;
        }
        $res = $this->getFactory()->getApplicationPool()->getApplicationInstance($imgInstance);
        return $res;
    }
    
    public function getName() {
        return "PmsEventCalendar";
    }

    public function getSelectedName() {
        return $this->getConfigurationSetting("engine_name");
    }
    
    public function showSettings() {
        $this->includefile("settings");
    }
    
    public function saveSettings() {
        foreach($_POST['data'] as $key => $value) {
            $this->setConfigurationSetting($key, $value);
        }
    }

    public function addEntry() {
        $newEntry = new \core_listmanager_data_Entry();
        $newEntry->name = $_POST['data']['id'];
        $this->getApi()->getListManager()->addEntry($this->getListName(), $newEntry, "");
    }
    
    public function listBookings() {
        $filter = new \core_pmsmanager_PmsBookingFilter();
        $filter->startDate = $this->convertToJavaDate(time()-(86400*7));
        $filter->endDate = $this->convertToJavaDate(time());
        $filter->filterType = "registered";
        
        $bookings = $this->getApi()->getPmsManager()->getAllBookings($this->getSelectedName(), $filter);
        echo "<table width='100%'>";
        echo "<tr>";
        echo "<th>Tittel</th>";
        echo "<th>Startdato</th>";
        echo "<th>Sluttdato</th>";
        echo "<th>Antal rom / dager</th>";
        echo "<th></th>";
        echo "</tr>";
        foreach($bookings as $booking) {
            echo "<tr>";
            echo "<td>" . $booking->registrationData->resultAdded->{"title"} . "</td>";
            echo "<td>" . date("d.m.Y H:i", strtotime($booking->rooms[0]->date->start)) . "</td>";
            echo "<td>" . date("d.m.Y H:i", strtotime($booking->rooms[0]->date->end)) . "</td>";
            echo "<td>" . sizeof($booking->rooms) . "</td>";
            echo "<td align='right'><span class='pmsbutton' gstype='clicksubmit' method='addEntry' gsname='id' gsvalue='".$booking->id."'>Legg til</span></td>";
            echo "</tr>";
        }
        echo "</table>";
    }
    
    public function savePostedForm() {
        $event = $this->getApi()->getPmsEventManager()->getEntry($this->getSelectedName(), $_POST['data']['eventid']);
        foreach($_POST['data'] as $key => $value) {
            $event->{$key} = $value;
        }
        $this->getApi()->getPmsEventManager()->saveEntry($this->getSelectedName(), $event);
    }
    
    public function render() {
        if(!$this->getSelectedName()) {
            echo "No booking engine set yet";
            return;
        }
        if(isset($_GET['eventid'])) {
            $this->includefile("entryconfiguration");
        } else {
            if($this->isEditorMode()) {
                $this->includefile("eventmanagement");
            }
            $this->includefile("eventlist");
        }
    }
    
    public function removeLink() {
        $index = $_POST['data']['index'];
        $event = $this->getApi()->getPmsEventManager()->getEntry($this->getSelectedName(), $_POST['data']['eventid']);
        unset($event->lenker[$index]);
        $this->getApi()->getPmsEventManager()->saveEntry($this->getSelectedName(), $event);
        $_GET['eventid'] = $_POST['data']['eventid'];
    }
    
    public function addlink() {
        $event = $this->getApi()->getPmsEventManager()->getEntry($this->getSelectedName(), $_POST['data']['eventid']);
        $link = new \core_pmseventmanager_PmsBookingEventLink();
        $link->name = $_POST['data']['name'];
        $link->link = $_POST['data']['link'];
        $event->lenker[] = $link;
        $this->getApi()->getPmsEventManager()->saveEntry($this->getSelectedName(), $event);
        $_GET['eventid'] = $_POST['data']['eventid'];
    }

    public function getEventList() {
        $list = $this->getApi()->getListManager()->getList($this->getListName());
        if(!$list) {
            return array();
        }
        return $list;
    }

    public function getListName() {
        return $this->getSelectedName() . "_eventcalendar";
    }

}
?>
