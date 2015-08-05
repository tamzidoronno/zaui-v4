<?php
namespace ns_4a5ee780_32f8_11e5_a2cb_0800200c9a66;

class FormItem extends \MarketingApplication implements \Application {
    
    public function getDescription() {
        return "Create forms in whatever way you want. Combine multiple instances of this application to create your own customized forms.";
    }

    public function getName() {
        return "Form Item";
    }

    public function loadConfig() {
        $this->includefile("configuration");
    }
    
    public function render() {
        $this->includefile("field");
    }
        
    public function addScripts() {
    }
    
    
    public function renderConfig() {
        $taxes = $this->getApi()->getProductManager()->getTaxes();

        if (sizeof($taxes) > 0) {
            $taxgroups = array();
            foreach ($taxes as $tax) {
                $taxgroups[$tax->groupNumber] = $tax;
            }

            $setting = new \core_common_Settings();
            $setting->id = "shipmentent";
            $setting->value = $taxgroups[0]->taxRate;
            @$this->configuration->settings->{"shipmentent"} = $setting;
            
            foreach($taxgroups as $number => $taxobj) {
                if($number == 0) {
                    continue;
                }
                $setting = new \core_common_Settings();
                $setting->id = "tax_group_". $number;
                $setting->value = $taxgroups[$number]->taxRate;
                $this->configuration->settings->{"tax_group_". $number} = $setting;
            }
        }

        $this->includefile("formconfig");
    }

    public function getSecret() {
        return $this->getApi()->getStoreManager()->getKeySecure("secret", "fdsafasfneo445gfsbsdfasfasf");
    }
    
    public function getCaptchaKey() {
        return $this->getApi()->getStoreManager()->getKey("sitekey");
    }
    
    public function saveConfig() {
        $this->getApi()->getStoreManager()->saveKey("sitekey", $_POST['sitekey'], false);
        $this->getApi()->getStoreManager()->saveKey("secret", $_POST['secret'], true);
    }

    public function submitForm() {
        $result = "<table width='600'>";
        foreach($_POST['data']['result'] as $index => $res) {
            $name = "";
            if(isset($res['name'])) {
                $name = $res['name'];
            }
            $type = $res['type'];
            if($type == "textarea") {
                $result .= "<tr>";
                $result .= "<td colspan='2' valign='top'><b>" . $name . "</b><br>" . $res['val'] . "<br><br>";
                $result .= "</tr>";
            } elseif($type == "infofield") {
                $result .= "<tr>";
                $result .= "<td colspan='2' valign='top'><br><br>" . $res['val'] . "<br><br>";
                $result .= "</tr>";
            } elseif($type == "h1" || $type == "h2" || $type == "h3") {
                $result .= "<tr>";
                $result .= "<td width='50%' colspan='2' valign='top'><$type>" . $res['val'] . "</$type>";
                $result .= "</tr>";
            } else {
                $result .= "<tr>";
                $result .= "<td width='50%' valign='top'><b>" . $name . "</b></td>";
                $result .= "<td width='50%' valign='top'>" . $res['val'] . "<br><br></td>";
                $result .= "</tr>";
            }
        }
        $result .= "</table>";
        
        $email = $this->getFactory()->getStore()->configuration->emailAdress;
        
        $_SESSION['submittedForm'] = $result; 
        
        $this->getApi()->getMessageManager()->sendMail($email, $email, "Form submission", $result, "post@getshop.com", "post@getshop.com");
    }
    
    public function renderBottom() {
        echo "\n" . "<script src='https://www.google.com/recaptcha/api.js?onload=capthaLoader&render=explicit'></script>";
    }
    
    public function getSubmittedForm() {
        return $_SESSION['submittedForm'];
    }
    
    public function updateconfig() {
        $this->setConfigurationSetting("type", $_POST['data']['type']);
        $this->setConfigurationSetting("title", $_POST['data']['title']);
        $this->setConfigurationSetting("name", $_POST['data']['name']);
        $this->setConfigurationSetting("selected", $_POST['data']['selected']);
        $this->setConfigurationSetting("placeholder", $_POST['data']['placeholder']);
        $this->setConfigurationSetting("required", $_POST['data']['required']);
        
        for($i = 0; $i < 10; $i++) {
            $this->setConfigurationSetting("dropdown_$i", $_POST['data']['dropdown_'.$i]);
        }
    }

    public function getField($name) {
        $field = $this->getConfigurationSetting($name);
        if(!$field) {
            return "";
        }
        return $field;
    }

}