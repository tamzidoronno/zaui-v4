<?php
namespace ns_a22747ef_10b1_4f63_bef8_41c02193edd8;

class PmsRoomConfiguration extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }
    
    public function getAccessories() {
        if(isset($this->acc)) {
            return (array)$this->acc;
        }
        $this->acc = $this->getApi()->getPmsManager()->getAccesories($this->getSelectedMultilevelDomainName());
        return (array)$this->acc;
    }
    
    public function deletetype() {
        $typeid = $_POST['data']['value'];
        $this->getApi()->getBookingEngine()->deleteBookingItemType($this->getSelectedMultilevelDomainName(), $typeid);
    }

    public function deleteroom() {
        $roomid = $_POST['data']['gsroomid'];
        $this->getApi()->getBookingEngine()->deleteBookingItem($this->getSelectedMultilevelDomainName(), $roomid);
    }
    
    public function getName() {
        return "PmsRoomConfiguration";
    }

    public function getTypeSystemCategories() {
        $systemCategories = array();
        $systemCategories[0] = "ROOM";
        $systemCategories[1] = "CONFERENCE";
        $systemCategories[2] = "RESTAURANT";
        $systemCategories[3] = "CAMPING";
        $systemCategories[4] = "CABIN";
        $systemCategories[5] = "HOSTEL BED";
        $systemCategories[6] = "APARTMENT";
        return $systemCategories;
    }
    
    public function render() {
        $types = $this->getApi()->getBookingEngine()->getBookingItemTypesWithSystemType($this->getSelectedMultilevelDomainName(), null);
        $items = $this->getApi()->getBookingEngine()->getBookingItems($this->getSelectedMultilevelDomainName());
        if(sizeof($types) == 0) {
            echo "<br><br>";
            $this->includefile("firsttypecreation");
            return;
        }
        
        if(sizeof($items) == 0) {
            $this->includefile("itemsnotaddedyet");
        }

        $this->includefile("roomtypeconfig");
        $this->includefile("roomconfig");
    }
    
    public function formatDescription($row) {
        $accessories = $this->getAccessories();
        
        $type = $this->getApi()->getBookingEngine()->getBookingItemType($this->getSelectedMultilevelDomainName(), $row->typeId);
        $additional = $this->getApi()->getPmsManager()->getAdditionalTypeInformationById($this->getSelectedMultilevelDomainName(), $type->id);
        
        if($type->visibleForBooking) {
            $res = "<i class='fa fa-eye' style='color:green; float:right;' title='This room is visible for booking'></i>";
        } else {
            $res = "<i class='fa fa-eye' style='color:gray; float:right;' title='This room is not visible for booking'></i>";
        }
        
        $typeConfig = $this->getTypeSystemCategories();
        $res .= "<span style='float:right; margin-right: 5px; color:#bbb;'>" . $typeConfig[$type->systemCategory] . "</span>";
        
        $res .= " <b>" . $row->name . "</b><br>" . $row->description . "<bR>";
        
        $res .= "<b>Accessorier:</b> ";
        foreach($accessories as $acc) {
            if(in_array($acc->id, $additional->accessories)) {
                $icon = "briefcase";
                if($acc->icon) {
                    $icon = $acc->icon;
                }
                $res .= "<i class='fa fa-$icon' title='".$acc->title."'></i> ";
            }
        }
        
        if ($type->departmentId) {
            $res .= "<br/><b>Department:</b> ".$this->getApi()->getDepartmentManager()->getDepartment($type->departmentId)->name;
        }
        
        return $res;
    }
    
    public function saveRoomSettings() {
        $itemId = $_POST['data']['id'];
        $item = $this->getApi()->getBookingEngine()->getBookingItem($this->getSelectedMultilevelDomainName(), $itemId);
        $additional = $this->getApi()->getPmsManager()->getAdditionalInfo($this->getSelectedMultilevelDomainName(), $itemId);
        
        $item->bookingItemName = $_POST['data']['name'];
        $item->description = $_POST['data']['description'];
        $additional->hideFromCleaningProgram = $_POST['data']['hideFromCleaningProgram'] == "true";
        $additional->squareMetres = $_POST['data']['squareMetres'];
        $this->getApi()->getBookingEngine()->saveBookingItem($this->getSelectedMultilevelDomainName(), $item);
        $this->getApi()->getPmsManager()->updateAdditionalInformationOnRooms($this->getSelectedMultilevelDomainName(), $additional);
        if(isset($_POST['data']['itemtype']) && $_POST['data']['itemtype'] != $item->bookingItemTypeId) {
            $this->getApi()->getBookingEngine()->changeBookingItemType($this->getSelectedMultilevelDomainName(), $item->id, $_POST['data']['itemtype']);
        }
    }
    
    public function formatDate($row) {
        return date("d.m.Y", strtotime($row->rowCreatedDate));
    }
    public function formatName($row) {
        $res = "";
        if($row->hideFromCleaningProgram) {
            $res .= "<i class='fa fa-eye' style='color:gray; float:right;' title='Not visible in cleaning program'></i>";
        }
        $res .= "<b>" . $row->name . "</b><bR>";
        $res .= $row->description;
        return $res;
    }
    public function createRoom() {
        if($_POST['data']['type'] == "gsconference") {
            $item = $this->getApi()->getBookingEngine()->getBookingItemType($this->getSelectedMultilevelDomainName(), "gsconference");
            if(!$item) {
                $type = new \core_bookingengine_data_BookingItemType();
                $type->id = "gsconference";
                $type->visibleForBooking = false;
                $type->name = "Conference rooms";
                $type->systemCategory = 1;
                $this->getApi()->getBookingEngine()->updateBookingItemType($this->getSelectedMultilevelDomainName(), $type);
            }
        }
        
        $name = $_POST['data']['name'];
        $item = new \core_bookingengine_data_BookingItem();
        $item->bookingItemName = $name;
        $item->bookingItemTypeId = $_POST['data']['type'];
        $this->getApi()->getBookingEngine()->saveBookingItem($this->getSelectedMultilevelDomainName(), $item);
    }
    
    public function setNewTypeSorting() {
        $i = 1;
        foreach($_POST['data']['sortlist'] as $typeid) {
            $type = $this->getApi()->getBookingEngine()->getBookingItemType($this->getSelectedMultilevelDomainName(), $typeid);
            $type->order = $i;
            $this->getApi()->getBookingEngine()->updateBookingItemType($this->getSelectedMultilevelDomainName(), $type);
            $i++;
       }
    }    
    
    public function loadSortingTypes() {
        $this->includefile("typesorting");
    }
    
    public function loadSortingItems() {
        $this->includefile("itemsorting");
    }
    
    public function setNewSorting() {
        $i = 1;
        foreach($_POST['data']['sortlist'] as $itemid) {
            $item = $this->getApi()->getBookingEngine()->getBookingItem($this->getSelectedMultilevelDomainName(), $itemid);
            $item->order = $i;
            $this->getApi()->getBookingEngine()->saveBookingItem($this->getSelectedMultilevelDomainName(), $item);
            $i++;
       }
    }
    
    public function formatCleanedDate($row) {
        if(!$row->lastCleaned) {
            return "N/A";
        }
        return date("d.m.Y", strtotime($row->lastCleaned));
    }
    
    public function createAccesory() {
        $accessory = new \core_pmsmanager_PmsRoomTypeAccessory();
        $accessory->title = $_POST['data']['name'];
        $this->getApi()->getPmsManager()->saveAccessory($this->getSelectedMultilevelDomainName(), $accessory);
    }
    
    public function PmsManager_loadTypeResult() {
        $this->includefile("roomtypeconfigurationpanel");
    }
    
    public function PmsRoomConfiguration_loadRoomConfiguration() {
        $this->includefile("roomconfigurationpanel");
    }
    
    public function printImagesForType() {
        $id = $_POST['data']['typeId'];
        $additional = $this->getApi()->getPmsManager()->getAdditionalTypeInformationById($this->getSelectedMultilevelDomainName(), $id);
        foreach($additional->images as $img) {
            echo "<span class='imagecontainer'>";
            if($img->isDefault) {
                echo "<i class='fa fa-check' title='Default image'></i>";
            }
            echo "<i class='fa fa-trash-o deleteimage' title='Delete image' data-id='".$img->fileId."' data-typeid='".$id."'></i>";
            echo "<img src='displayImage.php?id=".$img->fileId."&width=80&height=80' class='setdefaultimg' data-fileid='".$img->fileId."' data-typeid='".$id."'>";
            echo "</span>";
        }
    }
    
    public function saveTypeSettings() {
        $type = $this->getApi()->getBookingEngine()->getBookingItemType($this->getSelectedMultilevelDomainName(), $_POST['data']['id']);
        $additional = $this->getApi()->getPmsManager()->getAdditionalTypeInformationById($this->getSelectedMultilevelDomainName(), $type->id);
        $languages = $this->getFactory()->getLanguageCodes();
        $languages[] = $this->getFactory()->getCurrentLanguage();
        $current = $this->getFactory()->getCurrentLanguage();
        
        $type->size = $_POST['data']['size'];
        $type->name = $_POST['data'][$current.'_name'];
        $type->description = $_POST['data'][$current.'_description'];
        $type->systemCategory = $_POST['data']['systemCategory'];
        
        foreach($languages as $key) {
            $type->translationStrings->{$key. "_name"} = json_encode($_POST['data'][$key.'_name']);
            $type->translationStrings->{$key. "_description"} = json_encode($_POST['data'][$key.'_description']);
        }
        
        
        $additional->numberOfChildren = $_POST['data']['numberOfChildren'];
        $additional->numberOfAdults = $_POST['data']['numberOfAdults'];
        $additional->defaultNumberOfBeds = $_POST['data']['defaultNumberOfBeds'];
        $additional->defaultNumberOfChildBeds = $_POST['data']['defaultNumberOfChildBeds'];
        $additional->maxNumberOfBeds = $_POST['data']['maxNumberOfBeds'];
        $additional->maxNumberOfChildBeds = $_POST['data']['maxNumberOfChildBeds'];
        $additional->accessories = array();
        foreach($_POST['data'] as $key => $val) {
            if(stristr($key, "accessory_") && $val == "true") {
                $additional->accessories[] = str_replace("accessory_", "", $key);
            }
        }
        
        if($_POST['data']['visibleForBooking'] == "yes") {
            $type->visibleForBooking = true;
        } else {
            $type->visibleForBooking = "false";
        }
        $this->getApi()->getBookingEngine()->updateBookingItemType($this->getSelectedMultilevelDomainName(), $type);
        $this->getApi()->getPmsManager()->saveAdditionalTypeInformation($this->getSelectedMultilevelDomainName(), $additional);
        
        $product = $this->getApi()->getProductManager()->getProduct($type->productId);
        $product->taxgroup = $_POST['data']['tax'];
        $this->getApi()->getProductManager()->saveProduct($product);
        
        $this->getApi()->getBookingEngine()->changeDepartmentOnType($this->getSelectedMultilevelDomainName(), $type->id, $_POST['data']['department']);
        
    }
    
    public function changeIcon() {
        $accessories = $this->getApi()->getPmsManager()->getAccesories($this->getSelectedMultilevelDomainName());
        foreach($accessories as $acc) {
            if($acc->id == $_POST['data']['id']) {
                $acc->icon = $_POST['data']['icon'];
                $this->getApi()->getPmsManager()->saveAccessory($this->getSelectedMultilevelDomainName(), $acc);
                return;
            }
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
        $additional = $this->getApi()->getPmsManager()->getAdditionalTypeInformationById($this->getSelectedMultilevelDomainName(), $typeId);
        $additional->images[] = $imageFile;
        $this->getApi()->getPmsManager()->saveAdditionalTypeInformation($this->getSelectedMultilevelDomainName(), $additional);
        
        $this->printImagesForType($typeId);
    }
    
    
    public function createType() {
        $room = $_POST['data']['name'];
        $type = $this->getApi()->getBookingEngine()->createABookingItemType($this->getSelectedMultilevelDomainName(), $room);
        $product = $this->getApi()->getProductManager()->createProduct();
        $product->name = $type->name;
        $this->getApi()->getProductManager()->saveProduct($product);
        $type->productId = $product->id;
        $this->getApi()->getBookingEngine()->updateBookingItemType($this->getSelectedMultilevelDomainName(), $type);
    }
    
    public function deleteImage() {
        $id = $_POST['data']['id'];
        $typeid = $_POST['data']['typeid'];
        $addinfo = $this->getApi()->getPmsManager()->getAdditionalTypeInformationById($this->getSelectedMultilevelDomainName(), $typeid);
        $imgres = array();
        foreach($addinfo->images as $img) {
            if($img->fileId == $id) {
                continue;
            }
            $imgres[] = $img;
        }
        $addinfo->images = $imgres;
        $this->getApi()->getPmsManager()->saveAdditionalTypeInformation($this->getSelectedMultilevelDomainName(), $addinfo);
    }
    
    public function setDefaultImage() {
        $id = $_POST['data']['fileid'];
        $typeid = $_POST['data']['typeId'];
        $addinfo = $this->getApi()->getPmsManager()->getAdditionalTypeInformationById($this->getSelectedMultilevelDomainName(), $typeid);
        $imgres = array();
        foreach($addinfo->images as $img) {
            if($img->fileId == $id) {
                $img->isDefault = true;
            } else {
                $img->isDefault = false;
            }
            $imgres[] = $img;
        }
        $addinfo->images = $imgres;
        $this->getApi()->getPmsManager()->saveAdditionalTypeInformation($this->getSelectedMultilevelDomainName(), $addinfo);
        $this->printImagesForType($typeid);
    }
}
?>
