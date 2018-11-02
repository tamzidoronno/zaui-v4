<?php
namespace ns_c3f32e06_365b_4c7f_995a_79012b9ea0fe;

class AccountingGeneralSettings extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "AccountingGeneralSettings";
    }

    public function StoreApplicationPool_getActivatedPaymentApplications() {
        $this->includefile("paymentapplication");
    }
    
    public function resetConnectoin() {
        $this->getApi()->getPaymentManager()->resetAllAccountingConfigurationForUsersAndOrders($_POST['data']['prompt']);
    }
    
    public function render() {
        $attributes = array(
            array('id', 'gs_hidden', 'id'),
            array('appName', 'Name', 'appName'),
            array('accoutUserId', 'Account id', 'accountUserId')            
        );
        
        $data = $this->getApi()->getStoreApplicationPool()->getActivatedPaymentApplications();
        foreach($data as $app) {
            $conf = $this->getApi()->getPaymentManager()->getStorePaymentConfiguration($app->id);
            $app->accountUserId = $conf ? $conf->userCustomerNumber : "Not set";
        }
        
        $args = array(null);
        $table = new \GetShopModuleTable($this, 'StoreApplicationPool', 'getActivatedPaymentApplications', $args, $attributes);
        $table->setData($data);
        $table->render();
        $this->includefile("extra");
    }
    
    public function savePaymentMethod() {
        $config = new \core_paymentmanager_StorePaymentConfig();
        $config->paymentAppId = $_POST['data']['id'];
        $config->userCustomerNumber = $_POST['data']['customernumber'];
        $config->accountingDescription = $_POST['data']['accountingDescription'];
        $this->getApi()->getPaymentManager()->saveStorePaymentConfiguration($config);
    }
    
    public function saveGeneral() {
        $config = $this->getApi()->getPaymentManager()->getGeneralPaymentConfig();
        $config->accountingCustomerOffset = $_POST['data']['customeroffset'];
        $config->postingDate = $_POST['data']['postingdate'];
        $config->accountingerOrderIdPrefix = $_POST['data']['orderprefix'];
        $this->getApi()->getPaymentManager()->saveGeneralPaymentConfig($config);
    }
}
?>
