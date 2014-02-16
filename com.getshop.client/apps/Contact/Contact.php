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
    
    public function renderConfig() {
        $this->includeFile("config");
    }
    
    public function isEditable() {
        if(isset($_SESSION['contact']['edit']) && $_SESSION['contact']['edit'] == $this->getConfiguration()->id) {
            return true;
        }
        return false;
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
    
    public function sendMessage() {
        $this->config = $this->getFactory()->getStoreConfiguration();
        
        $email = $_POST['data']['email'];
        $phone = $_POST['data']['phone'];
        $name = $_POST['data']['name'];
        
        $content = $this->__w("Name"). ": " . $name . "<br>";
        $content .= $this->__w("Phone").": " . $phone . "<br>";
        $content .= $this->__w("Email").": " . $email . "<br>";
        if (isset($_POST['data']['invoiceemail'])) {
            $content .= $this->__w("Invoice email").": " . $_POST['data']['invoiceemail'] . "<br>";
        }
        if ($this->isCompanyFieldActivated()) {
            $content .= "Company: " . $_POST['data']['company'] . "<br>";
        }
        
        if (isset($_POST['data']['groupname']) && $_POST['data']['groupname'] != "") {
            $content .= "Avdeling: " . $_POST['data']['groupname'] . "<br>";
        }
        
        if(!$email || !$phone || !$name || !$_POST['data']['content']) {
            echo "Required";
            return;
        }
        if(!stristr($email, "@")) {
            
            echo "email";
            return;
        }
        
        $content .= "<br><br>".$this->__w("Message").":<br>" . $_POST['data']['content'];
        $content = nl2br($content);
        
        $from = $email;
        $title = $this->__w("Message from a customer");

        $to = $this->getEmail();
        
        $this->getApi()->getMessageManager()->sendMail($to, "Webshop owner", $title, $content, $from, $name);
    }
    
}

?>
