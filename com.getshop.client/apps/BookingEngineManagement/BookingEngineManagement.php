<?php
namespace ns_3b18f464_5494_4f4a_9a49_662819803c4a;

class BookingEngineManagement extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "BookingEngineManagement";
    }

    public function updateName() {
        $name = $_POST['data']['name'];
        $id = $_POST['data']['bookingItemTypeId'];
        $type = $this->getApi()->getBookingEngine()->getABookingItemType($this->getSelectedName(), $id);
        $this->getApi()->getBookingEngine()->saveABookingItemType($this->getSelectedName(), $type);
        
    }
    
    public function render() {
        if($this->isEditorMode()) {
            if(!$this->getSelectedName()) {
                echo "No booking engine name set yet. please set it first";
                return;
            }
            $this->includefile("managementview");
        }
    }
    
    public function getSelectedName() {
        return $this->getConfigurationSetting("pmsname");
    }
    
    public function showSettings() {
        $this->includefile("settings");
    }
    
    public function saveSettings() {
        foreach($_POST['data'] as $key => $value) {
            $this->setConfigurationSetting($key, $value);
        }
    }
    
    public function deleteBookingItemType() {
        $id = $_POST['data']['bookingitemtypeid'];
        $this->getApi()->getBookingEngine()->deleteABookingItemType($this->getSelectedName(), $id);
    }
    
    public function addBookingItemType() {
        $room = $_POST['data']['name'];
        $this->getApi()->getBookingEngine()->createABookingItemType($this->getSelectedName(), $room);
    }

    public function addBookingItem() {
        $item = new \core_bookingengine_data_BookingItem();
        $item->bookingItemName = $_POST['data']['name'];
        $item->bookingItemTypeId = "273bb2f0-da4a-483d-8684-bae95164b10d";
        $this->getApi()->getBookingEngine()->saveBookingItem($this->getSelectedName(), $item);
    }
    
    public function deletetype() {
        $id = $_POST['data']['id'];
        $this->getApi()->getBookingEngine()->deleteBookingItem($this->getSelectedName(), $id);
    }
    
}
?>
