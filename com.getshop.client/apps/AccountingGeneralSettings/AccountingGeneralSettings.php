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
    
    public function readdTaxGroups() {
        $this->getApi()->getOrderManager()->readdTaxGroupToNullItems($_POST['data']['prompt']);
    }
    
    public function render() {
        echo "<h1>Payment method configuration</h1>";
        $this->includefile("simplepaymentconfiguration");
        
        echo "<span style='cursor:pointer;color:blue;' onclick='$(this).hide();$(\".advancepaymentconfig\").show();'>Show advance options</span>";
        echo "<div class='advancepaymentconfig'>";
        echo "<h1>Advance payment configuration setup</h1>";
        
        $attributes = array(
            array('id', 'gs_hidden', 'id'),
            array('appName', 'Name', 'appName'),
            array('accoutUserId', 'Account id', 'accountUserId'),
            array('description', 'Description', 'description'),
            array('customernumberpaid', 'Customernumber paid', 'customernumberpaid'),       
            array('offsetAccountingId_prepayment', 'Accrude prepaid', 'offsetAccountingId_prepayment'),         
            array('offsetAccountingId_accrude', 'Accrude postpaid', 'offsetAccountingId_accrude')
        );
        
        $data = $this->getApi()->getStoreApplicationPool()->getActivatedPaymentApplications();
        foreach($data as $app) {
            $config = $this->getApi()->getPaymentManager()->getStorePaymentConfiguration($app->id);
            $app->accountUserId = $config ? $config->userCustomerNumber : "Not set";
            $app->customernumberpaid = $config ? $config->userCustomerNumberPaid : "";
            $app->description  = $config && isset($config->accountingDescription) ? $config->accountingDescription : "";
            $app->offsetAccountingId_accrude  = $config && isset($config->offsetAccountingId_accrude) ? $config->offsetAccountingId_accrude : "";
            $app->offsetAccountingId_prepayment  = $config && isset($config->offsetAccountingId_prepayment) ? $config->offsetAccountingId_prepayment : "";
        }
        $args = array(null);
        $table = new \GetShopModuleTable($this, 'StoreApplicationPool', 'getActivatedPaymentApplications', $args, $attributes);
        $table->setData($data);
        $table->render();
        echo "Descriptions:<br>";
        echo "* NAME: the name of the payment method.<br>";
        echo "* ACCOUNT ID: The account / customer number you would like to transfer to.<br>";
        echo "* CUSTOMERNUMBER PAID: When payment has been completed, put the money into this account (usually used for invoices).<br>";
        echo "* ACCRUDE PREPAID: An interim / accrude / mellomfinansiering account when a customer has prepaid for their stay, usually : 2900.<br>";
        echo "* ACCRUDE POSTPAID: An interim / accrude / mellomfinansiering account when  customer pay after their stay, usually : 1530.<br>";
        echo "</div>";
        $this->includefile("extra");
    }
    
    public function savePaymentMethod() {
        $config = new \core_paymentmanager_StorePaymentConfig();
        $config->paymentAppId = $_POST['data']['id'];
        $config->userCustomerNumber = $_POST['data']['customernumber'];
        $config->userCustomerNumberPaid = $_POST['data']['customernumberpaid'];
        $config->accountingDescription = $_POST['data']['accountingDescription'];
        $config->offsetAccountingId_accrude = $_POST['data']['offsetAccountingId_accrude'];
        $config->offsetAccountingId_prepayment = $_POST['data']['offsetAccountingId_prepayment'];
        $this->getApi()->getPaymentManager()->saveStorePaymentConfiguration($config);
    }
    
    public function saveaccountingsettings() {
        $data = $this->getApi()->getStoreApplicationPool()->getActivatedPaymentApplications();
        foreach($data as $app) {
            $config = $this->getApi()->getPaymentManager()->getStorePaymentConfiguration($app->id);
            if(!$config) {
                $config = new \core_paymentmanager_StorePaymentConfig();
                $config->paymentAppId = $app->id;
            }
            if(isset($_POST['data']['description_'.$app->id])) {
                $config->accountingDescription = $_POST['data']['description_'.$app->id];
                $config->userCustomerNumber = $_POST['data']['account_'.$app->id];
                $this->getApi()->getPaymentManager()->saveStorePaymentConfiguration($config);
            }
        }
        
        $generalconfig = $this->getApi()->getPaymentManager()->getGeneralPaymentConfig();
        $generalconfig->interimPrePaidAccount = $_POST['data']['interimprepaid'];
        $generalconfig->interimPostPaidAccount = $_POST['data']['interimpostpaid'];
        $generalconfig->paidPostingAccount = $_POST['data']['paidaccount'];
        $generalconfig->agioAccount = $_POST['data']['agioaccount'];
        $generalconfig->dissAgioAccount = $_POST['data']['disagioaccount'];
        $generalconfig->conversionAccount = $_POST['data']['conversionAccount'];
        
        $this->getApi()->getPaymentManager()->saveGeneralPaymentConfig($generalconfig);
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
