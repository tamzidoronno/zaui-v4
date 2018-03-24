<?php
namespace ns_3f40fb50_ab55_47c3_abcb_72d161eb93ee;

class GetShopProductPresenter extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "GetShopProductPresenter";
    }

    public function render() {
        $this->includefile("products");
    }
    
    public function showDescription() {
        $this->wrapContentManager($_POST['data']['modules'], "Change me");
        die();
    }
    
    public function sendRequest() {
         if (!$_POST['data']['name']) {
            $obj = $this->getStdErrorObject(); // Get a default error message
            $obj->fields->errorMessage = "You have to enter a valid name"; // The message you wish to display in the gserrorfield
            $obj->gsfield->name = 1; // Will highlight the field that has gsname "hours"
            $this->doError($obj); // Code will stop here.    
        }
        
        if (!$_POST['data']['email']) {
            $obj = $this->getStdErrorObject(); // Get a default error message
            $obj->fields->errorMessage = "You have to enter a email"; // The message you wish to display in the gserrorfield
            $obj->gsfield->email = 1; // Will highlight the field that has gsname "hours"
            $this->doError($obj); // Code will stop here.    
        }
        
        if (!$_POST['data']['phonenumber']) {
            $obj = $this->getStdErrorObject(); // Get a default error message
            $obj->fields->errorMessage = "You have to enter a email"; // The message you wish to display in the gserrorfield
            $obj->gsfield->phonenumber = 1; // Will highlight the field that has gsname "hours"
            $this->doError($obj); // Code will stop here.    
        }
        
        $msg = "Request:";
        $msg .= "<br/>Name: ".$_POST['data']['name'];
        $msg .= "<br/>Email: ".$_POST['data']['email'];
        $msg .= "<br/>Phone: ".$_POST['data']['phonenumber'];
        $msg .= "<br/>Modules: ".$_POST['data']['modules'];
        $this->getApi()->getMessageManager()->sendMessageToStoreOwner($msg,  "Request from product selector");
        
    }
}
?>
