<?php
namespace ns_e005f6e5_221c_4b02_9b65_327a63a0af3b;

class AccountingAccounts extends \SystemApplication implements \Application {
    public $accounts;
    public $errorMessages = array();
    
    public function getDescription() {
        
    }

    public function getName() {
        return "AccountingAccounts";
    }

    public function render() {
        $this->includefile("accounts");
    }
    
    public function validateAccounts() {
        $errorMessages = array();
        
        $accounts = json_decode($_SESSION['accountstoimport'], true);
        if(sizeof((array)$accounts) == 0) {
            $errorMessages[] = "Could not find any accounts to upload";
        }
        
        $taxgroups = $this->getApi()->getProductManager()->getTaxes();
        
        $line = 1;
        foreach($accounts as $account) {
            if($account[0] != (int)$account[0]) {
                $errorMessages[] = $account[0] . " is not a number at line $line";
            }
            
            if(!isset($account[1]) || !$account[1]) {
                $errorMessages[] = $account[0] . " is not a number at line $line";
            }
            
            $found = false;
            foreach($taxgroups as $grp) {
                if(isset($account[2])) {
                    if($grp->taxRate == $account[2]) {
                        $found = true;
                    }
                }
            }
            
            if(!$found) {
                if(isset($account[2])) {
                    $errorMessages[] = "Could not find tax group for tax : " . $account[0] . " at line $line, tax: " . $account[2];
                }
            }
            $line++;
        }
        
        return $errorMessages;
    }
    
    public function completeImport() {
        $errors = $this->validateAccounts();
        if(sizeof($errors) > 0) {
            return;
        }

        
        $accounts = json_decode($_SESSION['accountstoimport']);
        $taxgroups = $this->getApi()->getProductManager()->getTaxes();
        
        foreach($accounts as $account) {
            $detail = new \core_productmanager_data_AccountingDetail();
            $detail->accountNumber = $account[0];
            $detail->description = $account[1];
            foreach($taxgroups as $grp) {
                if($grp->taxRate == $account[2]) {
                    $detail->getShopTaxGroup = $grp->groupNumber;
                }
            }

            $this->getApi()->getProductManager()->saveAccountingDetail($detail);
        }
        
    }
    
    public function saveAccomodationAccount() {
        $this->getApi()->getProductManager()->setAccomodationAccount($_POST['data']['accountid']);
    }
    
    public function saveTaxGroups() {
        $taxes = $this->getApi()->getProductManager()->getTaxes();        
        foreach($taxes as $tax) {
            $tax->description = $_POST['data'][$tax->id."_description"];
            $tax->taxRate = $_POST['data'][$tax->id."_rate"];
            $tax->accountingTaxGroupId = $_POST['data'][$tax->id."_accountingTaxGroupId"];
        }
        $this->getApi()->getProductManager()->setTaxes($taxes);
        $this->getApi()->getProductManager()->doubleCheckAndCorrectAccounts();
    }
    
    public function uploadAccounts() {
        $accounts = $_POST['data']['accountsdata'];
        $rows = explode("\n", $accounts);
        $accounts = array();
        foreach($rows as $row) {
            $accountrow = explode(";", $row);
            if(isset($accountrow[0]) && isset($accountrow[1]) && isset($accountrow[2])) {
                $accounts[] = $accountrow;
            }
        }
        $_SESSION['accountstoimport'] = json_encode($accounts);
        $this->accounts = $accounts;
        $this->errorMessages = $this->validateAccounts();
    }
    
    public function createAccountingAccoutn() {
        $detail = new \core_productmanager_data_AccountingDetail();
        $detail->accountNumber = $_POST['data']['accountnumber'];
        $detail->description = $_POST['data']['accountdescription'];
        $detail->type = $_POST['data']['type'];
        $detail->getShopTaxGroup = $_POST['data']['taxgroup'];
        
        $gstaxgroup = $this->getApi()->getProductManager()->getTaxes();
        foreach($gstaxgroup as $grp) {
            if($grp->groupNumber == $detail->getShopTaxGroup) {
                $detail->taxgroup = $grp->accountingTaxGroupId;
            }
        }
        
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
                $accountDetail->getShopTaxGroup = $_POST['data']['gstaxcode_'.$exp[1]];
                $accountDetail->subaccountid = $_POST['data']['subaccountid_'.$exp[1]];
                $accountDetail->subaccountvalue = $_POST['data']['subaccountvalue_'.$exp[1]];
                $this->getApi()->getProductManager()->saveAccountingDetail($accountDetail);
            }
        }
        $this->getApi()->getProductManager()->doubleCheckAndCorrectAccounts();
    }
    
    public function deleteAccount() {
        $this->getApi()->getProductManager()->deleteAccountingAccount($_POST['data']['accountid']);
    }
    
    public function changeAccountingNumber() {
        $this->getApi()->getProductManager()->changeAccountingNumber($_POST['data']['oldAccountNumber'], $_POST['data']['accountNumber']);
    }
}
?>
