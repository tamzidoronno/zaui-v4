<?php
namespace ns_13270e94_258d_408d_b9a1_0ed3bbb1f6c9;

class OrderExport extends \WebshopApplication implements \Application {
    public function getDescription() {
        return "Replaces the old accountingtransfer application. This is more flexible";
    }

    public function getTransferTypes() {
        $transfertypes = array();
        $transfertypes['creditor'] = "Creditor";
        $transfertypes['bookingcomratemanager'] = "Booking.com ratemanager";
        return $transfertypes;
    }

    public function getName() {
        return "OrderExport";
    }

    public function renderConfig() {
        $this->includefile("orderexportconfig");
    }
    
    public function removeTransferConfig() {
        $this->getApi()->getAccountingManager()->removeTransferConfig($_POST['data']['configid']);
    }
    
    public function saveAccountingConfig() {
        $ftpConfig = new \core_accountingmanager_TransferFtpConfig();
        $ftpConfig->username = $_POST['ftpuser'];
        $ftpConfig->password = $_POST['ftppassword'];
        $ftpConfig->hostname = $_POST['ftphostname'];
        $ftpConfig->path = $_POST['ftppath'];
        $ftpConfig->useSftp = $_POST['useSftp'];
        $ftpConfig->port = $_POST['port'];
        $ftpConfig->useActiveMode = $_POST['useActiveMode'];
        $ftpConfig->extension = $_POST['extension'];
        
        
        $configs = $this->getApi()->getAccountingManager()->getAllConfigs();
        foreach($configs as $conf) {
            if($conf->id == $_POST['configid']) {
                $conf->ftp = $ftpConfig;
                $conf->delay = $_POST['delay'];
                $conf->includeUsers = $_POST['includeUsers'];
                $this->getApi()->getAccountingManager()->saveConfig($conf);
                break;
            }
        }
    }
    
    public function addPaymentTypeToConfig() {
        $configs = $this->getApi()->getAccountingManager()->getAllConfigs();
        foreach($configs as $conf) {
            if($conf->id == $_POST['data']['configid']) {
                $type = new \core_accountingmanager_AccountingTransferConfigTypes();
                $type->paymentType = $_POST['data']['paymentmethod'];
                $type->status = $_POST['data']['paymentstatus'];
                $conf->paymentTypes[] = $type;
                print_r($conf->paymentTypes);
                $this->getApi()->getAccountingManager()->saveConfig($conf);
                break;
            }
        }
    }
    
    public function removePaymentTypeToConfig() {
        $configs = $this->getApi()->getAccountingManager()->getAllConfigs();
        foreach($configs as $conf) {
            $toRemove = null;
            if($conf->id == $_POST['data']['configid']) {
                $newArray = array();
                foreach($conf->paymentTypes as $key => $val) {
                    if($val->paymentType == $_POST['data']['type'] && $val->status == $_POST['data']['status']) {
                        $toRemove = $key;
                    } else {
                        $newArray[] = $val;
                    }
                }
                $conf->paymentTypes = $newArray;
                print_r($newArray);
                $this->getApi()->getAccountingManager()->saveConfig($conf);
            }
        }
    }
    
    public function render() {
        
    }
    
    public function addNewConfig() {
        $config = new \core_accountingmanager_AccountingTransferConfig();
        $config->transferType = $_POST['data']['newtype'];
        $this->getApi()->getAccountingManager()->saveConfig($config);
    }
}
?>
