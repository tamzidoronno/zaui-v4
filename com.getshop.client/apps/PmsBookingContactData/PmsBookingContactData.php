<?php
namespace ns_d3951fc4_6929_4230_a275_f2a7314f97c1;

class PmsBookingContactData extends \WebshopApplication implements \Application {
    var $validation;
    var $bookingCompleted = false;
    var $currentBooking = null;
    var $counter = 0;
    var $lastFieldPrintedType = "";
    
    public function getDescription() {
        return "Asks the user for to enter the nessesary contact data to complete the booking";
    }

    public function getName() {
        return "PmsBookingContactData";
    }

    public function render() {
        if(!$this->getSelectedName()) {
            echo "Please specify a booking engine first";
            return;
        }
        if($this->bookingCompleted) {
            ?>
            <script>
                document.location.href="/completed_<? echo $this->getSelectedName(); ?>.html";
            </script>
            <?
        } else {
            $this->includefile("roomcontactdata");
        }
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

    /**
     * @param \core_bookingengine_data_RegistrationRulesField $value
     */
    public function printField($value) {
        $currentlyAdded = $this->getCurrentBooking();
        $curAddedFields = $currentlyAdded->registrationData->resultAdded;
        
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
            
            $valueSet = "";
            if(isset($curAddedFields->{$value->name})) {
                $valueSet = $curAddedFields->{$value->name};
            }
            
            echo "<label class='col-".$value->width. " " . $lastone . " type_".$value->type."'>";
            
            if ($value->type == "text" || $value->type == "mobile" || $value->type == "email") {
               $this->printTitle($value->title, $value->required, $value->type);
               echo "<input type='text' gsname='".$value->name."' value='$valueSet'>";
            } else if($value->type == "radio") {
                echo "<input type='radio' gsname='".$value->name."' name='".$value->name."'>";
                $this->printTitle($value->title, $value->required, $value->type);
            } else if($value->type == "title") {
                $this->printTitle($value->title, $value->required, $value->type);
            } else if($value->type == "textarea") {
                $this->printTitle($value->title, $value->required, $value->type);
                echo "<textarea gsname='".$value->name."'>".$valueSet."</textarea>";
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
        $this->savePostedForm();
        $this->validatePostedForm();
        if(!sizeof($this->validation) > 0) {
            $this->getApi()->getPmsManager()->completeCurrentBooking($this->getSelectedName());
            $this->bookingCompleted = true;
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
        $validation = array();
        //First validate user data.
        
        $originalForm = $this->getConfigurationToUse();
        
        foreach($originalForm->data as $key => $requirements) {
            $this->validateField($key, $requirements);
        }
        
        if(isset($_POST['data']['agreetoterms']) && $_POST['data']['agreetoterms'] != "true") {
            $this->validation['agreetoterms'] = "You need to agree to the terms and conditions";
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
        
        
        
        if($_POST['data']['choosetyperadio'] == "registration_private" && stristr($key, "company_")) {
            return;
        }
        if($_POST['data']['choosetyperadio'] == "registration_company" && stristr($key, "user_")) {
            return;
        }
        
        if(isset($_POST['data'][$requirements->name])) {
            $res = $_POST['data'][$requirements->name];

            if($requirements->required && !$res) {
                $this->validation[$requirements->name] = "Field is required";
            }
        }
    }

    public function savePostedForm() {
        $originalForm = $this->getConfigurationToUse();
        foreach($_POST['data'] as $key => $val) {
            $originalForm->resultAdded->{$key} = $val;
        }
        $selected = $this->getCurrentBooking();
        $selected->registrationData = $originalForm;
        $this->getApi()->getPmsManager()->setBooking($this->getSelectedName(), $selected);
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

}
?>
