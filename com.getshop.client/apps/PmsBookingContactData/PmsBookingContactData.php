<?php
namespace ns_d3951fc4_6929_4230_a275_f2a7314f97c1;

class PmsBookingContactData extends \WebshopApplication implements \Application {
    var $validation;
    var $currentBooking = null;
    var $counter = 0;
    var $lastFieldPrintedType = "";
    var $selectPrint = "";
    
    public function getDescription() {
        return "Asks the user for to enter the nessesary contact data to complete the booking";
    }

    public function getName() {
        return "PmsBookingContactData";
    }

    public function addAlternativeOrganiasation() {
        $booking = $this->getCurrentBooking();
        $booking->alternativeOrginasation = $_POST['data']['orgid'];
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedName(), $booking);
    }
    
    public function render() {
        if(!$this->getSelectedName()) {
            echo "Please specify a booking engine first";
            return;
        }
        echo "<span class='registrationdatafieldarea'>";
        $this->includefile("roomcontactdata");
        echo "</span>";
    }
    
    public function saveSettings() {
        foreach($_POST['data'] as $key => $value) {
            $this->setConfigurationSetting($key, $value);
        }
    }
    
    
    public function getSelectedName() {
        return $this->getConfigurationSetting("booking_engine_name");
    }
    
    public function showSettings() {
        $this->includefile("settings");
    }

    public function getNameText() {
        if($this->getConfigurationSetting("name_text")) {
            return $this->getConfigurationSetting("name_text");
        }
        return $this->__w("Name");
    }

    public function getConfigurationToUse() {
        $booking = $this->getCurrentBooking();
        $defaultRule = null;
        foreach($booking->rooms as $room) {
            if(isset($room->item) && $room->item && $room->item->rules) {
                return $room->item->rules;
            }
            if(isset($room->type) && $room->type && $room->type->rules) {
                return $room->type->rules;
            }
        }
        
        return $this->getApi()->getBookingEngine()->getDefaultRegistrationRules($this->getSelectedName());
    }
    

    public function changeUserToSubmitOn() {
        $booking = $this->getCurrentBooking();
        $booking->userId = $_POST['data']['userid'];
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedName(), $booking);
    }
    
    /**
     * @param \core_bookingengine_data_RegistrationRulesField $value
     */
    public function printField($value) {
        $currentlyAdded = $this->getCurrentBooking();
        $curAddedFields = $currentlyAdded->registrationData->resultAdded;
        
        $valueSet = "";
        if(isset($curAddedFields->{$value->name})) {
            $valueSet = $curAddedFields->{$value->name};
        }
        
        if($value->type == "select") {
            echo "<label class='col-".$value->width. " type_".$value->type."'>";
            if($this->selectPrint != $value->name) {
                echo "<select gsname='" . $value->name . "'>";
            }
            $this->selectPrint = $value->name;
            if($valueSet == $value->title) { $selected = "SELECTED"; } else { $selected = ""; }
            echo "<option value='".$value->title."' $selected>" . $value->title . "</option>";
            return;
        }
        
        
        if($this->selectPrint) {
            echo "</select></label>";
            $this->selectPrint = false;
            return;
        }
        
        if($this->lastFieldPrintedType == "radio" && $value->type != "radio") {
            echo "<div style='clear:both;'>&nbsp;</div>";
        } 
        $this->lastFieldPrintedType = $value->type;
        if ($value->active) {
            $lastone = "";
            if($value->width == 100) {
                $lastone = "lastone";
                $this->counter = 0;
            } else if($value->width == 50 && $this->counter == 1) {
                $lastone = "lastone";
                $this->counter = 0;
            } else if($value->width == 33 && $this->counter == 2) {
                $lastone = "lastone";
                $this->counter = 0;
            } else {
                $this->counter++;
            }
                
            echo "<label class='col-".$value->width. " " . $lastone . " type_".$value->type."'>";
            if (stristr($value->name, "prefix")) {
                $this->printTitle($value->title, $value->required, $value->type);
                $this->printPhoneCodes($value, $valueSet);
            } else if (stristr($value->name, "relationship")){
                $this->printTitle($value->title, $value->required, $value->type);
                $this->printRelationship($value);
            } else if (stristr($value->name, "company_vatRegistered")) {
                $this->printTitle($value->title, $value->required, $value->type);
                echo "<div class='formspacer'><input type='checkbox' gsname='".$value->name."'></div>";
            } else if ($value->type == "text" || $value->type == "number" || $value->type == "mobile" || $value->type == "email") {
               $this->printTitle($value->title, $value->required, $value->type);
               echo "<input type='text' gsname='".$value->name."' value='$valueSet'>";
            } else if($value->type == "radio") {
                echo "<input type='radio' gsname='".$value->name."' name='".$value->name."' value='".$value->title."'>";
                $this->printTitle($value->title, $value->required, $value->type);
            } else if($value->type == "title") {
                $this->printTitle($value->title, $value->required, $value->type);
            } else if($value->type == "textarea") {
                $this->printTitle($value->title, $value->required, $value->type);
                $maxChar = $this->getConfigurationSetting("maxCharTextArea");
                if(!$maxChar || $maxChar == "" || $maxChar <= 0){
                    $maxChar = -1;
                    echo "<textarea gsname='".$value->name."'>".$valueSet."</textarea>";
                }else{
                    echo "<textarea maxlength='".$maxChar."' gsname='".$value->name."'>".$valueSet."</textarea>";
                }
            } else {
                echo "Type not set yet: " . " (" . $value->type . ")";
            }
            echo "<span class='errordesc'>";
            if(isset($this->validation[$value->name])) {
                echo "* " . $this->validation[$value->name];
            } else {
                echo "&nbsp;";
            }
            echo "</span>";
            
            echo "</label>";
        }
    }
    
    public function submitForm() {
        unset($_SESSION['pmfilter'][$this->getSelectedName()]);
        $this->savePostedForm();
        $this->validatePostedForm();
        $isPikStore = $this->getApi()->getStoreManager()->isPikStore();
        $loaded = false;
        if(!sizeof($this->validation) > 0) {
            $bookingToSend = $this->getCurrentBooking();
            $this->currentBooking = $this->getApi()->getPmsManager()->completeCurrentBooking($this->getSelectedName());
            if($this->currentBooking) {
                $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedName());
                $curBooking = $this->currentBooking;
                $daysToIgnore = $config->ignorePaymentWindowDaysAheadOfStay;
                $ignorePaymentWindow = false;
                if($daysToIgnore > 0) {
                    $latestDay = null;
                    foreach($bookingToSend->rooms as $room) {
                        $latestDay = strtotime($room->date->start);
                    }
                    $daysBetween = 0;
                    if($latestDay == null || $latestDay > $startDayOnBooking) {
                        $daysBetween = floor(($latestDay-time()) / (60 * 60 * 24));
                    }
                    if($daysBetween > $daysToIgnore) {
                        $ignorePaymentWindow = true;
                    }
                    echo $daysBetween;
                }
                if(($bookingToSend->totalPrice == 0.0 || !$bookingToSend->totalPrice || $bookingToSend->totalPrice == 0) && $bookingToSend->priceType == 1) {
                    $ignorePaymentWindow = true;
                }
                 
                echo "<script>";
                if($config->payAfterBookingCompleted || $isPikStore) {
                    if(!isset($curBooking->orderIds[0]) || $ignorePaymentWindow) {
                        if(\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()) {
                            $nextPage = $this->getConfigurationSetting("nextPageId");
                            if(\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::isAdministrator() && $nextPage) {
                                echo 'thundashop.common.goToPageLink("/?page='.$nextPage. '");';
                            } else {
                                echo 'thundashop.common.goToPageLink("/?page=booking_completed_'.$this->getSelectedName() . '");';
                            }
                        } else {
                            if($ignorePaymentWindow) {
                                echo 'thundashop.common.goToPageLink("/?page=booking_completed_'.$this->getSelectedName() . '");';
                            } else {
                                echo 'thundashop.common.goToPage("payment_failed");';
                            }
                        }
                    } else {
                        echo 'thundashop.common.goToPageLink("?page=cart&payorder='.$curBooking->orderIds[0].'");';
                    }
                } else {
                    if($curBooking->confirmed) {
                            $nextPage = $this->getConfigurationSetting("nextPageId");
                            if(\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::isAdministrator() && $nextPage) {
                                echo 'thundashop.common.goToPageLink("/?page='.$nextPage. '");';
                            } else {
                                echo 'thundashop.common.goToPageLink("/?page=booking_completed_'.$this->getSelectedName().'_confirmed");';
                            }
                    } else {
                        $nextPage = $this->getConfigurationSetting("nextPageId");
                        if(\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::isAdministrator() && $nextPage) {
                            echo 'thundashop.common.goToPageLink("/?page='.$nextPage. '");';
                        } else {
                            echo 'thundashop.common.goToPageLink("/?page=booking_completed_'.$this->getSelectedName() . '");';
                        }
                    }
                }
                echo "</script>";
                $loaded = true;
            } else {
                $this->validation[] = "Someone booked your room before you where able to complete the booking process, please try again with a different room.";
                $this->unknownError = "Someone booked your room before you where able to complete the booking process, please try again with a different room.";
                if(sizeof($bookingToSend->rooms) > 0) {
                    $this->getApi()->getPmsManager()->warnFailedBooking($this->getSelectedName(), $bookingToSend);
                }
            }
        } 
        if($loaded) {
           echo "<div style='font-size:30px;text-align:center;'><i class='fa fa-spin fa-spinner'></i></div>"; 
        } else {
            $this->includefile("roomcontactdata");
        }
    }

    public function printTitle($title, $required, $type) {
        echo "<span class='labeltitle'>";
        if ($required && $type != "radio") {
            echo "* ";
        }

        echo $title . "</span>";
    }

    public function validatePostedForm() {
        $config = $this->getApi()->getPmsManager()->getConfiguration($this->getSelectedName());
        if($this->isEditorMode() && !$config->forceRequiredFieldsForEditors) {
            return;
        }
        
        $validation = array();
        //First validate user data.
        
        $originalForm = $this->getConfigurationToUse();
        
        foreach($originalForm->data as $key => $requirements) {
            $this->validateField($key, $requirements);
        }
        
        if(isset($_POST['data']['agreetoterms']) && $_POST['data']['agreetoterms'] != "true") {
            $this->validation['agreetoterms'] = $this->__w("You need to agree to the terms and conditions");
        }
        
        return true;
    }

    /**
     * @param type $key
     * @param \core_bookingengine_data_RegistrationRulesField $requirements
     */
    public function validateField($key, $requirements) {
        if(!$requirements->active) {
            return;
        }
        
        
        
        if(isset($_POST['data']['choosetyperadio']) && $_POST['data']['choosetyperadio'] == "registration_private" && stristr($key, "company_")) {
            return;
        }
        if(isset($_POST['data']['choosetyperadio']) && $_POST['data']['choosetyperadio'] == "registration_company" && stristr($key, "user_")) {
            return;
        }
        
        if(isset($_POST['data']['choosetyperadio']) && $_POST['data']['choosetyperadio'] == "registered_user") {
            return;
        }
        
        
        if(isset($_POST['data'][$requirements->name])) {
            $res = $_POST['data'][$requirements->name];

            if($key == "company_email" && !stristr($res, "@") && $res) {
                $this->validation[$requirements->name] = $this->__w("Not a valid email");
            }

            if($key == "user_emailAddress" && !stristr($res, "@")) {
                $this->validation[$requirements->name] = $this->__w("Not a valid email");
            }

            if($requirements->required && !$res) {
                $this->validation[$requirements->name] = $this->__w("Field is required");
            }
        }
        
        $config = $this->getConfigurationToUse();
        if($config->includeGuestData) {
            $this->validateGuestData();
        }
    }

    public function savePostedForm() {
        $originalForm = $this->getConfigurationToUse();
        foreach($_POST['data'] as $key => $val) {
            $originalForm->resultAdded->{$key} = $val;
        }
        
        $i = 0;
        $newList = array();
        
        $selected = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedName());
        foreach($selected->registrationData->contactsList as $contact) {
            $contact->name = $_POST['data']['contact_name_'.$i];
            $contact->phone = $_POST['data']['contact_phone_'.$i];
            $contact->email = $_POST['data']['contact_email_'.$i];
            $contact->title = $_POST['data']['contact_title_'.$i];
            $newList[] = $contact;
            $i++;
        }
        $originalForm->contactsList = $newList;
        $selected->registrationData = $originalForm;
        
        $config = $this->getConfigurationToUse();
        if($config->includeGuestData) {
            $i = 1;
            foreach($selected->rooms as $room) {
                $room->numberOfGuests = $_POST['data']['visitor_numberofguests_'.$i];
                $guest = new \core_pmsmanager_PmsGuests();
                $guest->name = $_POST['data']['visitor_name_'.$i];
                $guest->prefix = $_POST['data']['visitor_prefix_'.$i];
                $guest->email = $_POST['data']['visitor_email_'.$i];
                $guest->phone = $_POST['data']['visitor_phone_'.$i];
                $room->guests = array();
                $room->guests[] = $guest;
                $i++;
            }
        }
        
        foreach($selected->rooms as $room) {
            if(!$room->canBeAdded) {
                $room->canBeAdded = "false";
            }
        }
        if(isset($_POST['data']['submit']) && $_POST['data']['submit'] == "nopay" && $this->isEditorMode()) {
            $selected->avoidCreateInvoice = true;
        }
        if(isset($_POST['data']['invoicenote'])) {
            $selected->invoiceNote = $_POST['data']['invoicenote'];
        }
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedName(), $selected);
        $this->currentBooking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedName());
    }

    public function removeIndex() {
        $index = $_POST['data']['index'];
        $booking = $this->getCurrentBooking();
        unset($booking->registrationData->contactsList[$index]);
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedName(), $booking);
    }
    
    public function addContact() {
        $booking = $this->getCurrentBooking();
        $booking->registrationData->contactsList[] = new \core_bookingengine_data_Contacts();
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedName(), $booking);
    }
    
    /**
     * @return \core_pmsmanager_PmsBooking 
     */
    public function getCurrentBooking() {
        if(!$this->currentBooking) {
            $this->currentBooking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedName());
        }
        return $this->currentBooking;
    }

    public function validateGuestData() {
        if($this->isAdminMode()) {
            return;
        }
        
        $booking = $this->getCurrentBooking();
        $i = 1;
        foreach($booking->rooms as $room) {
            $guest = new \core_pmsmanager_PmsGuests();
            if(isset($room->guests[0])) {
                $guest = $room->guests[0];
            }
            if(!$guest->email) {
                $this->validation['visitor_email_' . $i] = $this->__w("Field is required");
            }
            if(!$guest->phone) {
                $this->validation['visitor_phone_' . $i] = $this->__w("Field is required");
            }
            if(!$guest->name) {
                $this->validation['visitor_name_' . $i] = $this->__w("Field is required");
            }
            $i++;
        }
    }

    public function getCodes() {
        $codes = [7840,93,355,213,1684,376,244,1264,1268,54,374,297,247,61,672,43,994,1242,973,880,1246,1268,375,32,501,229,1441,975,591,387,267,55,246,1284,673,359,226,257,855,237,1,238,345,236,235,56,86,61,61,57,269,242,243,682,506,385,53,599,537,420,45,246,253,1767,1809,670,56,593,20,503,240,291,372,251,500,298,679,358,33,596,594,689,241,220,995,49,233,350,30,299,1473,590,1671,502,224,245,595,509,504,852,36,354,91,62,98,964,353,972,39,225,1876,81,962,77,254,686,965,996,856,371,961,266,231,218,423,370,352,853,389,261,265,60,960,223,356,692,596,222,230,262,52,691,1808,373,377,976,382,1664,212,95,264,674,977,31,599,1869,687,64,505,227,234,683,672,850,1670,47,968,92,680,970,507,675,595,51,63,48,351,1787,974,262,40,7,250,685,378,966,221,381,248,232,65,421,386,677,27,500,82,34,94,249,597,268,46,41,963,886,992,255,66,670,228,690,676,1868,216,90,993,1649,688,1340,256,380,971,44,1,598,998,678,58,84,1808,681,967,260,255,263];
        sort($codes);
        return $codes;
    }

    public function printPhoneCodes($value, $valueSet) {
        if(!$valueSet) {
            $valueSet = "47";
        }
        echo "<select gsname='".$value->name."'>";
        foreach($this->getCodes() as $code) {
            $selected = "";
            if($valueSet == $code) {
                $selected = "SELECTED";
            }
            echo "<option value='$code' $selected>+$code</option>";
        }
        echo "</select>";
    }
    public function printRelationship($value){
        
        echo "<select gsname='".$value->name."'>";
        echo "<option value='Singel'>Singel</option>";
        echo "<option value='Samboer'>Samboer</option>";
        echo "<option value='Gift'>Gift</option>";
        echo "</select>";
    }
}
?>
