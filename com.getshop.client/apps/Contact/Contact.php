<?php
namespace ns_96de3d91_41f2_4236_a469_cd1015b233fc;

class Contact extends \WebshopApplication implements \Application {
    public function getDescription() {
        return $this->__f("Add this contact form, configure it, and you are ready to start receiving questions and feedback from your users per email.");
    }
       
    public function getAvailablePositions() {
        return "middle";
    }
    
    public function getName() {
        return $this->__("Contact form");
    }
    
    public function postProcess() {
        
    }
    
    public function preProcess() {
    }
    
    public function getStarted() {
        echo $this->__f("Click the create application button and you will automatically get a form which sends you email directly to your email address when someone use this form.");
    }
    
    public function editContact() {
        $_SESSION['contact']['edit'] = $this->getConfiguration()->id;
    }
    
    public function saveContact() {
        unset($_SESSION['contact']['edit']);
        $email = $_POST['data']['email'];
        $phone = $_POST['data']['phone'];
        $postalCode = $_POST['data']['postalcode'];
        $address = $_POST['data']['address'];
        
        /* @var $config core_storemanager_data_StoreConfiguration */
        $config = $this->getFactory()->getStoreConfiguration();
        $config->postalCode = $postalCode;
        $config->emailAdress = $email;
        $config->streetAddress = $address;
        $config->phoneNumber = $phone;
        
        $this->getApi()->getStoreManager()->saveStore($config);
    }
    
    public function render() {
        if($this->isEditable()) {
            $this->includefile("EditContactTemplate");
        } else {
            $this->includefile("ContactTemplate");
        }
    }
    
    public function isEditable() {
        if(isset($_SESSION['contact']['edit']) && $_SESSION['contact']['edit'] == $this->getConfiguration()->id) {
            return true;
        }
        return false;
    }
    
    public function saveContactConfig() {
        $config = $_POST['data'];
        $this->setConfigurationSetting("emailConfig", json_encode($config));
    }

    public function getContactConfig() {
        $config = $this->getConfigurationSetting("emailConfig");
        if($config) {
            $config = json_decode($config, true);
        }
        return $config;
    }
    
    public function getBodyTitle() {
        $config = $this->getContactConfig();
        if(isset($config) && $config['bodyTitle']) {
            return $config['bodyTitle'];
        }
        
        return $this->__w("What's on your mind?");
    }
    
    public function getFields() {
        $fields = [];
        $fields['name'] = $this->__w("My name is:");
        $fields['phone'] = $this->__w("Phone");
        $fields['email'] = $this->__w('Email');
        
        $config = $this->getContactConfig();
        if(isset($config)) {
            $number = $config['numberOfFields'];
            if($number > 0) {
                $fields = array();
                for($i = 1; $i <= $number; $i++) {
                    $fields['field'.$i] = $config['fieldName_'.$i];
                }
            }
        }
        
        return $fields;
    }
    
    
    public function getPostalCode() {
        if(isset($this->getFactory()->getStoreConfiguration()->postalCode)) {
            return $this->getFactory()->getStoreConfiguration()->postalCode;
        }
        return "";
    }

    public function getAddress() {
        if(isset($this->getFactory()->getStoreConfiguration()->streetAddress)) {
            return $this->getFactory()->getStoreConfiguration()->streetAddress;
        }
        
        return "";
    }

    public function getEmail() {
        $config = $this->getContactConfig();
        if(isset($config) && $config['emailAddress']) {
            return $config['emailAddress'];
        }

        
        if(isset($this->getFactory()->getStoreConfiguration()->emailAdress)) {
            return $this->getFactory()->getStoreConfiguration()->emailAdress;
        }
        return "";
    }

    public function getPhone() {
        if(isset($this->getFactory()->getStoreConfiguration()->phoneNumber)) {
            return $this->getFactory()->getStoreConfiguration()->phoneNumber;
        }
        return "";
    }

    public function loadConfiguration() {
        $this->includefile("editform");
    }
    
    public function isCompanyFieldActivated() {
        $showCompany = $this->getConfigurationSetting("showcompany");
        return (isset($showCompany) && $showCompany == "true");
    }
    
    public function getHeader() {
        $content = @$this->getFactory()->getStoreConfiguration()->contactContent;
        if(!$content) {
            $content = "<b>Contact us</b><br>We live to serve you, if you have anything on your mind, please don't hesitate to contact us.";
        }
        return $content;
    }
    
    public function getSubject() {
        $config = $this->getContactConfig();
        if(isset($config) && isset($config['emailSubject'])) {
            return $config['emailSubject'];
        }
        
        return $this->__w("Message from a customer");
    }
    
    public function sendMessage() {
        $this->config = $this->getFactory()->getStoreConfiguration();
        $fields = $this->getFields();
        $content = "";
        foreach($_POST['data']['field'] as $field => $val) {
            if(!isset($fields[$field])) {
                continue;
            }
            $content .= $fields[$field].": " . $val . "<br>";
        }
        
        $content .= "<br><br>".$this->__w("Message").":<br>" . $_POST['data']['field']['content'];
        $content = nl2br($content);
        
        $title = $this->getSubject();

        $to = $this->getEmail();
        
        $this->getApi()->getMessageManager()->sendMail($to, "Webshop owner", $title, $content, "noreply@gethsop.com", "GetShop Backend");
    }
    
}

?>
