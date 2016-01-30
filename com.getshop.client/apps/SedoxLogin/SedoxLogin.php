<?php
namespace ns_af4ca00c_4007_42c1_a895_254710311191;

class SedoxLogin extends \MarketingApplication implements \Application {

    public function getDescription() {
        
    }

    public function getName() {
        return "SedoxLogin";
    }
    
    public function saveApiKey() {
        $this->setConfigurationSetting("apikey", $_POST['data']['key'], true);
    }
    
    public function saveAddress() {
        $this->setConfigurationSetting("wsdladdress", $_POST['data']['address']);
    }

    public function render() {
        if (\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::isAdministrator()) {
            echo "<div style='font-size: 19px; text-align: center;'>Logged in</div>";
        }
        
        if (\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject() != null) {
            return;
        }
        
        $this->includefile("login");
    }
    
    public function login() {
        $emailAddress = @$_POST['data']['username'];
        $password = @$_POST['data']['password'];
        if ($emailAddress != null && $password != null) {
            $user = $this->getApi()->getSedoxProductManager()->login($emailAddress, $password);
            if ($user != null) {
                echo "success";
            }
            
            \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::setLoggedOn($user);
            
            return;
        }
        
        echo "failed";
    }

    public function createAccount() {
        $this->startAdminImpersonation("StoreApplicationInstancePool", "getApplicationInstance");
        $instance = $this->getApi()->getStoreApplicationInstancePool()->getApplicationInstance($this->getConfiguration()->id);
        $this->setConfiguration($instance);
        $this->stopImpersionation();
        
        $client = new \SoapClient($this->getConfigurationSetting("wsdladdress"));
        $session = $client->login('databank', $this->getConfigurationSetting("apikey"));
        try {
            $result = $client->customerCustomerCreate($session, 
                array(
                    'email' => $_POST['data']['email'], 
                    'firstname' => $_POST['data']['firstname'], 
                    'lastname' => $_POST['data']['lastname'], 
                    'password' => $_POST['data']['password'], 
                    'taxvat' => $_POST['data']['tax'], 
                    'website_id' => 1, 
                    'store_id' => 1, 
                    'group_id' => 1
                    )
                );
        } catch (\SoapFault $fault) {
            echo $fault->getMessage();
        }
    }
    
    public function requestAdminRights() {
        $this->requestAdminRight("StoreApplicationInstancePool", "getApplicationInstance", "It need access to store the apikey securly");
    }
    
}
?>