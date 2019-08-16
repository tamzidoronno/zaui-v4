<?php
namespace ns_3b18f464_5494_4f4a_9a49_662819803c4a;

class BookingEngineManagement extends \WebshopApplication implements \Application {
    public function getDescription() { 
       return "Configure the core of the booking engine";
    }

    public function getName() {
        return "BookingEngineManagement";
    }

    public function removeHistorical() {
        $productId = $_POST['data']['productid'];
        
        $type = $this->getApi()->getBookingEngine()->getBookingItemType($this->getSelectedName(), $_POST['data']['typeid']);
        $newArray = array();
        foreach($type->historicalProductIds as $histProdId) {
            if($histProdId == $productId) {
                continue;
            }
            $newArray[] = $histProdId;
        }
        $type->historicalProductIds = $newArray;
        $this->getApi()->getBookingEngine()->updateBookingItemType($this->getSelectedName(), $type);
        $this->loadTypeSettings();
    }
    
    public function configureTypeSorting() {
         $this->includefile("typesorting");
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
    
    public function saveTypeImage() {
        $content = strstr($_POST['data']['fileBase64'], "base64,");
        $content = str_replace("base64,", "", $content);
        $content = base64_decode($content);
        $fileId = \FileUpload::storeFile($content);
        
        $imageFile = new \core_pmsmanager_PmsTypeImages();
        $imageFile->fileId = $fileId;
        $imageFile->filename = $_POST['data']['fileName'];
        $imageFile->isDefault = false;
        
        $typeId = $_POST['data']['typeId'];
        $additional = $this->getApi()->getPmsManager()->getAdditionalTypeInformationById($this->getSelectedName(), $typeId);
        $additional->images[] = $imageFile;
        $this->getApi()->getPmsManager()->saveAdditionalTypeInformation($this->getSelectedName(), $additional);
        
        $this->printImagesForType($typeId);
    }
    
    
    public function checkForRoomsToClose() {
        $this->getApi()->getPmsManager()->checkForRoomsToClose($this->getSelectedName());
    }
    
    public function setNewSorting() {
        $i = 1;
        foreach($_POST['data']['sortlist'] as $itemid) {
            $item = $this->getApi()->getBookingEngine()->getBookingItem($this->getSelectedName(), $itemid);
            $item->order = $i;
            $this->getApi()->getBookingEngine()->saveBookingItem($this->getSelectedName(), $item);
            $i++;
       }
    }
    
    public function setNewTypeSorting() {
        $i = 1;
        foreach($_POST['data']['sortlist'] as $typeid) {
            $type = $this->getApi()->getBookingEngine()->getBookingItemType($this->getSelectedName(), $typeid);
            $type->order = $i;
            $this->getApi()->getBookingEngine()->updateBookingItemType($this->getSelectedName(), $type);
            $i++;
       }
    }
    
    public function configureItemSorting() {
        $this->includefile("itemsorting");
    }
    
    public function configureOpeningHours() {
        $this->includefile("addmoredates");
    }
    
    public function getRepeatingSummary() {
        return "";
    }
    
    public function addRepeatingDates() {
        $data = new \core_pmsmanager_TimeRepeaterData();
        $data->repeaterId = $_POST['data']['repeaterId'];
        $data->repeatMonday = $_POST['data']['repeatMonday'] == "true";
        $data->repeatTuesday = $_POST['data']['repeatTuesday'] == "true";
        $data->repeatWednesday = $_POST['data']['repeatWednesday'] == "true";
        $data->repeatThursday = $_POST['data']['repeatThursday'] == "true";
        $data->repeatFriday = $_POST['data']['repeatFriday'] == "true";
        $data->repeatSaturday = $_POST['data']['repeatSaturday'] == "true";
        $data->repeatSunday = $_POST['data']['repeatSunday'] == "true";
        $data->endingAt = $this->convertToJavaDate(strtotime($_POST['data']['endingAt']));
        $data->repeatEachTime = $_POST['data']['repeateachtime'];
        $data->timePeriodeType = $_POST['data']['timeperiodetype'];
        $data->timePeriodeTypeAttribute = $_POST['data']['timePeriodeTypeAttribute'];
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
        
        $typeid = null;
        if(isset($_POST['data']['typeid'])) {
            $typeid = $_POST['data']['typeid'];
        }

        $this->getApi()->getBookingEngine()->saveOpeningHours($this->getSelectedName(), $data, $typeid);
        
        if($typeid) {
            $this->loadTypeSettings();
        } else {
            $this->configureOpeningHours();
        }
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
        return $this->getApi()->getBookingEngine()->getOpeningHoursWithType($this->getSelectedName(), $id, null);
    }
    
    public function deleteClosingHours() {
    $this->getApi()->getBookingEngine()->deleteOpeningHours($this->getSelectedName(), $_POST['data']['id']);
    $this->includefile("addmoredates");
    
    }
    
    public function editFormFields() {
        $id = $_POST['data']['itemid'];
        $item = $this->getApi()->getBookingEngine()->getBookingItem($this->getSelectedName(), $id);
        $rules = $item->rules;
        $generator = new \FieldGenerator();
        $generator->setData($rules, $this->getFactory(), $this->getSelectedName(), $id, "saveBookingRules");
        $generator->load();
    }
    
    public function configureBookingFields() {
        $rules = $this->getApi()->getBookingEngine()->getDefaultRegistrationRules($this->getSelectedName());
        $generator = new \FieldGenerator();
        $generator->setData($rules, $this->getFactory(), $this->getSelectedName(), "", "saveDefaultBookingFields");
        $generator->load();
    }
    
    
    public function editFormFieldForType() {
        $id = $_POST['data']['typeid'];
        $type = $this->getApi()->getBookingEngine()->getBookingItemType($this->getSelectedName(), $id);
        $rules = $type->rules;
        $generator = new \FieldGenerator();
        $generator->setData($rules, $this->getFactory(), $this->getSelectedName(), $id, "saveBookingRules");
        $generator->load();
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
        
        $saved = false;
        foreach($_POST['data'] as $key => $value) {
            if(stristr($key, "bookingengine_")) {
                if($value == "true") {
                    $name = str_replace("bookingengine_", "", $key);
                    $res = $this->getApi()->getBookingEngine()->getDefaultRegistrationRules($name);
                    $generator = new \FieldGenerator();
                    $rules = $generator->createBookingRules($res);
                    foreach($rules->data as $f => $obj) {
                        $res->data->{$f}->{"title"} = $obj->title;
                    }
                    $this->getApi()->getBookingEngine()->saveDefaultRegistrationRules($name, $res);
                    $saved = true;
                }
            }
        }
        
        if(!$saved) {
            echo "saving on defaultname";
            $this->getApi()->getBookingEngine()->saveDefaultRegistrationRules($this->getSelectedName(), $rules);
        }
        
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
       
        if ($item->bookingItemTypeId != $_POST['data']['bookingitemtype']) {
            $this->getApi()->getBookingEngine()->changeBookingItemType($this->getSelectedName(), $item->id, $_POST['data']['bookingitemtype']);
        }
        
        $additional = $this->getApi()->getPmsManager()->getAdditionalInfo($this->getSelectedName(), $_POST['data']['itemid']);
        $additional->textMessageDescription = $_POST['data']['textMessageDescription'];
        $additional->squareMetres = $_POST['data']['squareMetres'];
        $additional->hideFromCleaningProgram = $_POST['data']['hideFromCleaningProgram'] == "true";
        $this->getApi()->getPmsManager()->updateAdditionalInformationOnRooms($this->getSelectedName(), $additional);
    }
    
    public function saveItemType() {
        $item = $this->getApi()->getBookingEngine()->getBookingItemType($this->getSelectedName(), $_POST['data']['typeid']);
        
        $historical = array();
        if($item->productId != $_POST['data']['productId']) {
            foreach($item->historicalProductIds as $histProdId) {
                if($histProdId == $_POST['data']['productId']) {
                    continue;
                }
                $historical[] = $histProdId;
            }
            $historical[] = $item->productId;
            
            $item->historicalProductIds = $historical;
        }
        
        if(isset($_POST['data']['addon'])) { $item->addon = $_POST['data']['addon']; }
        if(isset($_POST['data']['productId'])) { $item->productId = $_POST['data']['productId']; }
        if(isset($_POST['data']['visibleForBooking'])) { $item->visibleForBooking = $_POST['data']['visibleForBooking']; }
        if(isset($_POST['data']['autoConfirm'])){$item->autoConfirm = $_POST['data']['autoConfirm']; }
        if(isset($_POST['data']['size'])) { $item->size = $_POST['data']['size']; }
        if(isset($_POST['data']['name'])) { $item->name = $_POST['data']['name']; }
        if(isset($_POST['data']['description'])) { $item->description = $_POST['data']['description']; }
        if(isset($_POST['data']['group'])) { $item->group = $_POST['data']['group']; }
        if(isset($_POST['data']['capacity'])) { $item->capacity = $_POST['data']['capacity']; }
        if(isset($_POST['data']['minStay'])) { $item->minStay = $_POST['data']['minStay']; }
        
        $additional = $this->getApi()->getPmsManager()->getAdditionalTypeInformationById($this->getSelectedName(), $_POST['data']['typeid']);
        foreach($_POST['data'] as $key => $val) {
            if(stristr($key, "additional_")) {
                $k = str_replace("additional_", "", $key);
                $additional->{$k} = $val;
            }
        }
        $this->getApi()->getPmsManager()->saveAdditionalTypeInformation($this->getSelectedName(), $additional);
        
        $this->getApi()->getBookingEngine()->updateBookingItemType($this->getSelectedName(), $item);
        
        if($item->productId && isset($_POST['data']['name'])) {
            $product = $this->getApi()->getProductManager()->getProduct($item->productId);
            $product->name = $_POST['data']['name'];
            $this->getApi()->getProductManager()->saveProduct($product);
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
    
    public function loadItemSettings() {
        $this->includefile("itemsettings");
    }
    
    public function deleteBookingItemType() {
        $id = $_POST['data']['bookingitemtypeid'];
        $this->getApi()->getBookingEngine()->deleteBookingItemType($this->getSelectedName(), $id);
    }
    
    public function getType($id, $types) {
        foreach ($types as $type) {
            if ($type->id == $id) 
                return $type;
        }
        
        return null;
    }
    
    public function addBookingItemType() {
        $room = $_POST['data']['name'];
        $type = $this->getApi()->getBookingEngine()->createABookingItemType($this->getSelectedName(), $room);
        $product = $this->getApi()->getProductManager()->createProduct();
        $product->name = $type->name;
        $this->getApi()->getProductManager()->saveProduct($product);
        
        $type->productId = $product->id;
        $this->getApi()->getBookingEngine()->updateBookingItemType($this->getSelectedName(), $type);
    }

    public function addBookingItem() {
        $item = new \core_bookingengine_data_BookingItem();
        $item->bookingItemName = $_POST['data']['name'];
        $item->bookingItemTypeId = $_POST['data']['type'];
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
    
    public function addGeneralOpeningHour() {
        $id = $_POST['data']['typeid'];
        $this->getApi()->getBookingEngine()->saveOpeningHours($this->getSelectedName(), null, $id);
        if($id) {
            $this->loadTypeSettings();
        } else {
            $this->configureOpeningHours();
        }
    }

    public function removeImageFromType() {
        $typeId = $_POST['data']['typeId'];
        $additionals = $this->getApi()->getPmsManager()->getAdditionalTypeInformationById($this->getSelectedName(), $typeId);
        
        $newImgs = array();
        foreach($additionals->images as $img) {
            if($img->fileId != $_POST['data']['fileId']) {
                $newImgs[] = $img;
            }
        }
        $additionals->images = $newImgs;
        
        $this->getApi()->getPmsManager()->saveAdditionalTypeInformation($this->getSelectedName(), $additionals);
        $this->printImagesForType($typeId);
    }

    public function makeImageDefault() {
        $typeId = $_POST['data']['typeId'];
        $additionals = $this->getApi()->getPmsManager()->getAdditionalTypeInformationById($this->getSelectedName(), $typeId);
        
        $newImgs = array();
        foreach($additionals->images as $img) {
            $img->isDefault = false;
            if($img->fileId == $_POST['data']['fileId']) {
                $img->isDefault = true;
            }
            $newImgs[] = $img;
        }
        $additionals->images = $newImgs;
        
        $this->getApi()->getPmsManager()->saveAdditionalTypeInformation($this->getSelectedName(), $additionals);
        $this->printImagesForType($typeId);
    }
    
    public function printImagesForType($typeId) {
        $type = $this->getApi()->getPmsManager()->getAdditionalTypeInformationById($this->getSelectedName(), $typeId);
//        echo "<pre>";
//        print_r($type);
//        echo "</pre>";
        
        echo "<table>";
        foreach($type->images as $image) {
            echo "<tr>";
            echo "<td><img src='/displayImage.php?id=" . $image->fileId . "&width=100'></td>";
            echo "<td><i class='fa fa-trash-o removeImageFromType' style='cursor:pointer;' fileId='".$image->fileId."' typeId='$typeId'></i> " . $image->filename;
            if($image->isDefault) {
                echo ", this is the default image";
            } else {
                echo ", <span class='makeimgasdefault'  fileId='".$image->fileId."' typeId='$typeId' style='cursor: pointer; color: blue;'>make this image as the default</span>";
            }
            echo "</td>";
            echo "</tr>";
        }
        echo "</table>";
    }

}
?>
