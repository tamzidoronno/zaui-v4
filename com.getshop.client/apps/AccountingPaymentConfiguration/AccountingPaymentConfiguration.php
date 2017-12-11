<?php
namespace ns_95a508d1_e382_468f_a515_677e6adbc6e8;

class AccountingPaymentConfiguration extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "AccountingPaymentConfiguration";
    }

    public function render() {
        $this->renderTable();
    }

    public function StoreApplicationPool_getApplications() {
        $this->includefile("paymentsettings");
    }
    
    public function renderTable() {
        $paymentApps = array();
        $apps = $this->getApi()->getStoreApplicationPool()->getApplications();
        foreach ($apps as $app) {
            if ($app->type == "PaymentApplication") {
                $paymentApps[] = $app;
            }
        }
        
        $attributes = array(
            array('id', 'gs_hidden', 'id'),
            array('appName', 'Name', 'appName')
        );
        
        $args = array(null);
        $table = new \GetShopModuleTable($this, 'StoreApplicationPool', 'getApplications', $args, $attributes);
        $table->setData($paymentApps);
        $table->render();
    }
    
    public function saveSettings() {
        $paymentConfig = new \core_paymentmanager_PaymentConfiguration();
        $paymentConfig->id = $_POST['data']['id'];
        $paymentConfig->isAllowedToManuallyMarkAsPaid = $_POST['data']['isAllowedToManuallyMarkAsPaid'] == "true" ? true : "0";
        $paymentConfig->automaticallyCloseOrderWhenPaid = $_POST['data']['automaticallyCloseOrderWhenPaid'] == "true" ? true : "0";
        $paymentConfig->transferToAccountingBasedOnCreatedDate = $_POST['data']['transferToAccountingBasedOnCreatedDate'] == "true" ? true : "0";
        $paymentConfig->transferToAccountingBasedOnPaymentDate = $_POST['data']['transferToAccountingBasedOnPaymentDate'] == "true" ? true : "0";
        $paymentConfig->transferCreditNoteToAccountingBasedOnPaymentDate = $_POST['data']['transferCreditNoteToAccountingBasedOnPaymentDate'] == "true" ? true : "0";
        $paymentConfig->transferCreditNoteToAccountingBasedOnCreatedDate = $_POST['data']['transferCreditNoteToAccountingBasedOnCreatedDate'] == "true" ? true : "0";
        $this->getApi()->getPaymentManager()->savePaymentConfiguration($paymentConfig);
    }

}
?>
