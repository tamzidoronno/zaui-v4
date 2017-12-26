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
    
    public function formatVistior($room) {
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
        
        foreach($room->guest as $guest) {
            $vistorText .= $guest->name;
            if($guest->email) { $vistorText .= " - " . $guest->email; }
            if($guest->phone) { $vistorText .= "<span class='secondary_text'>+" . $guest->prefix . $guest->phone . "</span>"; }
            $vistorText .= "<br>";
        }
        
        $vistorText .= "<div class='secondary_text booked_for'>" . $room->owner . "</div>";
        
        $vistorText .= "<div></div><span class='secondary_text'>" .$room->numberOfGuests  . " guests";
        if($room->checkedIn) {
            $vistorText .= " <i class='fa fa-smile-o' title='Guest has checked in'></i>";
        }
        $vistorText .= "</span>";
        
        if($room->requestedEndDate) {
            $vistorText .= "<div class='secondary_text'> Requested end date <span >" . date("d.m.Y", strtotime($room->requestedEndDate)) . "</span></div>";
        }
        
        if(@$filter->groupByBooking && $room->numberOfRoomsInBooking > 1) {
            $vistorText .=  '<div>+ ' . ($room->numberOfRoomsInBooking-1) . " addititional entries.</div>";
        }
        
        return $vistorText;
    }
    
    public function formatState($room) {
        if ($room->progressState == "waitingforlock") {
            $waiting = $this->pmsSearchBooking->__f("Receiving code from lock...");
            return "<span title='$waiting' class='fa-stack fa-lg'><i class='fa fa-hourglass fa-stack-1x'></i> <i style='color: #FFF' class='fa fa-lock  fa-stack-3x'></i></span>";
        }
        
        if ($room->progressState == "notpaid") {
            $waiting = $this->pmsSearchBooking->__f("Room not paid");
            return "<span title='$waiting' class='fa-stack fa-lg'><i class='fa fa-usd fa-stack-1x'></i> <i class='fa fa-ban fa-stack-2x'></i></span>";
        }
        
        if ($room->progressState == "ended") {
            $waiting = $this->pmsSearchBooking->__f("Ended");
            return "<span title='$waiting' class='fa-stack fa-lg'><i class='fa fa-check fa-stack-1x'></i> <i class='fa fa-calendar fa-stack-2x'></i></span>";
        }
        
        if ($room->progressState == "active") {
            $waiting = $this->pmsSearchBooking->__f("All good (active)");
            return "<span title='$waiting' class='fa-stack fa-lg'> <i class='fa fa-check fa-stack-2x'></i></span>";
        }
        
        return $room->progressState;
    }
    
    public function formatPrice($room) {
        $priceData = round($room->price);
        if($room->totalCost) {
            $priceData = round($room->price) . "<div style='color:#aaa' title='Total cost for this room'>(" . round($room->totalCost) . ")</div>";
        }
        if(isset($filter->filterType) && $filter->filterType == "unsettled") {
            $priceData = $room->totalUnsettledAmount;
        }
        
        return $priceData;
    }
    
    public function formatRegDate($room) {
        $date = date("d.m.y H:i", strtotime($room->regDate));
        if(!$room->bookingEngineId) {
            $date .= "<i class='fa fa-warning' title='Booking not added to booking engine' gstype='clicksubmit' method='tryAddToBookingEngine' gsname='id' gsvalue='".$room->pmsRoomId."'></i>";
        }
        return $date;
    }
    
    public function formatStartPeriode($room) {
        $diff = ($room->end - $room->start)/1000;
        if($diff < 40000) {
            return date("d.m.Y H:i", $room->start/1000) . " - " . date("H:i", $room->end/1000);
        } else {
            return date("d.m.y H:i", $room->start/1000) . "<br>" . date("d.m.y H:i", $room->end/1000);
        }
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
