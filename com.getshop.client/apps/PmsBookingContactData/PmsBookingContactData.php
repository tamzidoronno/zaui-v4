<?php
namespace ns_d3951fc4_6929_4230_a275_f2a7314f97c1;

class PmsBookingContactData extends \WebshopApplication implements \Application {
    var $validation;
    var $bookingCompleted = false;
    var $counter = 0;
    
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
            $this->includefile("completedbooking");
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
        $booking = $this->getApi()->getPmsManager()->getCurrentBooking($this->getSelectedName());
        $defaultRule = null;
        foreach($booking->rooms as $room) {
            if($room->item && $room->item->rules) {
                return $room->item->rules;
            }
            if($room->type && $room->type->rules) {
                return $room->type->rules;
            }
        }
        
        return $this->getApi()->getBookingEngine()->getDefaultRegistrationRules($this->getSelectedName());
    }

    /**
     * @param \core_bookingengine_data_RegistrationRulesField $value
     */
    public function printField($value) {
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
            
            if ($value->type == "text" || $value->type == "mobile" || $value->type == "email") {
                 $this->printTitle($value->title, $value->required, $value->type);
               echo "<input type='text' gsname='".$value->name."'>";
            } else if($value->type == "radio") {
                echo "<input type='radio' gsname='".$value->name."' name='".$value->name."'>";
                $this->printTitle($value->title, $value->required, $value->type);
            } else if($value->type == "title") {
                $this->printTitle($value->title, $value->required, $value->type);
            } else if($value->type == "textarea") {
                $this->printTitle($value->title, $value->required, $value->type);
                echo "<textarea gsname='".$value->name."'></textarea>";
            } else {
                echo "Type not set yet: " . " (" . $value->type . ")";
            }
            echo "</label>";
        }
    }

    public function printTitle($title, $required, $type) {
        echo "<span class='labeltitle'>";
        if ($required && $type != "radio") {
            echo "* ";
        }

        echo $title . "</span>";
    }

}
?>
