<?php
namespace ns_e6570c0a_8240_4971_be34_2e67f0253fd3;

class AccountFinanceReport extends \MarketingApplication implements \Application {
    private $paymentConfigs = array();
    public $extramessage = "";
    
    public function getDescription() {
        
    }

    public function getName() {
        return "AccountFinanceReport";
    }

    public function render() {
        $this->fetchConfigs();
        
        if (isset($_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_account_summary'])) {
            $this->includefile("accountsummary");
            return;
        }
        
        if (isset($_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_day_orderid'])) {
            $this->includefile("orderview");
            return;
        } 
        if (isset($_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_day_start']) && $_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_day_start']) {
            $this->includefile("dayoverview");
            return;
        } 
        
        echo "<div class='leftmenu'>";
            $this->includefile("leftmenu");
        echo "</div>";
        
        echo "<div class='workarea'>";
            $this->includefile($this->getTab());
        echo "</div>";
    }

    public function createGroupByAccount($dayIncome) {
        
    }
    
    public function logoutVisma() {
        unset($_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_VISMA_NET_OAUTHSESSION']);
    }
    
    public function downloadAccountingFile() {
        $system = $this->getApi()->getGetShopAccountingManager()->getCurrentSystemOther();
        if ($system == "GENERELL_NORWEGIAN") {
            $this->includefile("gbat10");
        }
    }
    
    public function showSummaryForAccount() {
        $_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_account_summary'] = $_POST['data']['account'];
    }
    
    public function showDetailedReport() {
        $_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_day_start'] = $_POST['data']['start'];
        $_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_day_end'] = $_POST['data']['end'];
    }

    public function groupOnAccounting($incomes) {
        $grouped = array();
        
        foreach ($incomes as $entry) {
            if ($entry->isTaxTransaction && $this->isShowingIncTaxes()) {
                continue;
            }
            
            if (!isset($grouped[$entry->accountingNumber])) {
                $grouped[$entry->accountingNumber] = 0;
            }
            
            
            $amount = $entry->isActualIncome && !$entry->isOffsetRecord && !$this->isShowingIncTaxes() ? $entry->amountExTax : $entry->amount;
            $newVal = $grouped[$entry->accountingNumber] + $amount;
            $grouped[$entry->accountingNumber] = $newVal;
            
            ksort($grouped);
        }
        
        return $grouped;
    }

    public function showOrder() {
        $_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_day_orderid'] = $_POST['data']['orderid'];
    }
    public function cancelBrowsing() {
        unset($_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_day_start']);
        unset($_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_day_end']);
    }
    
    public function cancelOrderView() {
        unset($_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_day_orderid']);
    }

    public function getAccountDescription($account) {
        foreach ($this->paymentConfigs as $config) {
            if (!$config)
                continue;
            
            if ($config->offsetAccountingId_accrude == $account) 
                return "(accrued)";
            
            if ($config->offsetAccountingId_prepayment == $account) 
                return "(prepaid)";
            
            if ($config->userCustomerNumber == $account) 
                return $config->accountingDescription;
            
            if ($config->userCustomerNumberPaid == $account) 
                return $config->accountingDescription."<br/> (paid)";
        }
        
        $detail = $this->getApi()->getProductManager()->getAccountingDetail($account);
        if ($detail != null) {
            return $detail->description;
        }
        
        return "";
    }

    public function fetchConfigs() {
        $paymentApps = $this->getApi()->getStoreApplicationPool()->getActivatedPaymentApplications();
        foreach ($paymentApps as $paymentApp) {
            $this->paymentConfigs[] = $this->getApi()->getPaymentManager()->getStorePaymentConfiguration($paymentApp->id);
        }
        
    }

    public function showMonth() {
        $strTime = $_POST['data']['year']."-".$_POST['data']['month']."-1";
        $_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_all_start'] = date("1.m.Y 00:00:00", strtotime($strTime));
        $_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_all_end'] = date("t.m.Y 23:59:59", strtotime($strTime));
        $_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_SHOW_INC_TAX'] = $_POST['data']['tax'];
    }

    public function getEnd() {
        if (!isset($_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_all_end'])) {
            $_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_all_end'] = date('t.m.Y 23:59:59', strtotime('today'));
        }
        return $this->convertToJavaDate(strtotime($_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_all_end']));
    }

    public function getStart() {
        if (!isset($_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_all_start'])) {
            $_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_all_start'] = "1.".date('m.Y 00:00:00', strtotime('today'));
        }
        return $this->convertToJavaDate(strtotime($_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_all_start']));
    }

    public function closeCurrentPeriode() {
        $this->getApi()->getOrderManager()->closeTransactionPeriode($this->getEnd());
    }
    
    public function closeBankAccount() {
        $this->getApi()->getOrderManager()->closeBankAccount($this->getEnd());
    }
    
    public function downloadReport() {
        $this->includefile("pdfreport");
    }

    public function getOrderBalance($orderId, $records) {
        $total = 0;
        
        foreach ($records as $record) {
            if ($record->incrementalOrderId == $orderId) {
                $total += $record->amount;
            }
        }
        
        return round($total, 4);
    }

    public function getPaymentName($paymentType) {
        $name = explode("\\", $paymentType);
        return $name[1];
    }

    public function cancelAccountOverview() {
        unset($_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_account_summary']);
    }

    public function getTaxCodeForAccount($accountNumber) {
        return $this->getApi()->getProductManager()->getAccountingDetail($accountNumber)->taxgroup;
    }

    public function resetLastMonth() {
        $start = $this->getStart();
        $end = $this->getEnd();
        $this->getApi()->getOrderManager()->resetLastMonthClose($_POST['data']['password'], $start, $end);
    }
    
    public function transferData() {
        $system = $this->getApi()->getGetShopAccountingManager()->getCurrentSystemOther();
        
        if ($system == "VISMA_NET") {
            $this->doVismaTransfer();
        }
    }
    
    public function createVismaNETLoginLink() {
        $builder = new VismaNetPostBuilder($this->getApi(), $this);
        $result = $builder->startNewSession();
        $_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_VISMA_NET_OAUTHSESSION'] = $result->id;
        echo $result->loginLink;
        die();
    }

    public function doVismaTransfer() {
        $start = $this->getStart();
        $end = $this->getEnd();
        
        $builder = new VismaNetPostBuilder($this->getApi(), $this);
        $vismaDays = $builder->getResult($start, $end);
        $this->extramessage = $builder->sendData($vismaDays);
    }

    public function isShowingIncTaxes() {
        return !isset($_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_SHOW_INC_TAX']) 
        || $_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_SHOW_INC_TAX'] == "yes"
        || !$_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_SHOW_INC_TAX'];
    }
    
    public function startUploadOfData() {
        $start = $this->convertToJavaDate(strtotime($_POST['data']['start']));
        $end = $this->convertToJavaDate(strtotime($_POST['data']['end']));
        $this->getApi()->getGetShopAccountingManager()->transferData($start, $end);
    }

    public function downloadYearReport() {
        $this->includefile("excelreport");
    }

    public function changeMenu() {
        $_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_submenu'] = $_POST['data']['tab'];
    }
    
    public function getDoubledPostPaymentMethods() {
        $ret = array();
        $paymentApps = $this->getApi()->getStoreApplicationPool()->getActivatedPaymentApplications();
        foreach ($paymentApps as $paymentApp) {
            $paymentConfig = $this->getApi()->getPaymentManager()->getStorePaymentConfiguration($paymentApp->id);
            if ($paymentConfig->userCustomerNumberPaid != null && $paymentConfig->userCustomerNumberPaid != "") {
                $ret[] = $paymentApp;
            }
        }
        
        return $ret;        
    }
    
    public function getNameOfPaymentMethod($app) {
        $instance = $this->getFactory()->getApplicationPool()->createInstace($app);
        return $instance->getName();
    }

    public function getTab() {
        if (!isset($_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_submenu'])) {
            return "freport";
        }
        
        if ($_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_submenu'] == "doublepost") {
            return "doublepost";
        }
        
        return "freport";
    }

}
?>
