<?php
class FieldGenerator {
    /* @var $factory Factory */
    var $factory;
    var $name; 
    var $id;
    var $saveMethod;
    var $rules;
    var $validation;
    
    public function FieldGenerator() {
        
    }
    
    /**
     * 
     * @param type $rules
     * @param Factory $factory
     * @param type $name
     * @param type $id
     * @param type $saveMethod
     */
    public function setData($rules, $factory, $name, $id, $saveMethod) {
        $this->factory = $factory;
        $this->name = $name;
        $this->rules = $rules;
        $this->id = $id;
        $this->saveMethod = $saveMethod;
    }
    
    /**
     * @param core_pmsmanager_PmsRegistrationRules $rules
     */
    public function load() {
        $width = array();
        $width[] = 100;
        $width[] = 50;
        $width[] = 33;
        $width[] = 25;
        
        if(!$this->rules) {
            $this->rules = $this->factory->getApi()->getPmsManager()->initBookingRules($this->name);
        }
        
        $userFields = $this->getUserFields();
        echo "<div gstype='form' method='$this->saveMethod'>";
        echo "<input type='hidden' value='$this->id' gsname='itemid'>";
        echo "<br><h1>Guest information</h1>";
        $checked = "";
        if($this->rules->includeGuestData) {
            $checked = "CHECKED";
        }
        echo "<div>";
        echo "<input type='checkbox' gsname='displayGuestData' $checked style='float:right;'>";
        echo "Display guest information";
        echo "</div>";
        echo "<br>";
        $checked = "";
        if($this->rules->displayContactsList) {
            $checked = "CHECKED";
        }
        echo "<div>";
        echo "<input type='checkbox' gsname='displayContactsList' $checked style='float:right;'>";
        echo "Display contacts list";
        echo "</div>";
        
        echo "<br><h1>User / billing data</h1><br>";
        $this->printUsersFieldsSetup($userFields, $this->rules, $width);
        
        echo "<br><h1>Additional fields</h1>";
        $this->printAdditionalFields($this->rules);
        
        $fields = $this->factory->getApi()->getStoreManager()->getMultiLevelNames();
        
        echo "<br><h2>Who should this form be saved to</h2>";
        foreach($fields as $field => $name) {
            $checked = "";
            if($name == $this->name) {
                $checked = "CHECKED";
            }
            echo "<input type='checkbox' gsname='bookingengine_" . $name . "' $checked> $name<br>";
        }
        echo "<input type='button' gstype='submit' value='Save configuration' gsname='saveConfiguration'><br><br>";
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
        $fields["user_username"] = "Username";
        $fields["user_password"] = "Password";
        $fields["user_repeatpassword"] = "Repeat password";
        $fields["user_fullName"] = "Name of the user";
        $fields["user_prefix"] = "Phone prefix";
        $fields["user_cellPhone"] = "Cell phone number";
        $fields["user_emailAddress"] = "Email address";
        $fields["user_relationship"] = "Relationship";
        
        $fields["title_2"] = "Users address details";
        $fields["user_birthday"] = "Birth day of user";
        $fields["user_address_address"] = "Users street address";
        $fields["user_address_postCode"] = "Users postal code";
        $fields["user_address_city"] = "Users city";
        $fields["user_address_countrycode"] = "Users country code";
        $fields["user_address_countryname"] = "Name of the country";
        
        $fields["title_3"] = "<br>Company description details";
        $fields["company_name"] = "Name of the company";
        $fields["company_vatNumber"] = "Company organisation number";
        $fields["company_website"] = "Companys website";
        $fields["company_contact"] = "Contact person";
        $fields["company_prefix"] = "Phone prefix";
        $fields["company_phone"] = "Company phone number";
        $fields["company_email"] = "Company email";
        $fields["company_vatRegistered"] = "Is registered into the mva register";
        
        $fields["title_4"] = "Company address details";
        $fields["company_address_address"] = "Users street address";
        $fields["company_address_postCode"] = "Users postal code";
        $fields["company_address_city"] = "Users city";
        $fields["company_address_countrycode"] = "Users country code";
        $fields["company_address_countryname"] = "Name of the country";
        
        $fields["title_5"] = "Company invoice details";
        $fields["company_invoiceAddress_address"] = "Street address for invoice";
        $fields["company_invoiceAddress_postCode"] = "Postal code for sending invoice to";
        $fields["company_invoiceAddress_city"] = "City for invoice";
        $fields["company_invoiceAddress_countrycode"] = "Country code for invoice";
        $fields["company_invoiceAddress_countryname"] = "Country name for invoice";
        $fields["company_emailAddressToInvoice"] = "Email invoice address";
        $fields["company_invoicenote"] = "Invoice note to booking";

        return $fields;
    }

    public function createBookingRules($rules = null) {
        if($rules == null) {
            $rules = new \core_bookingengine_data_RegistrationRules();
            $rules->data = new \stdClass();
        }
        
        foreach($_POST['data'] as $key => $value) {
            $field = explode(";", $key);
            if(sizeof($field) != 3) {
                continue;
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
            if(isset($rules->data->{$field})) {
                $formData = $rules->data->{$field};
            }
            $formData->dependsOnCondition = $data['dependency'];
            $formData->title = $data['title'];
            $formData->required = $data['required'] == "true";
            $formData->active = $data['active'] == "true";
            $formData->type = $data['type'];
            $formData->name = $data['fieldname'];
            $formData->width = $data['width'];
            $formData->visible = $data['visible'] == "true";
            $rules->data->{$field} = $formData;
        }
        
        foreach($result['user'] as $field => $data) {
            $formData = new \core_bookingengine_data_RegistrationRulesField();
            if(isset($rules->data->{$field})) {
                $formData = $rules->data->{$field};
            }
            $formData->title = $data['name'];
            $formData->active = $data['active'] == "true";
            $formData->required = $data['required'] == "true";
            $formData->type = $data['type'];
            $formData->name = $field;
            $formData->width = $data['width'];
            $formData->visible = $data['visible'] == "true";
            $rules->data->{$field} = $formData;
        }
        
        $rules->displayContactsList = $_POST['data']['displayContactsList'] == "true";
        $rules->includeGuestData = $_POST['data']['displayGuestData'] == "true";
        
        return $rules;
    }
    
    /**
     * @param core_bookingengine_data_RegistrationRulesField $rules
     */
    public function printAdditionalFields($rules) {
        $types = array();
        $types['text']="Input field";
        $types['number']="Number";
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
            $visible = "";
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
                if($fieldData->visible)
                    $visible = "CHECKED";
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
            echo "<input type='checkbox' title='Visible' gsname='additional;field_$i;visible' $visible>";
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

    public function printUsersFieldsSetup($userFields, $rules, $width) {
        foreach($userFields as $field => $text) {

            $fieldData = new core_bookingengine_data_RegistrationRulesField();
            $active = "";
            $required = "";
            $visible = "";
            $title = $text;
            if(isset($rules->data->{$field})) {
                $fieldData = $rules->data->{$field};
                $title = $fieldData->title;
                if($fieldData->active)
                    $active = "CHECKED";
                if($fieldData->required)
                    $required = "CHECKED";
                if($fieldData->visible)
                    $visible = "CHECKED";
            }
            
            
            if(stristr($field, "title_")) {
                echo "<br><h2>" . $text . "</h2>";
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
                echo "<input type='checkbox' title='Visible' gsname='user;".$field.";visible' $visible>";
                echo "</span>";
                echo $field . "<span class='description' style='color:#777; display:block;padding:5px;'>" . $text . "</span>";
            }
        }
    }

    
    public function validatePostedForm() {
        if($this->factory->isEditorMode()) {
            return;
        }

        $this->validation = array();
        //First validate user data.
        
        foreach($this->rules->data as $key => $requirements) {
            $this->validateField($key, $requirements);
        }
        
        if(isset($_POST['data']['agreetoterms']) && $_POST['data']['agreetoterms'] != "true") {
            $this->validation['agreetoterms'] = $this->factory->__w("You need to agree to the terms and conditions");
        }
        
        if(isset($_POST['data']['user_username'])) {
            if(!$this->factory->getApi()->getUserManager()->checkIfFieldOnUserIsOkey("username", $_POST['data']['user_username'])) {
                $this->validation['user_username'] = $this->factory->__w("Username already taken");
            }
        }
        
        if(isset($_POST['data']['user_cellPhone'])) {
            if(!$this->factory->getApi()->getUserManager()->checkIfFieldOnUserIsOkey("cellPhone", $_POST['data']['user_cellPhone'])) {
                $this->validation['user_cellPhone'] = $this->factory->__w("User with this cell phone already exists");
            }
        }
        
        if(isset($_POST['data']['user_emailAddress'])) {
            if(!$this->factory->getApi()->getUserManager()->checkIfFieldOnUserIsOkey("emailAddress", $_POST['data']['user_emailAddress'])) {
                $this->validation['user_emailAddress'] = $this->factory->__w("User with this email already exists.");
            }
        }
        
        if(isset($_POST['data']['user_password']) && isset($_POST['data']['user_repeatpassword'])) {
            if($_POST['data']['user_password'] != $_POST['data']['user_repeatpassword']) {
                $this->validation['user_password'] = $this->factory->__w("Password does not match confirmed password.");
            }
        }

        return $this->validation;
    }

    /**
     * @param type $key
     * @param \core_bookingengine_data_RegistrationRulesField $requirements
     */
    public function validateField($key, $requirements) {
        if(!$requirements->active) {
            return;
        }
        
        if(isset($_POST['data']['choosetyperadio'])) {
            if($_POST['data']['choosetyperadio'] == "registration_private" && stristr($key, "company_")) {
                return;
            }
            if($_POST['data']['choosetyperadio'] == "registration_company" && stristr($key, "user_")) {
                return;
            }
        }
        
        if(isset($_POST['data'][$requirements->name])) {
            $res = $_POST['data'][$requirements->name];

            if($requirements->required && !$res) {
                $this->validation[$requirements->name] = $this->factory->__w("Field is required");
            }
        }
        
        $config = $this->rules;
        if($config->includeGuestData) {
            $this->validateGuestData();
        }
    }

    public function createUserObject() {
        $user = new core_usermanager_data_User();
        $user->address = new core_usermanager_data_Address();
        foreach($_POST['data'] as $key => $val) {
            if(substr($key,0, 5) != "user_") {
                continue;
            }
            $key = substr($key, 5);
            if(substr($key,0, 8) == "address_") {
                $addrkey = substr($key, 8);
                $user->address->{$addrkey} = $val;
            } else {
                if(property_exists($user, $key) === TRUE) {
                    $user->{$key} = $val;
                }
            }
        }
        return $user;
    }

}
?>
