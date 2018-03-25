<?php
namespace ns_916aff23_c765_4b8c_9d8f_8783f1b7bd16;

class SrsTables extends \MarketingApplication implements \Application {
    private $rooms = array();
    
    public function getDescription() {
        
    }

    public function getName() {
        return "SrsTables";
    }

    public function render() {
        $this->includefile("tables");
    }

    
    /**
     * 
     * @return \core_resturantmanager_ResturantRoom
     */
    public function getSelectedRoom() {
        $rooms = $this->getRooms();
        
        if (!isset($_SESSION['SrSTable_selected_room'])) {
            return null;
        }
        
        foreach ($rooms as $room) {
            if ($room->id == $_SESSION['SrSTable_selected_room']) {
                return $room;
            }
        }
    }

    public function getRooms() {
        if (!$this->rooms) {
            $this->rooms = $this->getApi()->getResturantManager()->getRooms();
        }
        return $this->rooms;
    }

    /**
     * 
     * @return \core_resturantmanager_ResturantRoom[]
     */
    public function getTables() {
        return array();
    }
    
    public function selectRoom() {
        $_SESSION['SrSTable_selected_room'] = $_POST['data']['roomid'];
    }

}
?>
