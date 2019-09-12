<?php
namespace ns_e005f6e5_221c_4b02_9b65_327a63a0af3b;

class AccountingAccounts extends \SystemApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "AccountingAccounts";
    }

    public function render() {
        $this->includefile("accounts");
    }
    
    public function createAccountingAccoutn() {
        $detail = new \core_productmanager_data_AccountingDetail();
        $detail->accountNumber = $_POST['data']['accountnumber'];
        $detail->description = $_POST['data']['accountdescription'];
        
        $this->getApi()->getProductManager()->saveAccountingDetail($detail);
    }
    
    public function saveAccountingDescription() {
        foreach ($_POST['data'] as $key => $value) {
            $exp = explode("_", $key);
            if ($exp[0] == "description") {
                $accountDetail = new \core_productmanager_data_AccountingDetail();
                $accountDetail->accountNumber = $exp[1];
                $accountDetail->description = $value;
                $accountDetail->taxgroup = $_POST['data']['taxcode_'.$exp[1]];
                $accountDetail->subaccountid = $_POST['data']['subaccountid_'.$exp[1]];
                $accountDetail->subaccountvalue = $_POST['data']['subaccountvalue_'.$exp[1]];
                $this->getApi()->getProductManager()->saveAccountingDetail($accountDetail);
            }
        }
    }
    
    public function deleteAccount() {
        $this->getApi()->getProductManager()->deleteAccountingAccount($_POST['data']['accountid']);
    }
    
    public function changeAccountingNumber() {
        $this->getApi()->getProductManager()->changeAccountingNumber($_POST['data']['oldAccountNumber'], $_POST['data']['accountNumber']);
    }
}
?>
