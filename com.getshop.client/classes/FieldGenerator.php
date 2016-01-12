<?php
class FieldGenerator {
    /* @var $factory Factory */
    var $factory;
    var $name; 
    
    /**
     * @param core_pmsmanager_PmsRegistrationRules $rules
     */
    public function load($rules, $factory, $name) {
        $this->factory = $factory;
        $this->name = $name;
        
        if(!$rules) {
            $rules = $factory->getApi()->getPmsManager()->initBookingRules($name);
        }
        
        $userFields = $this->getUserFields();
        echo "<div gstype='form' method='saveConfiguration'>";
        echo "<h1>Guest information</h1>";
        echo "Print guest information";
        echo "<h1>User / billing data</h1>";
        foreach($userFields as $field => $text) {
            if(stristr($field, "title_")) {
                echo "<h2>" . $text . "</h2>";
            } else {
                echo "<span class='whatever' style='float:right;'>";
                echo "<input type='text' title='Field name' gsname='".$field."' value='$text' style='margin-right:10px; width: 400px;'>";
                echo "<input type='checkbox' title='Active' gsname='".$field.";active'>";
                echo "<input type='checkbox' title='Required' gsname='".$field.";required'>";
                echo "</span>";
                echo $field . "<span class='description' style='color:#777; display:block;padding:5px;'>" . $text . "</span>";
            }
        }
        
        echo "<h1>Additional fields</h1>";
        $this->printAdditionalFields();
        echo "<input type='button' gstype='submit' value='Save configuration' gsname='saveConfiguration'>";
        echo "</div>";
    }
    
    private function getUserFields() {
                
        $fields = array();
        $fields["title_0"] = "Users information details";
        $fields["emailAddress"] = "Email address";
        $fields["prefix"] = "Phone prefix";
        $fields["cellPhone"] = "Cell phone number";
        $fields["birthDay"] = "Users birth date";
        
        $fields["title_2"] = "Users address details";
        $fields["address.postCode"] = "Users postal code";
        $fields["address.address"] = "Users street address";
        $fields["address.city"] = "Users city";
        $fields["address.countrycode"] = "Users country code";
        $fields["address.countryname"] = "Name of the country";
        
        $fields["title_3"] = "<hr>Company description details";
        $fields["company.email"] = "Company email";
        $fields["company.prefix"] = "Phone prefix";
        $fields["company.phone"] = "Company phone number";
        $fields["company.postnumber"] = "Company postal code";
        $fields["company.vatNumber"] = "Company organisation number";
        $fields["company.country"] = "Country company registered in";
        $fields["company.city"] = "City company registered in";
        $fields["company.name"] = "Name of the company";
        $fields["company.vatRegistered"] = "Is registered into the mva register";
        $fields["company.emailAddressToInvoice"] = "Email invoice address";
        
        $fields["title_4"] = "Company invoice details";
        $fields["company.address.postCode"] = "Users postal code";
        $fields["company.address.address"] = "Users street address";
        $fields["company.address.city"] = "Users city";
        $fields["company.address.countrycode"] = "Users country code";
        $fields["company.address.countryname"] = "Name of the country";
        
        $fields["title_5"] = "Company invoice details";
        $fields["company.invoiceAddress.postCode"] = "Postal code for sending invoice to";
        $fields["company.invoiceAddress.address"] = "Street address for invoice";
        $fields["company.invoiceAddress.city"] = "City for invoice";
        $fields["company.invoiceAddress.countrycode"] = "Country code for invoice";
        $fields["company.invoiceAddress.countryname"] = "Country name for invoice";

        return $fields;
    }

    public function printAdditionalFields() {
        
        $types = array();
        $types['text']="Input field";
        $types['select']="Dropdown";
        $types['radio']="Radio buttons";
        $types['checkbox']="Checkbox";
        
        ?>
        <div gstype="form" method="editFormFields">
            <input type="button" gstype="submitToInfoBox" type="button" value="Open field configuration">
        </div>
        <?php

        for($i = 1; $i <= 20; $i++) {
            echo "<div style='clear:both; margin-bottom: 10px;'><span style='display:inline-block; width: 20px;'>$i.</span>";
            echo "<span class='whatever' style='float:right;'>";
            echo "<select gsname='field_$i;type' style='margin-right:10px;'>";
            foreach($types as $idx => $type) {
                echo "<option value='$idx'>$type</option>";
            }
            echo "</select>";
            echo "<input gsname='field_$i;title' type='text' title='Title of the field' placeholder='Field title' style='margin-right:10px; width: 300px;'>";
            echo "<input type='checkbox' title='Active' gsname=';field_$i;active'>";
            echo "<input type='checkbox' title='Required' gsname='field_$i;required'>";
            echo "<input type='checkbox' title='Dependency' gsname='field_$i;dependcy'>";
            echo "</span>";
            echo "<input type='txt' value='' placeholder='Field name' gsname='field_$i;fieldname'>";
            echo "</div>";
        }
    }

}
?>
