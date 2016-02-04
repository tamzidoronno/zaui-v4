<?php
namespace ns_3b18f464_5494_4f4a_9a49_662819803c4a;

class BookingEngineManagement extends \WebshopApplication implements \Application {
    public function getDescription() { 
       return "Configure the core of the booking engine";
    }

    public function getName() {
        return "BookingEngineManagement";
    }

    public function updateName() {
        $name = $_POST['data']['name'];
        $id = $_POST['data']['entryid'];
        if($_POST['data']['type'] == "item") {
            $item = $this->getApi()->getBookingEngine()->getBookingItem($this->getSelectedName(), $id);
            $item->bookingItemName = $name;
            $this->getApi()->getBookingEngine()->saveBookingItem($this->getSelectedName(), $item);
        } else {
            $type = $this->getApi()->getBookingEngine()->getBookingItemType($this->getSelectedName(), $id);
            $type->name = $name;
            $this->getApi()->getBookingEngine()->updateBookingItemType($this->getSelectedName(), $type);
        }
        
    }
    
    public function configureOpeningHours() {
        $this->includefile("addmoredates");
    }
    
    public function getRepeatingSummary() {
        return "";
    }
    
    public function addRepeatingDates() {
        $data = new \core_pmsmanager_PmsRepeatingData();
        $data->repeattype = $_POST['data']['repeattype'];
        if(isset($_POST['data']['itemid'])) {
            $data->bookingItemId = $_POST['data']['itemid'];
        }
        if(isset($_POST['data']['typeid'])) {
            $data->bookingTypeId = $_POST['data']['typeid'];
        }
        
        $data = new \core_pmsmanager_TimeRepeaterData();
        $data->repeatMonday = $_POST['data']['repeatMonday'] == "true";
        $data->repeatTuesday = $_POST['data']['repeatTuesday'] == "true";
        $data->repeatWednesday = $_POST['data']['repeatWednesday'] == "true";
        $data->repeatThursday = $_POST['data']['repeatThursday'] == "true";
        $data->repeatFriday = $_POST['data']['repeatFriday'] == "true";
        $data->repeatSaturday = $_POST['data']['repeatSaturday'] == "true";
        $data->repeatSunday = $_POST['data']['repeatSunday'] == "true";
        $data->endingAt = $this->convertToJavaDate(strtotime($_POST['data']['endingAt']));
        $data->repeatEachTime = $_POST['data']['repeateachtime'];
        if(isset($_POST['data']['repeatmonthtype'])) {
            $data->data->repeatAtDayOfWeek = $_POST['data']['repeatmonthtype'] == "dayofweek";
        }
        $data->repeatPeride = $_POST['data']['repeat_periode'];
        
        $data->firstEvent = new \core_pmsmanager_TimeRepeaterDateRange();
        $data->firstEvent->start = $this->convertToJavaDate(strtotime($_POST['data']['eventStartsAt'] . " " . $_POST['data']['starttime']));
        if(isset($_POST['data']['eventEndsAt'])) {
            $data->firstEvent->end = $this->convertToJavaDate(strtotime($_POST['data']['eventEndsAt'] . " " . $_POST['data']['endtime']));
        } else {
            $data->firstEvent->end = $this->convertToJavaDate(strtotime($_POST['data']['eventStartsAt'] . " " . $_POST['data']['endtime']));
        }
        
        if($data->repeatPeride == "0") {
            $data->repeatEachTime = 1;
        }
        
        $itemid = null;
        if(isset($_POST['data']['itemid'])) {
            $itemid = $_POST['data']['itemid'];
        }

        $this->getApi()->getBookingEngine()->saveOpeningHours($this->getSelectedName(), $data, $itemid);
    }
    
    /**
     * 
     * @return \core_pmsmanager_TimeRepeaterData
     */
    public function getOpeningHours() {
        $id = null;
        if(isset($_POST['data']['typeid'])) {
            $id = $_POST['data']['typeid'];
        }
        return $this->getApi()->getBookingEngine()->getOpeningHours($this->getSelectedName(), $id);
    }
    
    public function deleteClosingHours() {
        $this->getApi()->getBookingEngine()->saveOpeningHours($this->getSelectedName(), null, null);
    }
    
    public function editFormFields() {
        $id = $_POST['data']['itemid'];
        $item = $this->getApi()->getBookingEngine()->getBookingItem($this->getSelectedName(), $id);
        $generator = new \FieldGenerator();
        $rules = $item->rules;
        $generator->load($rules, $this->getFactory(), $this->getSelectedName(), $id, "saveBookingRules");
    }
    
    public function configureBookingFields() {
        $generator = new \FieldGenerator();
        $rules = $this->getApi()->getBookingEngine()->getDefaultRegistrationRules($this->getSelectedName());
        $generator->load($rules, $this->getFactory(), $this->getSelectedName(), "", "saveDefaultBookingFields");
    }
    
    
    public function editFormFieldForType() {
        $id = $_POST['data']['typeid'];
        $type = $this->getApi()->getBookingEngine()->getBookingItemType($this->getSelectedName(), $id);
        $generator = new \FieldGenerator();
        $rules = $type->rules;
        $generator->load($rules, $this->getFactory(), $this->getSelectedName(), $id, "saveBookingRulesForType");
    }
    
    public function loadTypeSettings() {
        $this->includefile("typeconfigurations");
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
        
    public function saveDefaultBookingFields() {
        $generator = new \FieldGenerator();
        $rules = $generator->createBookingRules();
        $this->getApi()->getBookingEngine()->saveDefaultRegistrationRules($this->getSelectedName(), $rules);
    }

    public function saveBookingRules() {
        $generator = new \FieldGenerator();
        $rules = $generator->createBookingRules();
       
        $id = $_POST['data']['itemid'];
        $item = $this->getApi()->getBookingEngine()->getBookingItem($this->getSelectedName(), $id);
        $generator = new \FieldGenerator();
        $item->rules = $generator->createBookingRules();
        $this->getApi()->getBookingEngine()->saveBookingItem($this->getSelectedName(), $item);
    }
    
    public function saveBookingRulesForType() {
        $generator = new \FieldGenerator();
        $rules = $generator->createBookingRules();
       
        $id = $_POST['data']['itemid'];
        $type = $this->getApi()->getBookingEngine()->getBookingItemType($this->getSelectedName(), $id);
        $generator = new \FieldGenerator();
        $type->rules = $generator->createBookingRules();
        $this->getApi()->getBookingEngine()->updateBookingItemType($this->getSelectedName(), $type);
    }
    
    public function saveItem() {
        $item = $this->getApi()->getBookingEngine()->getBookingItem($this->getSelectedName(), $_POST['data']['itemid']);
        foreach($_POST['data'] as $key => $value) {
            $item->{$key} = $value;
        }
        $this->getApi()->getBookingEngine()->saveBookingItem($this->getSelectedName(), $item);
    }
    
    public function saveItemType() {
        $item = $this->getApi()->getBookingEngine()->getBookingItemType($this->getSelectedName(), $_POST['data']['typeid']);
        $item->addon = $_POST['data']['addon'];
        $item->productId = $_POST['data']['productId'];
        $item->visibleForBooking = $_POST['data']['visibleForBooking'];
        $item->size = $_POST['data']['size'];
        $item->name = $_POST['data']['name'];
        
        $this->getApi()->getBookingEngine()->updateBookingItemType($this->getSelectedName(), $item);
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
    
    public function loadItemSettings() {
        $this->includefile("itemsettings");
    }
    
    public function deleteBookingItemType() {
        $id = $_POST['data']['bookingitemtypeid'];
        $this->getApi()->getBookingEngine()->deleteBookingItemType($this->getSelectedName(), $id);
    }
    
    public function addBookingItemType() {
        $room = $_POST['data']['name'];
        $this->getApi()->getBookingEngine()->createABookingItemType($this->getSelectedName(), $room);
    }

    public function addBookingItem() {
        $item = new \core_bookingengine_data_BookingItem();
        $item->bookingItemName = $_POST['data']['name'];
        $itemTypes = $this->getApi()->getBookingEngine()->getBookingItemTypes($this->getSelectedName());

        $item->bookingItemTypeId = $itemTypes[0]->id;
        $this->getApi()->getBookingEngine()->saveBookingItem($this->getSelectedName(), $item);
    }
    
    public function updateType() {
        $id = $_POST['data']['id'];
        $type = $_POST['data']['itemTypeId'];
        $item = $this->getApi()->getBookingEngine()->getBookingItem($this->getSelectedName(), $id);
        $item->bookingItemTypeId = $type;
        $this->getApi()->getBookingEngine()->saveBookingItem($this->getSelectedName(), $item);
    }
    
    public function deletetype() {
        $id = $_POST['data']['entryid'];
        $this->getApi()->getBookingEngine()->deleteBookingItem($this->getSelectedName(), $id);
    }
    
    public function setProductToItemType() {
        $id = $_POST['data']['typeid'];
        $type = $this->getApi()->getBookingEngine()->getBookingItemType($this->getSelectedName(), $id);
        $type->productId = $_POST['data']['productid'];
        $this->getApi()->getBookingEngine()->updateBookingItemType($this->getSelectedName(), $type);
    }
    
}
?>
