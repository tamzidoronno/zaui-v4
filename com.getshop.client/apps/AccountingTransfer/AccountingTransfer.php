<?php
namespace ns_932810f4_5bd1_4b56_a259_d5bd2e071be1;

class AccountingTransfer extends \WebshopApplication implements \Application {
    public function getDescription() {
        return $this->__w("Export to accounting systems.");
    }

    public function getName() {
        return "ExternalTransfer";
    }

    public function render() {
        
    }
    
    public function renderConfig() {
        $this->includefile("accountingconfig");
    }
    
    public function transferNewFiles() {
        $this->getApi()->getAccountingManager()->forceTransferFiles();
    }
    
    public function saveAccountingConfig() {
        $states = array();
        for($i = 1; $i <= 10; $i++) {
            if($_POST['state_'.$i] == "true") {
                $states[] = $i;
            }
        }
        
        $config = $this->getApi()->getAccountingManager()->getAccountingManagerConfig();
        $config->statesToInclude = $states;
        $config->username = $_POST['ftpuser'];
        $config->password = $_POST['ftppassword'];
        $config->hostname = $_POST['ftphostname'];
        $config->path = $_POST['ftppath'];
        $config->useSftp = $_POST['useSftp'];
        $config->invoice_path = $_POST['invoice_path'];
        $config->port = $_POST['port'];
        $config->useActiveMode = $_POST['useActiveMode'];
        $config->transferAllUsersConnectedToOrders = $_POST['transferAllUsersConnectedToOrders'];
        $config->extension = $_POST['extension'];
        
        $types = $this->getTransferTypes();
        
        foreach($types as $idx => $heading) {
            $ftpconfig = new \core_accountingmanager_TransferFtpConfig();
            $ftpconfig->username = $_POST[$idx.'_ftpuser'];
            $ftpconfig->password = $_POST[$idx.'_ftppassword'];
            $ftpconfig->hostname = $_POST[$idx.'_ftphostname'];
            $ftpconfig->path = $_POST[$idx.'_ftppath'];
            $ftpconfig->useSftp = $_POST[$idx.'_useSftp'];
            $ftpconfig->port = $_POST[$idx.'_port'];
            $ftpconfig->invoice_path = $_POST[$idx.'_invoice_path'];
            $ftpconfig->useActiveMode = $_POST[$idx.'_useActiveMode'];
            $ftpconfig->extension = $_POST[$idx.'_extension'];
            $ftpconfig->engineNames = $_POST[$idx.'_engineNames'];
            $config->configrations->{$idx} = $ftpconfig;
        }
        
        $this->getApi()->getAccountingManager()->setAccountingManagerConfig($config);
    }

    public function getTransferTypes() {
        $transfertypes = array();
        $transfertypes['creditor'] = "Creditor";
        $transfertypes['bookingcomratemanager'] = "Booking.com ratemanager";
        return $transfertypes;
    }

}
?>
