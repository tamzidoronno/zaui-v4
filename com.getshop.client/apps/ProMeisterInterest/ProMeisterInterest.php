<?php
namespace ns_de7403c4_6b8e_4fb5_8d8a_d9fdc14ce76d;

class ProMeisterInterest extends \ns_d5444395_4535_4854_9dc1_81b769f5a0c3\EventCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "ProMeisterInterest";
    }

    public function render() {
        if (isset($_POST['event']) && $_POST['event'] == "sendInterestMessage") {
            $this->wrapContentManager("success_intreset_message", "Thank you, we have registered your interest now");
        } else {
            $this->includefile("promeisterinterest");
        }
        
    }
    
    public function sendInterestMessage() {
        $type = $this->getApi()->getBookingEngine()->getBookingItemType($this->getBookingEngineName(), $_POST['data']['typeid']);
        $user = \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject();
        
        $company = $user->companyObject;
        
        $subject = "Intresseanmälen";
        $content = "<br/>Event: " . $type->name;
        $content .= "<br/>";
        if ($company) {
            $content .= "<br/><b>Company</b>";
            $content .= "<br/>Company name: ".@$company->name;
            $content .= "<br/>VatNumber: ".@$company->vatNumber;
            $content .= "<br/>Address: ".@$company->address->address;
            $content .= "<br/>Post code: ".@$company->address->postCode;
            $content .= "<br/>City: ".@$company->address->city;
        }
        
        $content .= "<br/>";
        $content .= "<br/>Deltagarens namn: ".$user->fullName;
        $content .= "<br/>Kundnummer: ".@$company->reference;
        
        $to = $this->getFactory()->getStoreConfiguration()->emailAdress;

        $this->getApi()->getMessageManager()->sendMail($to, "ProMeister", $subject, $content, "noreply@getshop.com", "GetShop");
    }
}
?>
