<?php
namespace ns_932810f4_5bd1_4b56_a259_d5bd2e071be1;

class AccountingTransfer extends \WebshopApplication implements \Application {
    public function getDescription() {
        return $this->__w("Export to accounting systems.");
    }

    public function getName() {
        return "AccountingTransfer";
    }

    public function render() {
        
    }
    
    public function renderConfig() {
        $this->includefile("accountingconfig");
    }
    
    public function transferNewFiles() {
        $this->getApi()->getAccountingManager()->transferFilesToAccounting();
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
        $config->port = $_POST['port'];
        $config->useActiveMode = $_POST['useActiveMode'];
        
        $config->creditor_username = $_POST['creditor_ftpuser'];
        $config->creditor_password = $_POST['creditor_ftppassword'];
        $config->creditor_hostname = $_POST['creditor_ftphostname'];
        $config->creditor_path = $_POST['creditor_ftppath'];
        $config->creditor_useSftp = $_POST['creditor_useSftp'];
        $config->creditor_port = $_POST['creditor_port'];
        $config->creditor_useActiveMode = $_POST['creditor_useActiveMode'];
        
        $config->extension = $_POST['extension'];
        $this->getApi()->getAccountingManager()->setAccountingManagerConfig($config);
    }
}
?>
