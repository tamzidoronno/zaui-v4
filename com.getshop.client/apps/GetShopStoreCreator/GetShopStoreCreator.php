<?php
namespace ns_6fc2b3f8_d6fa_4f70_9062_56179a701119;

class GetShopStoreCreator extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "GetShopStoreCreator";
    }

    public function render() {
        $this->includefile("create");
    }
    
    public function getTimeZones() {
        $appname = get_class($this);
        if (strpos($appname, "\\")) {
            $appname = substr($appname, 0, strpos($appname, "\\"));
        }
        $content = file_get_contents('../app/' . $appname ."/timezones.json");
        return json_decode($content);
    }
    
    public function getCountries() {
        $appname = get_class($this);
        if (strpos($appname, "\\")) {
            $appname = substr($appname, 0, strpos($appname, "\\"));
        }
        $content = file_get_contents('../app/' . $appname ."/countries.json");
        return json_decode($content);
    }
    
    public function create() {
        if (!$_POST['data']['name']) {
            $obj = $this->getStdErrorObject(); // Get a default error message
            $obj->fields->errorMessage = "Your name can not be blank"; // The message you wish to display in the gserrorfield
            $obj->gsfield->name = 1; // Will highlight the field that has gsname "hours"
            $this->doError($obj); // Code will stop here.
        }
        
        if (!$_POST['data']['email']) {
            $obj = $this->getStdErrorObject(); // Get a default error message
            $obj->fields->errorMessage = "Email can not be blank"; // The message you wish to display in the gserrorfield
            $obj->gsfield->email = 1; // Will highlight the field that has gsname "hours"
            $this->doError($obj); // Code will stop here.
        }
        
        if (!$_POST['data']['cellphone']) {
            $obj = $this->getStdErrorObject(); // Get a default error message
            $obj->fields->errorMessage = "Phonenumber can not be blank"; // The message you wish to display in the gserrorfield
            $obj->gsfield->cellphone = 1; // Will highlight the field that has gsname "hours"
            $this->doError($obj); // Code will stop here.
        }
        
        if (!$_POST['data']['prefix']) {
            $obj = $this->getStdErrorObject(); // Get a default error message
            $obj->fields->errorMessage = "Prefix can not be blank"; // The message you wish to display in the gserrorfield
            $obj->gsfield->prefix = 1; // Will highlight the field that has gsname "hours"
            $this->doError($obj); // Code will stop here.
        }
        
        $startData = new \core_getshop_data_StartData();
        $startData->email = $_POST['data']['email'];
        $startData->name = $_POST['data']['name'];
        $startData->cellphone = $_POST['data']['cellphone'];
        $startData->prefix = $_POST['data']['prefix'];
        $startData->emailText = $this->getConfigurationSetting("text");
        $startData->emailSubject = $this->getConfigurationSetting("subject");
        $this->getApi()->getGetShop()->createNewStore($startData);
    }
    
    public function saveSettings() {
        $this->setConfigurationSetting("subject", $_POST['data']['subject']);
        $this->setConfigurationSetting("text", $_POST['data']['text']);
        $this->setConfigurationSetting("thankyou", $_POST['data']['thankyou']);
    }
}
?>
