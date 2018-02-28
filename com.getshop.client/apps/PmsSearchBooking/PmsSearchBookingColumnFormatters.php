<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

namespace ns_961efe75_e13b_4c9a_a0ce_8d3906b4bd73;

/**
 * Description of PmsSearchBookingColumnFormatters
 *
 * @author ktonder
 */
class PmsSearchBookingColumnFormatters {
    private $pmsSearchBooking;
    
    function __construct($pmsSearchBooking) {
        $this->pmsSearchBooking = $pmsSearchBooking;
    }

    public function formatRoom($room) {
        $roomText = "";
        if($room->keyIsReturned) {
            $roomText .=  '<i class="fa fa-key" title="Key has been returned" style="color:green;"></i> ';
        }
        $roomText .= $room->room;
        $roomText .= "<div class='secondary_text roominfosub'>".$room->roomType."</div>";
        return $roomText;
    }
    
    public function formatBookedFor($room) { 
        return "<div class='secondary_text booked_for'>" . $room->owner . "</div>";
    }
    
    public function formatAddons($room) {
        $comments = "";
        if(isset($room->bookingComments)) {
            foreach($room->bookingComments as $time => $val) {
                if($val->deleted) {
                    continue;
                }
                $comments .= $val->comment . "<br>";
            }
        }
       
        $vistorText = "";
        if($room->nonrefundable) {
            $vistorText .= "<i class='fa fa-usd' title='Non refundable'></i> ";
        }
        if(trim($comments)) {
            $vistorText .= "<i class='fa fa-comment' title='$comments'></i> ";
        }
        
        $breakfasts = $this->hasAddon($room, 1);
        
        if($breakfasts) {
            $vistorText .= "<i class='fa fa-cutlery' title='Breakfast included: $breakfasts'></i> ";
        }
        
        $vistorText .= $this->createAddonText($room) . " ";
        return $vistorText;
    }
    
    public function formatVistior($room) {
        $checkedIn = "";
        if($room->checkedIn) {
            $checkedIn = "<div class='guesticon checkedin'><i class='fa fa-smile-o' title='Guest has checked in'></i></div>";
        }
        
        $vistorText = "";
        $vistorText = "<span class='quickmenuoption dontExpand'></span><div class='numberofguests'>$room->numberOfGuests <div class='guesticon'><i class='icon-user'></i></div>$checkedIn</div>";
        
        $vistorText .= "<div class='guestinfo2'>";
            foreach($room->guest as $guest) {
                $vistorText .= "<div class='guestname'>".$guest->name."</div>";
                if($guest->email) { $vistorText .= "<div class='guestemail'>" . $guest->email."</div>"; }
                if($guest->phone) { $vistorText .= "<div class='guestphone'>+" . $guest->prefix . $guest->phone . "</div>"; }
                $vistorText .= "<br>";
            }

            $vistorText .= "<div></div><span class='secondary_text'>";
            $vistorText .= "</span>";

            if($room->requestedEndDate) {
                $vistorText .= "<div class='secondary_text'> Requested end date <span >" . date("d.m.Y", strtotime($room->requestedEndDate)) . "</span></div>";
            }

            if(@$filter->groupByBooking && $room->numberOfRoomsInBooking > 1) {
                $vistorText .=  '<div>+ ' . ($room->numberOfRoomsInBooking-1) . " addititional entries.</div>";
            }
            
        $vistorText .= "<span class='quickfunctions' roomid='".$room->pmsRoomId."'>";
        if($room->progressState != "deleted") {
            $vistorText .= "<i class='fa fa-trash-o dontExpand quickfunction' title='Delete room' type='delete'></i> ";
            $vistorText .= "<i class='fa fa-exchange dontExpand quickfunction' title='Change stay' type='changestay'></i> ";
        }
        $vistorText .= "<i class='fa fa-dollar dontExpand quickfunction' title='Change dayprice' type='changeprice'></i> ";
        $vistorText .= "<i class='fa fa-plus-circle dontExpand quickfunction' title='Update addons' type='updateaddons'></i> ";
        $vistorText .= "<i class='fa fa-users dontExpand quickfunction' title='Change guest information' type='guestinfo'></i> ";
        $vistorText .= "<i class='fa fa-arrow-right dontExpand quickfunction' title='Process payments' type='payments'></i>";
        $vistorText .= "</span></div>";
        
        return $vistorText;
    }
    
    public function formatState($room) {
        $text = "";
        if ($room->progressState == "waitingforlock") {
            $waiting = $this->pmsSearchBooking->__f("Receiving code from lock...");
            $text = "<span title='$waiting' class='fa-stack fa-lg'><i class='fa fa-hourglass fa-stack-1x'></i> <i style='color: #FFF' class='fa fa-lock  fa-stack-3x'></i></span>";
        } else if($room->progressState == "confirmed") {
            $waiting = $this->pmsSearchBooking->__f("Booking is confirmed");
            $text = "<span title='$waiting' ><i class='icon-clipboard-check'></i></span>";
        } else if ($room->progressState == "deleted") {
            $waiting = $this->pmsSearchBooking->__f("Room has been deleted");
            $text = "<span title='$waiting'><i class='icon-trashcan' style='color: red'></i></span>";
        } else if ($room->progressState == "notpaid") {
            $waiting = $this->pmsSearchBooking->__f("Room not paid");
            $text = "<span title='$waiting'><i class='icon-bag-dollar' style='color: red'></i></span>";
        } else if ($room->progressState == "ended") {
            $waiting = $this->pmsSearchBooking->__f("Ended");
            $text = "<span title='$waiting'><i class='icon-checkered-flag'></i style='color: green'></span>";
        } else if ($room->progressState == "active") {
            $waiting = $this->pmsSearchBooking->__f("All good (active)");
            $text = "<span title='$waiting'> <i class='icon-list'></i></span>";
        } else {
            $text = $room->progressState;
        }
        
        $channels = array();
        $channels['wubook_1'] = "Exp.";
        $channels['wubook_2'] = "b.com";
        
        $channelText = $room->channel;
        if(isset($channels[$room->channel])) {
            $channelText = $channels[$room->channel];
        }
        
        $text .= "<div class='channelDesc'>" . $channelText . "</div>";
        
        return $text;
    }
    
    public function formatPrice($room) {
        $priceData = "<div><div class='price'>".round($room->price)."</div><div class='pricetagright'></div></div>";
        
        return $priceData;
    }
    
    public function formatRegDate($room) {
        
        $date = \GetShopModuleTable::formatDate($room->regDate);
        $date = "<div class='rowdate1'>".date("d.m.y", strtotime($room->regDate))."</div><div class='rowdate2'>".date("H:i", strtotime($room->regDate))."</div>";
        if(!$room->bookingEngineId) {
            $date .= "<i class='fa fa-warning' title='Booking not added to booking engine' gstype='clicksubmit' method='tryAddToBookingEngine' gsname='id' gsvalue='".$room->pmsRoomId."'></i>";
        }
        return $date;
    }
    
    public function formatStartPeriode($room) {
        $date = \GetShopModuleTable::formatDate($room->start);
        $date .= "<i class='fa fa-arrows-h'></i>";
        return $date;
    }
    
    public function formatTotalPrice($room) {
        return $priceData = "<div title='Total cost for this room'>" . round($room->totalCost) . "</div>";
    }
    
    public function formatEndPeriode($room) {
        $date = \GetShopModuleTable::formatDate($room->end);
        return $date;
    }
    
    public function formatExpandButton() {
        return "<i class='icon-chevrons-expand-vertical'></i>";
    }
    
    private function createAddonText($room) {
        $products = $this->pmsSearchBooking->getAllProducts();
        $typesAdded = array();
        $total = 0;
        foreach($room->addons as $addon) {
            if($addon->addonType == 1) {
                continue;
            }
            if(!isset($typesAdded[$addon->productId])) {
                $typesAdded[$addon->productId]=0;
            }
            $typesAdded[$addon->productId] += $addon->count;
            $total += ($addon->price * $addon->count);
        }
        $res = array();
        foreach($typesAdded as $prodId => $val) {
            $title = $val . " x " . $products[$prodId]->name;
            if(isset($products[$prodId]->name)) {
                $name = "";
//                $name = $this->getFirstWords($products[$prodId]->name);
//                $res[] = $name;
                $res[] = "<span title='$title' style='cursor:pointer;'>($name)</span>";
            }
        }
        $text = "";
        $i = 0;
        if(sizeof($res) > 0) {
            $text = join("," , $res);
        }

        return $text;
    }
    
    
     /**
     * @param \core_pmsmanager_PmsBookingRooms $room
     * @param type $addonType
     */
    private function hasAddon($room, $addonType) {
        $count = 0;
        if(isset($room->addons)) {
            foreach($room->addons as $addon) {
                if($addon->addonType == $addonType) {
                    $count += $addon->count;
                }
            }
        }
        return $count;
    }
    
}
