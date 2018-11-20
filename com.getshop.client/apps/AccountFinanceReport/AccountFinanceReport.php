<?php
namespace ns_e6570c0a_8240_4971_be34_2e67f0253fd3;

class AccountFinanceReport extends \MarketingApplication implements \Application {
    private $paymentConfigs = array();
    
    public function getDescription() {
        
    }

    public function getName() {
        return "AccountFinanceReport";
    }

    public function render() {
        $this->fetchConfigs();
        if (isset($_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_day_orderid'])) {
            $this->includefile("orderview");
            return;
        } 
        if (isset($_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_day_start']) && $_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_day_start']) {
            $this->includefile("dayoverview");
            return;
        } 
        
        $this->includefile("freport");
    }

    public function createGroupByAccount($dayIncome) {
        
    }
    
    public function showDetailedReport() {
        $_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_day_start'] = $_POST['data']['start'];
        $_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_day_end'] = $_POST['data']['end'];
    }

    public function groupOnAccounting($incomes) {
        $grouped = array();
        
        foreach ($incomes as $entry) {
            
            if (!isset($grouped[$entry->accountingNumber])) {
                $grouped[$entry->accountingNumber] = 0;
            }

            $grouped[$entry->accountingNumber] = $grouped[$entry->accountingNumber] + $entry->amount;
            
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
    
    public function downloadReport() {
        $this->includefile("pdfreport");
    }
}
?>
