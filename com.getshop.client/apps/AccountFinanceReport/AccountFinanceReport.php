<?php
namespace ns_e6570c0a_8240_4971_be34_2e67f0253fd3;

class AccountFinanceReport extends \MarketingApplication implements \Application {
    private $paymentConfigs = array();
    public $extramessage = "";
    public $savedFreePost = false;
    
    public function getDescription() {
        
    }
    
    public function downloadDetailedReport() {
        $start = $this->convertToJavaDate(strtotime($_POST['data']['start'] . " 00:00:00"));
        $end = $this->convertToJavaDate(strtotime($_POST['data']['end'] . " 23:59:59"));
        
        $dayIncomes = $this->getApi()->getOrderManager()->getDayIncomes($start, $end);

        $accountingNumbers = array();
        $entries = array();
        foreach($dayIncomes as $income) {
            foreach($income->dayEntries as $dayEntry) {
                $accountingNumbers[$dayEntry->accountingNumber] = 0;
                $entries[] = $dayEntry;
            }
        }

        ksort($accountingNumbers);
        
        $i = 0;
        foreach($accountingNumbers as $accountnumber => $val) {
            $accountingNumbers[$accountnumber] = $i;
            $i++;
        }
        
        
        $header = array();
        $header[] = "Date";
        $header[] = "Orderid";
        foreach($accountingNumbers as $accountNumber => $offset) {
            $header[] = $accountNumber;
        }
        
        $matrix = array();
        $matrix[] = $header;
        
        $orders = $this->groupDayEntriesByOrder($dayIncomes);
        foreach($orders as $order) {
            $row = array();
            $row[] = date("d.m.Y", strtotime($order[0]->date));
            $row[] = $order[0]->incrementalOrderId;

            $entries = $this->groupOnAccounting($order);

            foreach($accountingNumbers as $accountNumber => $offset) {
                if(!isset($entries[$accountNumber])) {
                    $row[] = 0;
                } else {
                    $row[] = $entries[$accountNumber];
                }
            }
                    
            $matrix[] = $row;
        }
        echo json_encode($matrix);
    }
    

    public function getName() {
        return "AccountFinanceReport";
    }
    
    public function showDetailedProductView() {
        $_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_detailed_product_view'] = $_POST['data']['accountnumber'];
    }

    public function cancelDetailedProductView() {
        unset($_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_detailed_product_view']);
    }
    
    public function render() {
        $this->fetchConfigs();
        
        if (isset($_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_account_diff_report'])) {
            $this->includefile("diffreport");
            return;
        }
        
        if (isset($_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_detailed_for_payment_type'])) {
            $this->includefile("detailed_for_payment_type");
            return;
        }
        
        if (isset($_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_detailed_product_view'])) {
            $this->includefile("productsummary");
            return;
        }
        
        if (isset($_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_account_summary'])) {
            $this->includefile("accountsummary");
            return;
        }
        
        if (isset($_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_account_all_transactions'])) {
            $this->includefile("alltransactions");
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
        if ($system == "BUNTIMPORT_VISMA_BUSINESS") {
            $this->includefile("vismabunt");
        }
    }
    
    public function downloadAllTransactionsToExcel() {
        $excel = array();
        $start = $this->getStart();
        $end = $this->getEnd();
        
        
        $isPaymentRecords = isset($_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_paymentid']);
        if ($isPaymentRecords) {
            $dayIncome = $this->getApi()->getOrderManager()->getPaymentRecords($_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_paymentid'], $start, $end);
        } else {
            $dayIncome = $this->getApi()->getOrderManager()->getDayIncomesWithMetaData($start, $end);
        }
        
        $excel[] = array("Date", "Order id", "Amount inc tax", "Amount ex tax", "Name");
        foreach ($dayIncome as $income) {
            $day = date('d.m.Y', strtotime($income->start));
            foreach ($income->dayEntries as $entry) {
                if ($entry->accountingNumber != $_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_account_all_transactions']) {
                    continue;
                }
                
                $excel[] = array($day, $entry->incrementalOrderId, round($entry->amount,2), round($entry->amountExTax,2), @$entry->metaData->{"Guest name"});
            }
        }
        
        echo json_encode($excel);
    }

    public function showSummaryForAccount() {
        $_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_account_summary'] = $_POST['data']['account'];
    }
    
    public function cancelAllTransactionView() {
        unset($_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_account_all_transactions']);
    }
    
    public function showAllTransactionsForMonth() {
        $_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_account_all_transactions'] = $_POST['data']['account'];
    }
    
    public function showDetailedReport() {
        $_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_day_start'] = $_POST['data']['start'];
        $_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_day_end'] = $_POST['data']['end'];
    }

    public function groupOnAccounting($incomes, $batchId = false) {
        $grouped = array();
        
        foreach ($incomes as $entry) {
            if ($entry->isTaxTransaction && $this->isShowingIncTaxes()) {
                continue;
            }
            
            if (!isset($grouped[$entry->accountingNumber])) {
                $grouped[$entry->accountingNumber] = 0;
            }
            
            if ($batchId !== false && $entry->batchId != $batchId) {
                continue;
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
        if ($account == "0000") {
            return "f-report";
        }
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
        if(isset($_POST['data']['tax'])) {
            $_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_SHOW_INC_TAX'] = $_POST['data']['tax'];
        }
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
    
    public function transferAllDays() {
        $this->getApi()->getGetShopAccountingManager()->transferAllDaysThatCanBeTransferred();
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
        $accounttaxgrp = $this->getApi()->getProductManager()->getAccountingDetail($accountNumber);
        if(!$accounttaxgrp) {
            return "";
        } else {
            return $accounttaxgrp->taxgroup;
        }
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
        } else {
            $this->getApi()->getGetShopAccountingManager()->transferDoublePostFile($_POST['data']['doublepostid']);
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
        $this->extramessage = $builder->sendData($vismaDays, $this->getFactory()->getStore()->id);
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
    
    public function changeClosedDate() {
        if (!$_POST['data']['description']) {
            return;
        }
        
        $date = $this->convertToJavaDate(strtotime($_POST['data']['totime']));
        $this->getApi()->getOrderManager()->changeClosedDate($_POST['data']['description'], $date);
    }

    public function getTab() {
        if (!isset($_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_submenu'])) {
            return "freport";
        }
        
        if ($_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_submenu'] == "doublepost") {
            return "doublepost";
        }
        
        if ($_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_submenu'] == "settings") {
            return "settings";
        }
        
        if ($_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_submenu'] == "freeposting") {
            return "freeposting";
        }
        
        return "freport";
    }

    public function createTransferFiles() {
        $start = $this->getStart();
        $end = $this->getEnd();
        $paymentId = $_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_paymentid'];
        $this->getApi()->getOrderManager()->createNewDoubleTransferFile($paymentId, $start, $end);
    }

    /**
     * 
     * @param \core_getshopaccounting_DoublePostAccountingTransfer $file
     */
    public function getSum($file) {
        $amounts = array();
        foreach ($file->incomes as $income) {
            
            foreach ($income->dayEntries as $entry) {
                if (!isset($amounts[$entry->accountingNumber])) {
                    $amounts[$entry->accountingNumber] = 0;
                }
                
                $amounts[$entry->accountingNumber] += $entry->amount;   
            }
        }
        
        return $amounts;
    }

    public function downloadUnsettledAmountExcel() {
        $start = $this->getStart();
        $end = $this->getEnd();
        $paymentId = isset($_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_paymentid']) ? $_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_paymentid']  : null;
        $unsettledAmounts = $this->getApi()->getOrderManager()->getOrdersUnsettledAmount($_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_account_summary'], $end, $paymentId);
        
        $rows = array();
        
        $rows[] = array('orderid', 'name', 'amount', 'VAT number', 'Address', 'City', 'PostCode');
        
        foreach ($unsettledAmounts as $unsettledAmount) {
            $row = array();
            $row[] = $unsettledAmount->order->incrementOrderId;
            $row[] = $unsettledAmount->order->cart->address->fullName;
            $row[] = $unsettledAmount->amount; 
            
            if ($row ) {
                $user = $this->getApi()->getUserManager()->getUserById($unsettledAmount->order->userId);
                $row[] = $user->isCompanyMainContact && $user->companyObject ? $user->companyObject->vatNumber : "N/A";
            }
            
            $row[] = $unsettledAmount->order->cart->address->address;
            $row[] = $unsettledAmount->order->cart->address->city;
            $row[] = $unsettledAmount->order->cart->address->postCode;
            
            $rows[] = $row;
        }
        
        echo json_encode($rows);
    }

    public function isJson($string) {
         json_decode($string);
        return (json_last_error() == JSON_ERROR_NONE);
    }

    public function createFreePosting() {
        $freePost = new \core_ordermanager_data_AccountingFreePost();
        $freePost->amount = $_POST['data']['amount'];
        $freePost->date = $this->convertToJavaDate(strtotime($_POST['data']['date']));
        $freePost->creditAccountNumber = $_POST['data']['creditaccount'];
        $freePost->debitAccountNumber = $_POST['data']['debitaccount'];
        $freePost->comment = $_POST['data']['comment'];
        $this->savedFreePost = $this->getApi()->getOrderManager()->saveFreePost($freePost);
    }
    
    public function deleteRecord() {
        $this->getApi()->getOrderManager()->deleteFreePost($_POST['data']['freepostid']);
        $this->cancelOrderView();
    }
    
    public function showDiffReport() {
        $_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_account_diff_report'] = true;
    }
    
    public function cancelDiffReport() {
        unset($_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_account_diff_report']);
    }

    public function transferFileToPo() {
        echo "OK";
    }
    
    public function deleteDoublePostingFile() {
        $this->getApi()->getOrderManager()->deleteDoublePostingFile($_POST['data']['fileid']);
    }

    public function getBatchIncomes($dayEntries) {
        $ids = array();
        
        foreach ($dayEntries as $entry) {
            $ids[] = $entry->batchId;
        }
        
        $ids = array_unique($ids);
        
        return $ids;
    }

    public function showPaymentMethodSummary() {
        $_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_detailed_for_payment_type'] = $_POST['data']['account'];
    }
    
    public function cancelPaymentDetailedOverview() {
        unset($_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_detailed_for_payment_type']);
    }
    
    public function applyDiff() {
        $this->getApi()->getOrderManager()->applyCorrectionForOrder($_POST['data']['orderid'], $_POST['data']['password']);
    }

    public function groupDayEntriesByOrder($dayIncomes) {
        $orders = array();
        foreach ($dayIncomes as $dayIncome) {
            foreach ($dayIncome->dayEntries as $entry) {
                if ($entry->isTaxTransaction && $this->isShowingIncTaxes()) {
                    continue;
                }

                $orderId = $entry->orderId;

                if ($entry->freePostId) {
                    $orderId = $entry->freePostId;
                } 

                if (!isset($orders[$orderId])) {
                    $orders[$orderId] = array();
                }

                $orders[$orderId][] = $entry;
            }
        }
        return $orders;
    }

    public function addExtraAccounts($allAccountNumbers) {
        $accounts = $this->getApi()->getProductManager()->getAccountingAccounts();
        
        foreach ($accounts as $account) {
            if (!in_array($account->accountNumber, $allAccountNumbers)) {
                $allAccountNumbers[] = $account->accountNumber;
            }
        }
        
        $data = $this->getApi()->getStoreApplicationPool()->getActivatedPaymentApplications();
        foreach($data as $app) {
            $config = $this->getApi()->getPaymentManager()->getStorePaymentConfiguration($app->id);
            if ($config && $config->userCustomerNumber && !in_array($config->userCustomerNumber, $allAccountNumbers)) {
                $allAccountNumbers[] = $config->userCustomerNumber;
            }
        }
        
        return $allAccountNumbers;
    }

    public function getFreportRows($start, $end) {
        $dayIncomes = $this->getApi()->getOrderManager()->getDayIncomes($start, $end);

        foreach ($dayIncomes as $dayIncome) {
            if ($dayIncome->errorMsg) {
                $errors[] = $dayIncome->errorMsg;
            } else {
                foreach ($dayIncome->dayEntries as $entry) {
                    if ($entry->isTaxTransaction && $this->isShowingIncTaxes()) {
                        continue;
                    }
                    $allAccountNumbers[] = $entry->accountingNumber;
                }
            }
        }
        $allAccountNumbers = $this->addExtraAccounts($allAccountNumbers);
        $accounts = array_unique($allAccountNumbers);


        $rows = array();
        foreach ($dayIncomes as $dayIncome) {
            $row = new \stdClass();
            $row->start = date("d.m.Y H:i", strtotime($dayIncome->start));
            $row->end = date("d.m.Y H:i", strtotime($dayIncome->end));
            $row->locked = $dayIncome->isFinal ? true : false;
            $grouped = $this->groupOnAccounting($dayIncome->dayEntries);
            $controlSum = 0;
            foreach ($accounts as $account) {
                if (isset($grouped[$account]) && $grouped[$account]) {
                    $sum[$account] = @$sum[$account] + $grouped[$account];
                }

                if (isset($grouped[$account]) && $grouped[$account]) {
                    $controlSum += ($grouped[$account]);
                }

                $val = isset($grouped[$account]) && $grouped[$account] ? $grouped[$account] : "0";

                $row->$account = $val;
            }

            if ($controlSum < 0.00001 && $controlSum > -0.00001 ) {
                $controlSum = 0;
            }
            $row->controlSum = $controlSum;
            $rows[] = $row;
        }        
        
        return $rows;
    }

}
?>
