<?php


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
        if($room->room) {
            if($room->hasBeenCleaned || $room->roomCleaned) {
                $roomText .= "<i class='gsicon-gs-cleaning' style='color:blue' title='Room is clean'></i> " . $room->room;
            } else {
                $roomText .= "<i class='gsicon-gs-cleaning' title='Room is not cleaned'></i> " . $room->room;
            }
        }
        $roomText .= "<div class='secondary_text roominfosub'>".$room->roomType."</div>";
        return $roomText;
    }
    
    public function formatBookedFor($room) { 
        $res = "<div class='secondary_text booked_for'>" . $room->owner . "</div>";
        if($room->ownerDesc) {
            $res .= "<div style='white-space: nowrap;overflow: hidden;text-overflow: ellipsis; color:red;' title='".$room->ownerDesc."'>" . $room->ownerDesc . "</div>";
        }
        return $res;
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
    
    public function formatGuests($room) {
        $checkedIn = "";
        if($room->checkedIn) {
            $checkedIn = "<div class='guesticon checkedin'><i class='fa fa-smile-o' title='Guest has checked in'></i></div>";
        }
        
        $vistorText = "";
        $vistorText = "<div class='numberofguests'>$room->numberOfGuests <div class='guesticon'><i class='gsicon-user'></i></div>$checkedIn</div>";
        return $vistorText;
    }
    
    public function formatVistior($room, $hasSamleFakturaApp = false) {
        $vistorText = "";
        $name = "";
        if(isset($guest[0]->name)) {
            $name = $guest[0]->name;
        }
        
        $vistorText .= "<img src='/skin/flags/blank.gif' class='flag flag-".$room->countryCode."' style='float:left;'/><div class='guestinfo2' getshop_sorting='".$name."'>";
            foreach($room->guest as $guest) {
                $vistorText .= "<div class='guestname'>".$guest->name."</div>";
                if($guest->email) { $vistorText .= "<div class='guestemail'>" . $guest->email."</div>"; }
                if($guest->phone) { $vistorText .= "<div class='guestphone'>+" . $guest->prefix . $guest->phone . "</div>"; }
                $vistorText .= "<br>";
            }
            $vistorText .= "<span style='color:#bbb;padding-left:10px'>Speaking ".  strtolower($this->printLanguage($room->language));
            if($room->segmentName) {
                $vistorText .= ", - " . strtolower($room->segmentName);
            }
            $vistorText .= "</span>";
            $vistorText .= "<div></div><span class='secondary_text'>";
            $vistorText .= "</span>";

            if($room->requestedEndDate) {
                $vistorText .= "<div class='secondary_text'> Requested end date <span >" . date("d.m.Y", strtotime($room->requestedEndDate)) . "</span></div>";
            }

            if(@$filter->groupByBooking && $room->numberOfRoomsInBooking > 1) {
                $vistorText .=  '<div>+ ' . ($room->numberOfRoomsInBooking-1) . " addititional entries.</div>";
            }
            
            $vistorText .= "<span class='addedtocheckout dontExpand' roomid='".$room->pmsRoomId."'><i class='fa fa-check'></i> Added to checkout</span>";
            
        $vistorText .= "<span class='quickfunctions' roomid='".$room->pmsRoomId."'>";
        if($hasSamleFakturaApp) {
            $vistorText .= "<span style='float:left;padding-left:10px;' class='startcheckout dontExpand'>Start checkout</span>";
        }
        if($room->progressState != "deleted") {
            $vistorText .= "<i class='fa fa-trash-o dontExpand quickfunction' title='Delete room' type='delete'></i> ";
            $vistorText .= "<i class='fa fa-exchange dontExpand quickfunction' title='Change stay' type='changestay'></i> ";
        } else {
            $vistorText .= "<i class='fa fa-undo dontExpand quickfunction' title='Undo deletion' type='delete'></i> ";
        }
        $vistorText .= "<i class='fa fa-fighter-jet dontExpand quickfunction' title='Toggle as non refundable' type='togglenonref'></i> ";
        $vistorText .= "<i class='fa fa-dollar dontExpand quickfunction' title='Change dayprice' type='changeprice'></i> ";
        $vistorText .= "<i class='fa fa-plus-circle dontExpand quickfunction' title='Update addons' type='updateaddons'></i> ";
        $vistorText .= "<i class='fa fa-users dontExpand quickfunction' title='Change guest information' type='guestinfo'></i> ";
        if($room->numberOfRoomsInBooking > 1) {
            if($_SERVER['REQUEST_URI'] == "/data.php") {
                $base = "/pms.php";
            } else {
                $base = "/";
            }
            $vistorText .= "<a href='$base?page=groupbooking&bookingId=".$room->bookingId."' style='color:#bbb;'>";
            $vistorText .= "<i class='fa fa-arrows dontExpand' title='This room is part of a group of ".$room->numberOfRoomsInBooking." rooms'></i></a> ";
            
        }
        $vistorText .= "<i class='fa fa-edit dontExpand quickfunction' title='Change room' type='changeroom'></i> ";
        
        $vistorText .= "</span></div>";
        $vistorText .= "<span class='quickmenuoption dontExpand'></span> ";
        
        return $vistorText;
    }
    
    public function formatState($room) {
        $text = "";
        if ($room->progressState == "waitingforlock") {
            $waiting = $this->pmsSearchBooking->__f("Receiving code from lock...");
            $text = "<span title='$waiting' class='fa-stack fa-lg'><i class='fa fa-hourglass fa-stack-1x'></i> <i style='color: #FFF' class='fa fa-lock  fa-stack-3x'></i></span>";
        } else if($room->progressState == "confirmed") {
            $waiting = $this->pmsSearchBooking->__f("Booking is confirmed");
            $text = "<span title='$waiting' ><i class='gsicon-clipboard-check'></i></span>";
        } else if ($room->progressState == "deleted") {
            $waiting = $this->pmsSearchBooking->__f("Room has been deleted");
            $text = "<span title='$waiting'><i class='gsicon-trashcan' style='color: red'></i></span>";
        } else if ($room->progressState == "notpaid") {
            $waiting = $this->pmsSearchBooking->__f("Room not paid");
            $text = "<span title='$waiting'><i class='gsicon-bag-dollar' style='color: red'></i></span>";
        } else if ($room->progressState == "overbook") {
            $waiting = $this->pmsSearchBooking->__f("Rooms has been overbooked");
            $text = "<span title='Room is overbooked'><i class='fa fa-book' style='color: red'></i></span>";
        } else if ($room->progressState == "replaced") {
            $waiting = $this->pmsSearchBooking->__f("Booking has been replaced with a new booking by the OTA");
            $text = "<span title='Booking has been replaced with a new booking by the OTA'><i class='fa fa-unlink' style='color: gray'></i></span>";
        } else if ($room->progressState == "ended") {
            $waiting = $this->pmsSearchBooking->__f("Ended");
            $text = "<span title='$waiting'><i class='gsicon-checkered-flag'></i style='color: green'></span>";
        } else if ($room->progressState == "active") {
            $waiting = $this->pmsSearchBooking->__f("All good (active)");
            $text = "<span title='$waiting'> <i class='gsicon-list'></i></span>";
        } else {
            $text = $room->progressState;
        }
        
        $channels = $this->getChannels();
        $channelText = $room->channel;
        if(isset($channels[$room->channel])) {
            $channelText = $channels[$room->channel];
        }
        
        $text .= "<div class='channelDesc'>" . $channelText . "</div>";
        if($room->checkedIn && !$room->checkedOut) {
            $text .= "<div class='channelDesc' title='Room checked in' style='text-align:center;'>Is in</div>";
        }
        
        return $text;
    }
    
    public function formatPrice($room) {
        $priceData = "<div><div class='price'>".round($room->price)."</div><div class='pricetagright'></div></div>";
        return $priceData;
    }
    
    public function formatRegDate($room) {
        
        $date = \GetShopModuleTable::formatDate($room->regDate);
        $date = "<div class='rowdate1'>".date("d.m.y", strtotime($room->regDate))."</div> <div class='rowdate2'>".date("H:i", strtotime($room->regDate))."</div>";
        if(!$room->bookingEngineId) {
            $date .= "<i class='fa fa-warning dontExpand' title='Booking not added to booking engine' gstype='clicksubmit' method='tryAddToBookingEngine' gsname='id' gsvalue='".$room->pmsRoomId."'></i>";
        }
        return $date;
    }
    
    public function formatStartPeriode($room) {
        $date = \GetShopModuleTable::formatDate($room->start);
        $date .= "<i class='fa fa-arrows-h'></i>";
        return $date;
    }
    
    public function formatTotalPrice($room) {
        $priceData = "";
        if($room->totalUnpaidCost > 0.5) {
            $priceData = "<div><div class='unpaidprice dontExpand' title='Missing payment on room' roomid='".$room->pmsRoomId."'>".round($room->totalCost)."</div><div class='pricetagright'></div></div>";
        } else {
            $priceData = "<div title='Total cost for this room'>" . round($room->totalCost) . "</div>";
        }
        if($room->totalUnsettledAmount > 0.5) {
            $priceData .= "<div class='unsettledamountwarning dontExpand' roomid='".$room->pmsRoomId."' title='Amount not created orders for yet'>".$room->totalUnsettledAmount."</div>";
        }
        
        if($room->nonrefundable) {
            $priceData .= "<div style='color:#bbb;' title='This room is non refundable'>nonref</div>";
        }
        
        if($room->hasUnchargedPrePaidOrders) {
            $priceData .= "<div style='color:red;font-weight:bold;' title='NOT CHARGED: Remember to charge OTA for this stay, mark order as paid after.'>N/C</div>";
        }
        
        return $priceData;

    }
    
    public function formatEndPeriode($room) {
        $date = \GetShopModuleTable::formatDate($room->end);
        return $date;
    }
    
    public function formatExpandButton() {
        return "<i class='gsicon-chevrons-expand-vertical'></i>";
    }
    
    public function createAddonText($room, $plaintext = false) {
        $products = $this->pmsSearchBooking->getAllProducts();
        $typesAdded = array();
        $iconsAdded = array();
        $total = 0;
        foreach($room->addons as $addon) {
            if($addon->addonType == 1) {
                continue;
            }
            if(!isset($typesAdded[$addon->productId])) {
                $typesAdded[$addon->productId]=0;
            }
            $typesAdded[$addon->productId] += $addon->count;
            $iconsAdded[$addon->productId] = $addon->bookingicon;
            $total += ($addon->price * $addon->count);
        }
        $res = array();
        foreach($typesAdded as $prodId => $val) {
            if(isset($products[$prodId])) {
                $title = $val . " x " . $products[$prodId]->name;
            } else {
                $title = $val . " x deleted product";
            }
            if(isset($products[$prodId]->name)) {
                $name = $products[$prodId]->name;
                $words = explode(" ", $name);
                $acronym = "";
                foreach ($words as $w) {
//                    $w = strip_tags($w);
//                    echo "<div>" . $w[0] . " - " . $w . "</div>";
                      $acronym .= substr($w,0,1);
                }
                $acronym = htmlentities($acronym);
                $acronym = "(".strtoupper($acronym) .")";
                if(isset($iconsAdded[$addon->productId]) && $iconsAdded[$addon->productId]) {
                    $acronym = "<i class='fa fa-" . $iconsAdded[$addon->productId] . "'></i>";
                }
                if($plaintext) {
                    $res[] = $title;
                } else {
                    $res[] = "<span title='$title' style='cursor:pointer;'>$acronym</span>";
                }
            } else {
                if($plaintext) {
                    $res[] = "(deleted product)";
                } else {
                    $res[] = "<span title='$title' style='cursor:pointer;'>(deleted product)</span>";
                }
            }
        }
        $text = "";
        $i = 0;
        if(sizeof($res) > 0) {
            $text = join("," , $res);
        }
//
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

    public function getChannels() {
        $channels = array();
        $channels['web'] = "Admins";
        $channels['website'] = "Website";
        $channels['wubook_0'] = "Wubook";
        $channels['wubook_1'] = "Exp.";
        $channels['wubook_2'] = "b.com";
        $channels['wubook_37'] = "GDS";
        $channels['wubook_43'] = "ABNB";
        $channels['wubook_11'] = "HSR";
        $channels['wubook_28'] = "STRP";
        $channels['terminal'] = "Terminal";
        return $channels;
    }

    public function printLanguage($langcode) {
        if($langcode == "nb_NO") {
            $langcode = "no";
        }
        if($langcode == "en_EN") {
            $langcode = "en"; 
       }
        if($langcode == "en_en") {
            $langcode = "en";
        }
        $sendmsgconfig = new \ns_624fa4ac_9b27_4166_9fc3_5c1d1831b56b\PmsSendMessagesConfiguration();
        if(!$langcode) {
            $langcode="no";
        }
        $langcode = strtolower($langcode);
        $codes = $sendmsgconfig->getLanguageCodes();
        return $codes[$langcode];
    }

}
