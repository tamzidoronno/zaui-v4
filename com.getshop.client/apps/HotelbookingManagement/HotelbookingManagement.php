<?php
namespace ns_15e67fa1_c862_4bc3_9b17_dfd818f30712;

class HotelbookingManagement extends \ApplicationBase implements \Application {
    var $entries;
    var $dept;
    var $currentMenuEntry;
    
    
    function __construct() {
    }

    public function getDescription() {
        return "HotelbookingManagement";
    }
    
    public function toggleautodelete() {
        $id = $_POST['data']['bdataid'];
        $this->getApi()->getHotelBookingManager()->toggleAvoidAutoDelete($id);
    }
    
    public function getAvailablePositions() {
        return "left";
    }
    
    public function removeRoomFromOrder() {
        $bdata = $_POST['data']['bdata'];
        $roomid = $_POST['data']['roomid'];
        $reference = $_POST['data']['reference'];
        $this->getApi()->getHotelBookingManager()->removeRoomFromOrder($reference, $roomid, $bdata);
    }
    
    public function getEndDate() {
        if(isset($_POST['data']['enddate'])) {
            return strtotime($_POST['data']['enddate']);
        }
        return time()+86400;
    }
    
    public function getStartDate() {
        if(isset($_POST['data']['startdate'])) {
            return strtotime($_POST['data']['startdate']);
        }
        return time();
    }
    
    public function changeRoom() {
        $reference = $_POST['data']['referenceid'];
        $newRoomId = $_POST['data']['roomId'];
        $oldRoom = $_POST['data']['oldroom'];
        
        $rooms = $this->getApi()->getHotelBookingManager()->getAllRooms();
        
        foreach($rooms as $room) {
            if($room->roomName == $newRoomId) {
                $newRoomId = $room->id;
                break;
            }
        }
        
        $this->getApi()->getHotelBookingManager()->moveRoomOnReference($reference, $oldRoom, $newRoomId);
    }
    
    public function updateContactInfo() {
        $roomid = $_POST['data']['roomid'];
        $bookinguserinfo = $_POST['data']['bookinguserinfo'];
        $referenceid = $_POST['data']['referenceid'];
        $visitor = $_POST['data']['visitor'];
        
        $toupdate = new \core_hotelbookingmanager_Visitors();
        $toupdate->email = $visitor['email'];
        $toupdate->phone = $visitor['phone'];
        $toupdate->name = $visitor['name'];
        
        $visitors = array();
        $visitors[] = $toupdate;
        
        $this->getApi()->getHotelBookingManager()->updateBookingInformation($visitors, $bookinguserinfo, $roomid, $referenceid);
    }
    
    public function extendStay() {
        $data = $_POST['data'];
        $reference = $_POST['data']['reference'];
        $newdate = $_POST['data']['newdate'];
        $bdataid = $_POST['data']['bdataid'];
        
        echo $this->getApi()->getHotelBookingManager()->extendStay($reference, strtotime($newdate), $bdataid);
    }
    
    public function displayRoomBoxInfo() {
        echo "<span style='padding:10px; display:inline-block;'>";
        $selectedStart = strtotime($_POST['data']['startDate']);
        $selectedEnd = strtotime($_POST['data']['endDate']);
        $selectedRoomId = $_POST['data']['room'];
        $users = $this->getApi()->getUserManager()->getAllUsers();
        $allUsers = array();
        foreach($users as $user) {
            $allUsers[$user->id] = $user;
        }
        
        $reservations = $this->getApi()->getHotelBookingManager()->getAllReservations();
        $rooms = $this->getApi()->getHotelBookingManager()->getAllRooms();
        $allRooms = array();
        foreach($rooms as $room) {
            $allRooms[$room->id] = $room;
        }
        
        foreach($reservations as $reservation) {
            /* @var $reservation \core_hotelbookingmanager_BookingReference */
            $reservationStart = strtotime($reservation->startDate);
            $reservationStop = strtotime($reservation->endDate);
            
            /* @var $reservedRoom core_hotelbookingmanager_RoomInformation */
            $found = false;
            foreach($reservation->roomsReserved as $reservedRoom) {
                if($reservedRoom->roomId != $_POST['data']['room']) {
                    continue;
                }
                $found = true;
            }
            
            if(!$found) {
                continue;
            }
            
            if($selectedEnd < $reservationStart) {
                continue;
            }
            if($selectedStart > $reservationStop) {
                continue;
            }
            $this->printResevationRow($reservation, $allUsers, $allRooms, $selectedRoomId);
        }
        echo "</span>";
    }
    
    /**
     * @param core_hotelbookingmanager_BookingReference $reservation
     */
    function printResevationRow($reservation,$allUsers,$rooms, $selectedRoomId) {
            /* @var $reservation \core_hotelbookingmanager_BookingReference */
        echo "Booked for: " . $reservation->startDate . " - " . $reservation->endDate . "<br>";
        $bookedRooms = $reservation->roomsReserved;
        foreach($bookedRooms as $bookedRoom) {
            if($bookedRoom->roomId != $selectedRoomId) {
                continue;
            }
            $vistor = $bookedRoom->visitors[0];
            echo "&nbsp;&nbsp;&nbsp;&nbsp;" . $vistor->name . " - " . $vistor->phone . " - " . $vistor->email . " - <span style='color:#fff; cursor:pointer;' class='tempgrantaccess' roomId='".$bookedRoom->roomId."' refid='".$reservation->bookingReference."'>Temporary grant access</span><br>";
            echo "<br>";
        }
    }
    
    public function tempGrantAccess() {
        $refId = $_POST['data']['refId'];
        $room = $_POST['data']['room'];
        $this->getApi()->getHotelBookingManager()->tempGrantAccess($refId, $room);
    }
    
    public function updateBookingPrice() {
        $id = $_POST['data']['bookingid'];
        $price = $_POST['data']['price'];
        $price = str_replace(",", ".", $price);
        $bookingData = $this->getApi()->getHotelBookingManager()->getUserBookingData($id);
        $bookingData->bookingPrice = $price;
        $this->getApi()->getHotelBookingManager()->updateUserBookingData($bookingData);
    }
    
    public function showBookingInformation() {
        $this->includefile("userbookinginfo");
    }
    
    public function markAsPayed() {
        $id = $_POST['data']['id'];
        $this->getApi()->getHotelBookingManager()->markAsPayedForTest($id);
    }
    
    public function displayRoomAvailability() {
        $startDate = $this->getStartDate();
        $endDate = $this->getEndDate();
        $totalavialable = 0;
        if(isset($_POST['data'])) {
            $rooms = $this->getApi()->getHotelBookingManager()->getAllRooms();
            $roomsArray = array();
            foreach($rooms as $room) {
                $roomsArray[$room->id] = $room;
                $avilableclass = "notavailable";
                if($this->getApi()->getHotelBookingManager()->isRoomAvailable($room->id, $startDate, $endDate)) {
                    $avilableclass = "available";
                    $totalavialable++;
                }
                
                echo "<span class='roombox $avilableclass' room='$room->id'>";
                echo $room->roomName . "<br>";
                echo "</span>";
            }
        }
        echo "<div style='clear:both;'></div>";
        echo "Total number of available rooms: $totalavialable<br>";
        echo "<div style='overflow:auto; width: 1130px;'>";
        echo "<div style='width: 15000px;padding-top: 50px;'>";
        $stats = $this->getApi()->getHotelBookingManager()->getStatistics($startDate, $endDate, "");
        $dateprinted = false;
        $taken = array();
        foreach($stats as $roomId => $stat) {
            if(!$dateprinted) {
                $dateprinted = true;
                echo "<div style='padding-left: 20px;'>";
                foreach($stat->available as $day => $result) {
                    echo "<div class='datetext'>";
                    if(date('Y-m-d', $day) == date('Y-m-01', $day)) {
                        echo "<span style='border-left: solid 0px;height: 1px; width: 1px;float:left;'></span>";
                        echo "<b>";
                    }
                    echo date("d.m.Y", $day);
                    if(date('Y-m-d', $day) == date('Y-m-01', $day)) {
                        echo "</b>";
                    }
                    echo "</div>";
                }
                echo "</div>";
            }
            echo "<div style='clear:both;'>";
            echo "<span style='float:left; font-size: 10px;height: 10px; line-height: 10px;'>" . $roomsArray[$roomId]->roomName ."</span>";
            foreach($stat->available as $day => $result) {
                
                if(date('Y-m-d', $day) == date('Y-m-01', $day)) {
                    echo "<span style='border-left: solid 1px;height: 11px; float:left;'></span>";
                }
                if(!isset($taken[$day])) {
                    $taken[$day] = 0;
                }
                if(!isset($prices[$day])) {
                    $prices[$day] = 0;
                }
                
                if($result == 1) {
                    echo "<span style='border-left: solid 1px #266408;border-top: solid 1px #266408; width: 15px;height: 10px;background-color:green;float:left;'></span>";
                } else {
                    $taken[$day]++;
                    echo "<span style='border-left: solid 1px #266408;border-top: solid 1px #266408; width: 15px;height: 10px;background-color:#7e0505;float:left;'></span>";
                }
            }
            echo "</div>";
        }
        echo "</div>";
        echo "</div>";
        echo "<table>";
        echo "<tr>";
        echo "<td valign='top'>";
        $this->printBookingInformation($stats);
        echo "</td>";
        echo "<td valign='top' style='padding-left: 40px;'>";
        $this->printSalesInformation($stats);
        echo "</td>";
        echo "</tr>";
        echo "</table>";
        
    }
    
    public function displayHotelBookingOverview() {
//        echo "TEST";
//        $this->render();
    }
    
    public function saveArxData() {
        
        $settings = new \core_hotelbookingmanager_ArxSettings();
        $settings->address = $_POST['data']['arx_server'];
        $settings->username = $_POST['data']['arx_username'];
        $settings->password = $_POST['data']['arx_password'];
        $settings->smsFrom = $_POST['data']['arx_smsfrom'];
        $settings->smsWelcome = $_POST['data']['arx_smswelcome'];
        $settings->smsReady = $_POST['data']['arx_smsready'];
        $settings->smsReadyNO = $_POST['data']['arx_smsreadyNO'];
        $settings->smsWelcomeNO = $_POST['data']['arx_smswelcomeNO'];
        $settings->emailWelcome = $_POST['data']['arx_welcomeEmail'];
        $settings->emailWelcomeNO = $_POST['data']['arx_welcomeEmailNO'];
        $settings->emailWelcomeTitle = $_POST['data']['arx_welcomeemail_title'];
        $settings->emailWelcomeTitleNO = $_POST['data']['arx_welcomeemail_title_no'];
        $settings->extendStay = $_POST['data']['extend_stay'];
        $settings->extendStayNo = $_POST['data']['extend_stayNo'];
        
        $this->setConfigurationSetting("arx_server", $settings->address);
        $this->setConfigurationSetting("arx_username", $settings->username);
        $this->setConfigurationSetting("arx_password", $settings->password);
        $this->setConfigurationSetting("arx_smsfrom", $settings->smsFrom);
        $this->setConfigurationSetting("arx_smswelcome", $settings->smsWelcome);
        $this->setConfigurationSetting("arx_smsready", $settings->smsReady);
        $this->setConfigurationSetting("arx_smsreadyNO", $settings->smsReadyNO);
        $this->setConfigurationSetting("arx_smswelcomeNO", $settings->smsWelcomeNO);
        $this->setConfigurationSetting("arx_welcomeEmail", $settings->emailWelcome);
        $this->setConfigurationSetting("arx_welcomeEmailNO", $settings->emailWelcomeNO);
        $this->setConfigurationSetting("arx_welcomeemail_title", $settings->emailWelcomeTitle);
        $this->setConfigurationSetting("arx_welcomeemail_title_no", $settings->emailWelcomeTitleNO);
        $this->setConfigurationSetting("extend_stay", $settings->extendStay);
        $this->setConfigurationSetting("extend_stayNo", $settings->extendStayNo);
        
        $this->getApi()->getHotelBookingManager()->setArxConfiguration($settings);
    }
    
    public function saveVismaData() {
        
        $settings = new \core_hotelbookingmanager_VismaSettings();
        
        $settings->address = $_POST['data']['visma_server'];
        $settings->username = $_POST['data']['visma_username'];
        $settings->password = $_POST['data']['visma_password'];
        $settings->port = $_POST['data']['visma_port'];
        $settings->sqlUsername = $_POST['data']['visma_sqlUsername'];
        $settings->sqlPassword = $_POST['data']['visma_sqlPassword'];
        $settings->database = $_POST['data']['visma_db'];
        
        $this->setConfigurationSetting("visma_server", $settings->address);
        $this->setConfigurationSetting("visma_username", $settings->username);
        $this->setConfigurationSetting("visma_password", $settings->password);
        $this->setConfigurationSetting("visma_port", $settings->port);
        $this->setConfigurationSetting("visma_sqlUsername", $settings->sqlUsername);
        $this->setConfigurationSetting("visma_sqlPassword", $settings->sqlPassword);
        $this->setConfigurationSetting("visma_db", $settings->database);
        
        $this->getApi()->getHotelBookingManager()->setVismaConfiguration($settings);
    }
    
    public function updateAdminFee() {
        $fee = $_POST['data']['fee'];
        $reservation = $_POST['data']['ref'];
        echo $fee;
        $reservation = $this->getApi()->getHotelBookingManager()->getReservationByReferenceId($reservation);
        $reservation->bookingFee = $fee;
        $this->getApi()->getHotelBookingManager()->updateReservation($reservation);
    }
    
    public function saveContractData() {
        $this->setConfigurationSetting("utleier_navn", $_POST['data']['utleier_navn']);
        $this->setConfigurationSetting("utleier_adresse", $_POST['data']['utleier_adresse']);
        $this->setConfigurationSetting("utleier_postnr", $_POST['data']['utleier_postnr']);
        $this->setConfigurationSetting("utleier_sted", $_POST['data']['utleier_sted']);
    }
    
    public function addType() {
        $type = new \core_hotelbookingmanager_RoomType();
        $type->id = $_POST['data']['id'];
        $type->name = $_POST['data']['name'];
        $type->description_en = $_POST['data']['no_desc'];
        $type->description_no = $_POST['data']['no_desc'];
        $this->getApi()->getHotelBookingManager()->saveRoomType($type);
    }
    
    public function removeBookingReference() {
        $id = $_POST['data']['id'];
        $this->getApi()->getHotelBookingManager()->deleteUserBookingData($id);
    }
       
    public function activateBooking() {
        $id = $_POST['data']['bookingid'];
        $bdata = $this->getApi()->getHotelBookingManager()->getUserBookingData($id);
        $bdata->active = "true";
        $this->getApi()->getHotelBookingManager()->updateUserBookingData($bdata);
    }
    
    public function deleteType() {
        $id = $_POST['data']['typeId'];
        $this->getApi()->getHotelBookingManager()->removeRoomType($id);
    }
    function startsWith($haystack, $needle) {
        $length = strlen($needle);
        return (substr($haystack, 0, $length) === $needle);
    }
    function addRoom() {
         $room = new \core_hotelbookingmanager_Room();
         if(isset($_POST['data']['newroom'])) {
             $room->roomName = $_POST['data']['roomname'];
             $this->getApi()->getHotelBookingManager()->saveRoom($room);
         } else {
            foreach($_POST['data'] as $id => $val) {
                if($this->startsWith($id, "id_")) {
                   $id = str_replace("id_", "", $id);
                   $room = $this->getApi()->getHotelBookingManager()->getRoom($id);

                   $room->productId = $_POST['data']['productid_'.$id];
                   $room->roomName = $_POST['data']['roomname_'.$id];
                   $room->lockId = $_POST['data']['lockid_'.$id];
                   $room->isHandicap = $_POST['data']['handicap_'.$id];
                   $room->suitedForLongTerm = $_POST['data']['longterm_'.$id];

                   if($_POST['data']['available_'.$id] == "false") {
                       $room->isActive = "false";
                   } else {
                       $room->isActive = "true";
                   }
                   $this->getApi()->getHotelBookingManager()->saveRoom($room);
                }
            }
         }
    }
    
    public function getName() {
        return "HotelbookingManagement";
    }

    public function loadArxLog() {
        $this->includefile("arxlog");
    }
    
    public function postProcess() {
        
    }

    
    public function reprint() {
        
    }
    
    
    public function preProcess() {
        
    }
    
    public function showStopReference() {
        $this->includefile("stopreference");
    }
    
    public function stopReference() {
        $id = $_POST['data']['bookingid'];
        $bdata = $this->getApi()->getHotelBookingManager()->getUserBookingData($id);
        $bdata->active = "false";
        $this->getApi()->getHotelBookingManager()->updateUserBookingData($bdata);
    }
    
    public function getStarted() {
    }

    public function render() {
        $this->includefile("HotelbookingManagement");
    }

    public function deleteRoom() {
        $id = $_POST['data']['roomid'];
        $this->getApi()->getHotelBookingManager()->removeRoom($id);
    }
    
    public function loadMoveRoom() {
        $this->includefile("moveroom");
    }
    
    public function moveRoom() {
        $refid = $_POST['data']['refid'];
        $rooms = $_POST['data']['rooms'];
        
        $reference = $this->getApi()->getHotelBookingManager()->getReservationByReferenceId($refid);
        $reference->startDate = date("M d, Y h:m:s A", strtotime($_POST['data']['startdate']));
        $reference->endDate = date("M d, Y h:m:s A", strtotime($_POST['data']['enddate']));
        $this->getApi()->getHotelBookingManager()->updateReservation($reference);

        foreach($rooms as $id => $room) {
            $this->getApi()->getHotelBookingManager()->moveRoomOnReference($refid, $id, $room);
        }
    }
    
    public function getStoppedReferenceDate($referenceId) {
        $res = $this->getApi()->getHotelBookingManager()->getReservationByReferenceId($referenceId);
        if ($res) {
            $time = strtotime($res->endDate);
            return date("d.m.Y", $time);
        }
        
        return "";
    }
    
    /**
     * 
     * @param \core_hotelbookingmanager_Room $room
     * @param \core_hotelbookingmanager_RoomType[] $types
     */
    public function printRoomRow($room, $products) {
        
        $falseselected = "";
        $unavclass = "";
        if(!$room->isActive) {
            $falseselected = "SELECTED";
            $unavclass = "unavailable";
        }
        
        
        echo "<tr class='existingroomrow ".$unavclass."'>";
        echo "<td width='10'>";
        echo "<input type='hidden' value='". $room->id."' gsname='id_".$room->id."'>";
        echo "<i class='fa fa-trash-o' roomid='".$room->id."'></i></td>";
        echo "<td width='10'>";
        echo "<select gsname='productid_".$room->id."'>";
        echo "<option value=''>Connect to product</option>";
        foreach($products as $product) {
            /* @var $product \core_productmanager_data_Product */
            $selected = "";
            if($room->productId == $product->id) {
                $selected = "SELECTED";
            }
            
            echo "<option value='".$product->id."' $selected>" . $product->name . "</option>";
        }
        echo "</select>";
        echo "</td>";
        echo "<td>";
        echo "<select gsname='available_".$room->id."'>";
        echo "<option value='true'>". $this->__f("Available") . "</option>";
        echo "<option value='false' $falseselected>". $this->__f("Not available") . "</option>";
        echo "</select>";
        echo "</td>";
        echo "<td>";
        if($room->lastReservation) {
            echo $room->lastReservation->contact->names[0];
        }
        echo "</td>";
        echo "<td><input gsname='roomname_".$room->id."' value='" . $room->roomName . "'></td>";
        
        
        echo "<td align='right'>";
        $cleaned = "";
        $longterm = "";
        $handicap = "";
        
        if($room->isClean) {
            $cleaned = "CHECKED";
        }
        
        if($room->suitedForLongTerm) {
            $longterm = "CHECKED";
        }
        
        if($room->isHandicap) {
            $handicap = "CHECKED";
        }
        
        echo "<input type='checkbox' style='display:inline; width:12px; font-size:14px;' disabled title='Clean' $cleaned> ";
        echo "<input type='checkbox' style='display:inline; width:12px;' gsname='longterm_".$room->id."' $longterm title='Long term'> ";
        echo "<input type='checkbox' style='display:inline; width:12px;' gsname='handicap_".$room->id."' $handicap title='Handicap'>";
        echo "</td>";
        echo "</tr>";
    }

    public function printBookingInformation($stats) {
        $prices = array();
        $taken = array();
        
        $budget = array();
        $budget['04-2015'] = 0;
        $budget['05-2015'] = 10;
        $budget['06-2015'] = 40;
        $budget['07-2015'] = 50;
        
        $rooms = sizeof($this->getApi()->getHotelBookingManager()->getAllRooms());

        foreach($stats as $roomId => $stat) {
            foreach($stat->rentalPrice as $day => $result) {
                if(!isset($prices[$day])) {
                    $prices[$day] = 0;
                }
                $prices[$day] += $result;
            }
            foreach($stat->available as $day => $result) {
                if(!isset($taken[$day])) {
                    $taken[$day] = 0;
                }
                if(!$result) {
                    $taken[$day]++;
                }
            }
        }
        echo "<br><br><h1>Belegg kalender.</h1><br><br>";
        
        $matrix = array();
        foreach($prices as $day => $result) {
            $row = array();
            $row[] = round($result,2);
            if($taken[$day] > 0) {
                $row[] = round($result/$taken[$day],2);
            } else {
                $row[] = 0;
            }
            
            $row[] = $taken[$day];
            $row[] = ($rooms-$taken[$day]);
            
            $matrix[$day] = $row;
        }
        
        $roomsToEstimate = $rooms;
        if($_POST['data']['periode'] == "weekly") {
            $roomsToEstimate *= 7;
        } else if($_POST['data']['periode'] == "monthly") {
            $roomsToEstimate *= $number = cal_days_in_month(CAL_GREGORIAN, date("m", $day), date("Y", $day));
        }
        
        echo "<table cellspacing='1' cellpadding='1' style='background-color:#efefef;' class='takentable'>";
        echo "<tr style='background-color:#efefef;'>";
        echo "<th align='left'>Dato</th>";
        echo "<th align='left'>Total</th>";
        echo "<th align='left'>Snitt pris</th>";
        echo "<th align='left'>Rom leid ut</th>";
        echo "<th align='left'>Ledige rom</th>";
        echo "<th align='left'>Budsjett</th>";
        echo "<th align='left'>Belegg</th>";
        echo "</tr>";
        $matrix = $this->convertMatrixToPeriode($matrix);
        
        foreach($matrix as $day => $row) {
            echo "<tr>";
            echo "<td>" . date("d.m.Y", $day) . "</td>";
            foreach($row as $field) {
                echo "<td style='background-color:#fff;'>" . $field . "</td>";
            }
            
            if(!isset($budget[date("m-Y", $day)])) {
                $budgetMnth = 70;
            } else {
                $budgetMnth = $budget[date("m-Y", $day)];
            }
            echo "<td>$budgetMnth%</td>";
            $current = round(($row[2]/$roomsToEstimate)*100,1);
            $style = "background-color:green; color:#fff;";
            if($current < $budgetMnth) {
                $style = "background-color:red; color:#fff;";
            }
            
            
            echo "<td style='text-align:center;$style'>$current%</td>";
            
            echo "</tr>";
        }
        
        echo "</table>";
        echo "<style>.takentable td { padding: 5px; padding-top: 2px; padding-bottom: 2px; } </style>";
    }

    public function printSalesInformation() {
        
        echo "<br><br><h1>Sale.</h1><br><br>";

        echo "<table cellspacing='1' cellpadding='1' style='background-color:#efefef;' class='takentable'>";
        echo "<tr style='background-color:#efefef;'>";
        echo "<th align='left'>Date</th>";
        echo "<th align='left'>Total</th>";
        echo "<th align='left'>Credit card</th>";
        echo "<th align='left'>Invoice</th>";
        echo "<th align='left'>Nights</th>";
        echo "<th align='left'>Avg. price</th>";
        echo "<th align='left'>Orders</th>";
        echo "<th align='left'>Avg. order</th>";
        echo "</tr>";
        
        $salesInvoice = $this->getApi()->getOrderManager()->getSalesStatistics($this->getStartDate(), $this->getEndDate(), "ns_70ace3f0_3981_11e3_aa6e_0800200c9a66\InvoicePayment");
        $salesNetAxcept = $this->getApi()->getOrderManager()->getSalesStatistics($this->getStartDate(), $this->getEndDate(), "ns_def1e922_972f_4557_a315_a751a9b9eff1\Netaxept");
        
        $matrix = array();
        foreach($salesInvoice as $day => $sold) {
            $row = array();
            $invoice = $salesInvoice->{$day}->totalAmount;
            $creditcard = $salesNetAxcept->{$day}->totalAmount;
            $total = $invoice + $creditcard;
            
            $row[] = $total;
            $row[] = $creditcard;
            $row[] = $invoice;
            
            $totalCount = $salesInvoice->{$day}->totalCount;
            $totalCount += $salesNetAxcept->{$day}->totalCount;
            $row[] = $totalCount;
            if($totalCount > 0) {
                $row[] = round($total / $totalCount, 2); 
            } else {
                $row[] = 0; 
            }
            $orders = $salesInvoice->{$day}->numberOfOrders;
            $orders += $salesNetAxcept->{$day}->numberOfOrders;
            $row[] = $orders;
            if($orders > 0) {
                $row[] = round($total / $orders, 2);
            } else {
                $row[] = 0;
            }
            
            $matrix[$day] = $row;
        }
        $matrix = $this->convertMatrixToPeriode($matrix);
        foreach($matrix as $day => $row) {
            echo "<tr>";
            echo "<td>" . date("d.m.Y", $day) . "</td>";
            foreach($row as $field) {
                echo "<td style='background-color:#fff;'>" . $field . "</td>";
            }
            echo "</tr>";
        }
            
        echo "</table>";
    }

    public function convertMatrixToPeriode($matrix) {
        $periode = $_POST['data']['periode'];
        if($periode == "daily") {
            return $matrix;
        }
        
        $keys = array_keys($matrix);
        if($periode == "weekly") {
            $newMatrix = array();
            foreach($matrix as $day => $row) {
                foreach($row as $index => $field) {
                    $newMatrixIndex = strtotime('last monday', $day);
                    if(!isset($newMatrix[$newMatrixIndex])) {
                        $newMatrix[$newMatrixIndex] = array();
                    }
                    if(!isset($newMatrix[$newMatrixIndex][$index])) {
                        $newMatrix[$newMatrixIndex][$index] = 0;
                    }
                    $newMatrix[$newMatrixIndex][$index] += $field;
                }
            }
            $matrix = $newMatrix;
        }
        
        if($periode == "monthly") {
            $newMatrix = array();
            foreach($matrix as $day => $row) {
                foreach($row as $index => $field) {
                    $newMatrixIndex = strtotime('first day of this month', $day);
                    if(!isset($newMatrix[$newMatrixIndex])) {
                        $newMatrix[$newMatrixIndex] = array();
                    }
                    if(!isset($newMatrix[$newMatrixIndex][$index])) {
                        $newMatrix[$newMatrixIndex][$index] = 0;
                    }
                    $newMatrix[$newMatrixIndex][$index] += $field;
                }
            }
            $matrix = $newMatrix;
        }
        
        return $matrix;
    }

}
?>
