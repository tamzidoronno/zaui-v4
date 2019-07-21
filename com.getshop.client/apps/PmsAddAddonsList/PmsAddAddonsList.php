<?php
namespace ns_b72ec093_caa2_4bd8_9f32_e826e335894e;

class PmsAddAddonsList extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsAddAddonsList";
    }

    public function render() {
        echo "<div>";
        echo "<i class='fa fa-close hideaddaddonsarea' style='position:absolute; right: 10px; top: 10px; cursor:pointer;'></i>";
        $this->includefile("addaddonslist");
        echo "</div>";
    }
    
    public function loadAddonsToBeAddedPreview() {
        $this->includefile("addaddonstoroomstepDatePreview");
    }
    
    public function addAdvancedAddons() {
        $app = new \ns_f8cc5247_85bf_4504_b4f3_b39937bd9955\PmsBookingRoomView();
        $app->setData();
        $selectedRoom = $app->getSelectedRoom();
        $booking = $app->getPmsBooking();
        
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        $addonToUse = null;
        foreach($config->addonConfiguration as $addonConf) {
             if($addonConf->productId == $_POST['data']['productId']) {
                $addonToUse = $addonConf;
                break;
            }
        }
        $newAddons = array();
        foreach($_POST['data'] as $key => $val) {
            if(stristr($key, "rowindex_") && $val == "true") {
                $addon = new \core_pmsmanager_PmsBookingAddonItem();
                $index = str_replace("rowindex_", "", $key);
                $addon->count = $_POST['data']['count_'.$index];
                $addon->date = $this->convertToJavaDate(strtotime($_POST['data']['date_'.$index]));
                $addon->price = $_POST['data']['price_'.$index];
                if(stristr($addon->price, "%")) {
                    $addon->percentagePrice = str_replace("%","", $addon->price);
                    $addon->price = 0;
                }
                $addon->name = $_POST['data']['name'];
                $addon->productId = $addonToUse->productId;
                $addon->noRefundable = $addonToUse->noRefundable;
                $addon->addonId = $this->generate_uuid();
                $addon->bookingicon = $addonToUse->bookingicon;
                $addon->isUniqueOnOrder = $addonToUse->isUniqueOnOrder;
                
                if($_POST['data']['includedInRoomPrice'] == "true") {
                    $addon->isIncludedInRoomPrice = true;
                }

                $newAddons[] = $addon;
            }
        }
        foreach($booking->rooms as $room) {
            if($room->pmsBookingRoomId == $selectedRoom->pmsBookingRoomId) {
                foreach($newAddons as $tmp) {
                    $room->addons[] = $tmp;
                }
            }
        }
        $this->getApi()->getPmsManager()->saveBooking($this->getSelectedMultilevelDomainName(), $booking);
    }
    
    function generate_uuid() {
        return sprintf( '%04x%04x-%04x-%04x-%04x-%04x%04x%04x',
            mt_rand( 0, 0xffff ), mt_rand( 0, 0xffff ),
            mt_rand( 0, 0xffff ),
            mt_rand( 0, 0x0fff ) | 0x4000,
            mt_rand( 0, 0x3fff ) | 0x8000,
            mt_rand( 0, 0xffff ), mt_rand( 0, 0xffff ), mt_rand( 0, 0xffff )
        );
    }
    
    public function getDatesToAdd($start, $end, $room) {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedMultilevelDomainName());
        $addonItem = null;
        foreach($config->addonConfiguration as $id => $item) {
            if($item->productId == $_POST['data']['productId']) {
                $addonItem = $item;
            }
        }
        
        $dates = array();
        $result = array();
        
        if($_POST['data']['periode_selection'] == "startofstay") {
            $dates[] = $start;
        }
        if($_POST['data']['periode_selection'] == "endofstay") {
            $dates[] = $end;
        }
        if($_POST['data']['periode_selection'] == "single") {
            $dates[] = time();
        }
        if($_POST['data']['periode_selection'] == "repeat_weekly") {
            $data = new \stdClass();
            $data->firstEvent = new \stdClass();
            $data->firstEvent->start = $this->convertToJavaDate($start+60);
            $data->firstEvent->end = $this->convertToJavaDate($start+60);
            $data->repeatPeride = 1;
            $data->endingAt = $this->convertToJavaDate(strtotime(date("d.m.Y 23:59", $end)));
            
            $data->repeatMonday = $_POST['data']['repeat_mon'] == "true";
            $data->repeatTuesday = $_POST['data']['repeat_tue'] == "true";
            $data->repeatWednesday = $_POST['data']['repeat_wed'] == "true";
            $data->repeatThursday = $_POST['data']['repeat_thu'] == "true";
            $data->repeatFriday = $_POST['data']['repeat_fri'] == "true";
            $data->repeatSaturday = $_POST['data']['repeat_sat'] == "true";
            $data->repeatSunday = $_POST['data']['repeat_sun'] == "true";
            $data->repeatEachTime = 1;
            $data->avoidFirstEvent = true;
            
            $res = $this->getApi()->getPmsManager()->generateRepeatDateRanges($this->getSelectedMultilevelDomainName(), $data);
            foreach($res as $r) {
                $dates[] = strtotime($r->start);
            }
        }
        if($_POST['data']['periode_selection'] == "repeat_monthly") {
            $data = new \stdClass();
            $data->firstEvent = new \stdClass();
            $data->firstEvent->start = $this->convertToJavaDate($start);
            $data->firstEvent->end = $this->convertToJavaDate($start);
            $data->repeatPeride = 2;
            $data->endingAt = $this->convertToJavaDate($end);
            
            $res = $this->getApi()->getPmsManager()->generateRepeatDateRanges($this->getSelectedMultilevelDomainName(), $data);
            foreach($res as $r) {
                $dates[] = strtotime($r->start);
            }
        }
        if($_POST['data']['periode_selection'] == "alldays") {
            $toAdd = $start;
            while(true) {
                $dates[] = $toAdd;
                $toAdd = strtotime('+1 day', $toAdd);
                if($toAdd > $end || (date("d.m.Y", $toAdd) == date("d.m.Y", $end))) {
                    break;
                }
            }
        }
        
        
        foreach($dates as $date) {
            $object = new \stdClass();
            $object->count = $addonItem->count;
            if($_POST['data']['isperguest'] == "true") {
                $object->count = $room->numberOfGuests;
            }
            $object->percentagePrice = $addonItem->percentagePrice;
            $object->price = $addonItem->price;
            $result[$date] = $object;
        }
        
        return $result;
    }
    
    
    public function loadSecondAddAddonsStep() {
        $this->includefile("addonsstep2");
    }
    
    public function addProductToBooking() {
        $app = new ns_f8cc5247_85bf_4504_b4f3_b39937bd9955\PmsBookingRoomView();
        $app->setData();
        $productId = $_POST['data']['productId'];
        $room = $app->getSelectedRoom();
        
        $addons = $this->getApi()->getPmsManager()->createAddonsThatCanBeAddedToRoom($this->getSelectedMultilevelDomainName(), $productId, $room->pmsBookingRoomId);
        
        $initList = array();
        foreach($room->addons as $addon) {
            if(isset($addon->notAddedYet) && $addon->productId == $productId && !$addon->isSingle) {
                continue;
            }
            $initList[] = $addon;
        }
        
        foreach($addons as $addon) {
            $addon->notAddedYet = true;
            $initList[] = $addon;
        }
        $room->addons = $initList;
        $app->updateRoom($room);
    }
    
}
?>
