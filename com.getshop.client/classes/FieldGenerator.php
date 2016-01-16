<?php
class FieldGenerator {
    /* @var $factory Factory */
    var $factory;
    var $name; 
    
    /**
     * @param core_pmsmanager_PmsRegistrationRules $rules
     */
    public function load($rules, $factory, $name, $id, $saveMethod) {
        $this->factory = $factory;
        $this->name = $name;
        
        $width = array();
        $width[] = 100;
        $width[] = 50;
        $width[] = 33;
        $width[] = 25;
        
        if(!$rules) {
            $rules = $factory->getApi()->getPmsManager()->initBookingRules($name);
        }
        
        $userFields = $this->getUserFields();
        echo "<div gstype='form' method='$saveMethod'>";
        echo "<input type='hidden' value='$id' gsname='itemid'>";
        echo "<h1>Guest information</h1>";
        echo "Print guest information";
        echo "<h1>User / billing data</h1>";
        foreach($userFields as $field => $text) {

            $fieldData = new core_bookingengine_data_RegistrationRulesField();
            $active = "";
            $required = "";
            $title = $text;
            if(isset($rules->data->{$field})) {
                $fieldData = $rules->data->{$field};
                $title = $fieldData->title;
                if($fieldData->active)
                    $active = "CHECKED";
                if($fieldData->required)
                    $required = "CHECKED";
            }
            
            
            if(stristr($field, "title_")) {
                echo "<h2>" . $text . "</h2>";
            } else {
                echo "<span class='whatever' style='float:right;'>";
                echo "<input type='text' title='Field name' gsname='user;".$field.";name' value='$title' style='margin-right:10px; width: 400px;'>";
                echo "<select gsname='user;".$field.";width'>";
                foreach($width as $w) {
                    $s = "";
                    if($w == $fieldData->width) {
                        $s = "selected";
                    }
                    echo "<option value='$w' $s>$w%</option>";
                }
                echo "</select>";
                echo "<input type='checkbox' title='Active' gsname='user;".$field.";active' $active>";
                echo "<input type='checkbox' title='Required' gsname='user;".$field.";required' $required>";
                echo "</span>";
                echo $field . "<span class='description' style='color:#777; display:block;padding:5px;'>" . $text . "</span>";
            }
        }
        
        echo "<h1>Additional fields</h1>";
        $this->printAdditionalFields($rules);
        echo "<input type='button' gstype='submit' value='Save configuration' gsname='saveConfiguration'>";
        echo "</div>";
    }
    
    private function getType($field) {
        $fields = array();
        $fields['cellPhone'] = "mobile";
        $fields['emailAddress'] = "email";
       
        if(isset($fields[$field])) {
            return $fields[$field];
        }
        
        return "text";
    }
    
    private function getWidth($field) {
        $fields = array();
        $fields['fullName'] = "50";
        $fields['cellPhone'] = "50";
        $fields['user_address_address'] = "33";
        $fields['user_address_postCode'] = "33";
        $fields['user_address_city'] = "33";
        
        if(isset($fields[$field])) {
            return $fields[$field];
        }
        
        return "100";
    }
    
    private function getUserFields() {
                
        $fields = array();
        $fields["title_0"] = "Users information details";
        $fields["fullName"] = "Name of the user";
        $fields["cellPhone"] = "Cell phone number";
        $fields["emailAddress"] = "Email address";
        $fields["prefix"] = "Phone prefix";
        
        $fields["title_2"] = "Users address details";
        $fields["user_birthday"] = "Birth day of user";
        $fields["user_address_address"] = "Users street address";
        $fields["user_address_postCode"] = "Users postal code";
        $fields["user_address_city"] = "Users city";
        $fields["user_address_countrycode"] = "Users country code";
        $fields["user_address_countryname"] = "Name of the country";
        
        $fields["title_3"] = "<hr>Company description details";
        $fields["company_vatNumber"] = "Company organisation number";
        $fields["company_name"] = "Name of the company";
        $fields["company_phone"] = "Company phone number";
        $fields["company_website"] = "Companys website";
        $fields["company_email"] = "Company email";
        $fields["company_contact"] = "Contact person";
        $fields["company_prefix"] = "Phone prefix";
        $fields["company_vatRegistered"] = "Is registered into the mva register";
        $fields["company_emailAddressToInvoice"] = "Email invoice address";
        
        $fields["title_4"] = "Company address details";
        $fields["company_address_address"] = "Users street address";
        $fields["company_address_postCode"] = "Users postal code";
        $fields["company_address_city"] = "Users city";
        $fields["company_address_countrycode"] = "Users country code";
        $fields["company_address_countryname"] = "Name of the country";
        
        $fields["title_5"] = "Company invoice details";
        $fields["company_invoiceAddress_postCode"] = "Postal code for sending invoice to";
        $fields["company_invoiceAddress_address"] = "Street address for invoice";
        $fields["company_invoiceAddress_city"] = "City for invoice";
        $fields["company_invoiceAddress_countrycode"] = "Country code for invoice";
        $fields["company_invoiceAddress_countryname"] = "Country name for invoice";

        return $fields;
    }

    public function createBookingRules() {
        $rules = new \core_bookingengine_data_RegistrationRules();
        $rules->data = new \stdClass();
        
        foreach($_POST['data'] as $key => $value) {
            $field = explode(";", $key);
            if(sizeof($field) != 3) {
                echo $key;
            }
            @$result[$field[0]][$field[1]][$field[2]] = $_POST['data'][$key];
        }
        
        $oldFields = $this->getUserFields();
        $usr = array();
        $add = array();

        foreach($oldFields as $key => $val) {
            if(isset($result['user'][$key])) {
                $usr[$key] = $result['user'][$key];
                $usr[$key]['type'] = $this->getType($key); 
            }
        }
        
        $result['user'] = $usr;
        
        foreach($result['additional'] as $field => $data) {
            $formData = new \core_bookingengine_data_RegistrationRulesField();
            $formData->dependsOnCondition = $data['dependency'];
            $formData->title = $data['title'];
            $formData->required = $data['required'] == "true";
            $formData->active = $data['active'] == "true";
            $formData->type = $data['type'];
            $formData->name = $data['fieldname'];
            $formData->width = $data['width'];
            $rules->data->{$field} = $formData;
        }
        
        foreach($result['user'] as $field => $data) {
            $formData = new \core_bookingengine_data_RegistrationRulesField();
            $formData->title = $data['name'];
            $formData->active = $data['active'] == "true";
            $formData->required = $data['required'] == "true";
            $formData->type = $data['type'];
            $formData->name = $field;
            $formData->width = $data['width'];
            $rules->data->{$field} = $formData;
        }
        
        return $rules;
    }
    
    /**
     * @param core_bookingengine_data_RegistrationRulesField $rules
     */
    public function printAdditionalFields($rules) {
        $types = array();
        $types['text']="Input field";
        $types['select']="Dropdown";
        $types['radio']="Radio buttons";
        $types['checkbox']="Checkbox";
        $types['textarea']="Text area";
        $types['title']="Title text";
        
        $width = array();
        $width[] = 100;
        $width[] = 50;
        $width[] = 33;
        $width[] = 25;

        $numberOfFields = 20;
        for($i = 1; $i <= $numberOfFields; $i++) {
            
            $fieldData = new core_bookingengine_data_RegistrationRulesField();
            $active = "";
            $required = "";
            $title = "";
            $name = "";
            $dep = -1;
            if(isset($rules->data->{"field_".$i})) {
                $fieldData = $rules->data->{"field_".$i};
                $title = $fieldData->title;
                $name = $fieldData->name;
                $dep = $fieldData->dependsOnCondition;
                if($fieldData->active)
                    $active = "CHECKED";
                if($fieldData->required)
                    $required = "CHECKED";
            }
            
            
            echo "<div style='clear:both; margin-bottom: 10px;'><span style='display:inline-block; width: 20px;'>$i.</span>";
            echo "<span class='whatever' style='float:right;'>";
            echo "<select gsname='additional;field_$i;type' style='margin-right:10px;'>";
            foreach($types as $idx => $type) {
                $selected = "";
                if($fieldData->type == $idx) {
                    $selected = "SELECTED";
                }
                echo "<option value='$idx' $selected>$type</option>";
            }
            echo "</select>";
            echo "<input gsname='additional;field_$i;title' type='text' title='Title of the field' placeholder='Field title' style='margin-right:10px; width: 300px;' value='$title'>";
            echo "<select gsname='additional;field_$i;width'>";
            foreach($width as $w) {
                $s = "";
                if($w == $fieldData->width) {
                    $s = "selected";
                }
                echo "<option value='$w' $s>$w%</option>";
              }
            echo "<input type='checkbox' title='Active' gsname='additional;field_$i;active' $active>";
            echo "<input type='checkbox' title='Required' gsname='additional;field_$i;required' $required>";
            echo "<select gsname='additional;field_$i;dependency'>";
            echo "<option value='-1'>No dependecy</option>";
            for($j = 0;$j < $numberOfFields; $j++) {
                $selected = "";
                if($j == $dep) {
                    $selected = "SELECTED";
                }
                echo "<option value='$j' $selected>Field $j</option>";
            }
            echo "</select>";
            echo "</span>";
            echo "<input type='txt' value='$name' placeholder='Field name' gsname='additional;field_$i;fieldname'>";
            echo "</div>";
        }
    }

}
?>
