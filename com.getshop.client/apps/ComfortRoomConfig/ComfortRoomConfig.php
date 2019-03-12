<?php
namespace ns_aed99d87_1cc0_4b26_bddc_5583ea260392;

class ComfortRoomConfig extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ComfortRoomConfig";
    }

    public function toggleUnitToRoom() {
        $unitid = $_POST['data']['unitid'];
        $roomconfig = $this->getApi()->getComfortManager()->getComfortRoom($_POST['data']['roomid']);
        $units = (array)$roomconfig->getshopDeviceUnitsConnected;
        $state = "";
        if(in_array($unitid, $roomconfig->getshopDeviceUnitsConnected)) {
            $state = "removed";
            $newArray = array();
            foreach($roomconfig->getshopDeviceUnitsConnected as $id) {
                if($id == $unitid) {
                    continue;
                }
                $newArray[] = $id;
            }
            $units = $newArray;
        } else {
            $state = "added";
            $units[] = $unitid;
        }
        $roomconfig->getshopDeviceUnitsConnected = $units;
        $this->getApi()->getComfortManager()->saveComfortRoom($roomconfig);
        $res = array();
        $res['unitid'] = $unitid;
        $res['roomid'] = $_POST['data']['roomid'];
        $res['state'] = $state;
        echo json_encode($res);
    }
    
    public function render() {
        $this->includefile("roomlist");
    }
}
?>
