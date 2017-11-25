<?php
namespace ns_a22747ef_10b1_4f63_bef8_41c02193edd8;

class PmsRoomConfiguration extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }
    
    public function getAccessories() {
        return array();
    }
    

    public function getName() {
        return "PmsRoomConfiguration";
    }

    public function render() {
        $this->includefile("roomtypeconfig");
        $this->includefile("roomconfig");
    }
    
    public function formatDescription($row) {
//        print_r($row);
        $type = $this->getApi()->getBookingEngine()->getBookingItemType($this->getSelectedMultilevelDomainName(), $row->typeId);
        $res = "<b>" . $row->name . "</b><br>" . $row->description . "<bR><b>Accessorier: <i class='fa fa-car'></i> <i class='fa fa-calculator'></i> <i class='fa fa-bell'></i></b>";
        if($type->visibleForBooking) {
            $res .= "<i class='fa fa-eye' style='color:green; float:right;' title='This room is visible for booking'></i>";
        } else {
            $res .= "<i class='fa fa-eye' style='color:gray; float:right;' title='This room is not visible for booking'></i>";
        }
        return $res;
    }
    
    public function formatDate($row) {
        return date("d.m.Y", strtotime($row->rowCreatedDate));
    }
    
    public function createAccesory() {
        $accessory = new \core_pmsmanager_PmsRoomTypeAccessory();
        $accessory->title = $_POST['data']['name'];
        $this->getApi()->getPmsManager()->saveAccessory($this->getSelectedMultilevelDomainName(), $accessory);
    }
    
    public function PmsRoomConfiguration_loadTypeResult() {
        $id = $_POST['data']['id'];
        $type = $this->getApi()->getBookingEngine()->getBookingItemType($this->getSelectedMultilevelDomainName(), $id);
        $additional = $this->getApi()->getPmsManager()->getAdditionalTypeInformationById($this->getSelectedMultilevelDomainName(), $id);
        $notVisible = !$type->visibleForBooking ? "SELECTED" : "";
        $images = $additional->images;
        $accessories = $this->getApi()->getPmsManager()->getAccesories($this->getSelectedMultilevelDomainName());
        
        echo "<div gstype='form'>";
        echo "<b>Name:</b><br>";
        echo "<input type='txt' value='" . $type->name . "' class='gsniceinput1' style='width:100%;box-sizing:border-box;' gsname='name'><br><br>";
        echo "<b>Description:</b><br>";
        echo "<textarea style='width: 100%; height: 90px;border:solid 1px #dcdcdc;box-sizing:border-box; padding: 10px;' gsname='description'>" . $type->description . "</textarea>";
        
        echo "<table width='100%'>";
        echo "<tr>";
        echo "<th>Guests</th>";
        echo "<th>Children</th>";
        echo "<th>Adults</th>";
        echo "<th>Beds</th>";
        echo "<th>Beds for children</th>";
        echo "<th>Max beds</th>";
        echo "<th>Max beds for children</th>";
        echo "<th>Visible for booking</th>";
        echo "</tr>";
        echo "<tr style='text-align:center;'>";
        echo "<td><input type='txt' class='gsniceinput1' style='width: 40px;text-align:center;' value='".$type->size."' gsname='size'></td>";
        echo "<td><input type='txt' class='gsniceinput1' style='width: 40px;text-align:center;' value='".$additional->numberOfChildren."' gsname='numberOfChildren'></td>";
        echo "<td><input type='txt' class='gsniceinput1' style='width: 40px;text-align:center;' value='".$additional->numberOfAdults."' gsname='numberOfAdults'></td>";
        echo "<td><input type='txt' class='gsniceinput1' style='width: 40px;text-align:center;' value='".$additional->defaultNumberOfBeds."' gsname='defaultNumberOfBeds'></td>";
        echo "<td><input type='txt' class='gsniceinput1' style='width: 40px;text-align:center;' value='".$additional->defaultNumberOfChildBeds."' gsname='defaultNumberOfChildBeds'></td>";
        echo "<td><input type='txt' class='gsniceinput1' style='width: 40px;text-align:center;' value='".$additional->maxNumberOfBeds."' gsname='maxNumberOfBeds'></td>";
        echo "<td><input type='txt' class='gsniceinput1' style='width: 40px;text-align:center;' value='".$additional->maxNumberOfChildBeds."' gsname='maxNumberOfChildBeds'></td>";
        echo "<td><select class='gsniceselect1' gsname='visibleForBooking'><option value='yes'>Yes</option><option value='no' $notVisible>No</option></select></td>";
        echo "</tr>";
        echo "</table>";
        echo "<b>Accesories: <span class='createnewaccessory' style='cursor:pointer;'>(create new)</span></b><br>";
        echo "<table width='100%'>";
        echo "<tr>";
        $i = 1;
        foreach($accessories as $accessory) {
            echo "<td><input type='checkbox' gsname='accessory_".$accessory->id."'> <i class='fa fa-briefcase'></i> ". $accessory->title . "</td>";
            if($i % 8 == 0) {
                echo "</tr><tr>";
            }
            $i++;
        }
        echo "</tr>";
        echo "</table>";
        echo "<div style='text-align:right;padding-top: 20px;'>";
        echo "<input type='button' style='padding:9px; width: 200px;' gstype='submit' value='Save'>";
        echo "</div>";
        echo "</div>";
        
        echo "<br><br>";
        echo "<b>Images: <input type='button' class='uploadTypeImage' value='Upload an image' typeid='".$type->id."'></b><br>";
        echo "<div class='imagearea'>";
        $_POST['data']['typeId'] = $type->id;
        $this->printImagesForType();
        echo "</div>";
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
        $type = $_POST['data']['name'];
        $this->getApi()->getBookingEngine()->createABookingItemType($this->getSelectedMultilevelDomainName(), $type);
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
