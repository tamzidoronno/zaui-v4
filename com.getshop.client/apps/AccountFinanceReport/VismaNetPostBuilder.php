<?php

namespace ns_e6570c0a_8240_4971_be34_2e67f0253fd3;

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

class VismaNetPostBuilder {
    
    /**
     * @var \GetShopApi
     */
    private $api;
    
    /**
     * @var AccountFinanceReport 
     */
    private $parent;

    function __construct($api, $parent) {
        $this->api = $api;
        $this->parent = $parent;
    }

    private function getTaxAccounts() {
        $taxes = $this->api->getProductManager()->getTaxes();
        $ret = array();
        
        foreach ($taxes as $tax) {
            if ($tax->accountingTaxAccount) {
                $ret[] = $tax->accountingTaxAccount;
            }
        }
        
        return $ret;
    }
    
    public function getResult($start, $end) {
        $_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_SHOW_INC_TAX'] = "no";
        
        $dayIncomes = $this->api->getOrderManager()->getDayIncomes($start, $end);
        $rows = array();
        
        $taxAccounts = $this->getTaxAccounts();
        
        $allDays = array();
        
        $allDayEntries = array();
        
        foreach ($dayIncomes as $dayIncome) {
            $allDayEntries = array_merge($allDayEntries, $dayIncome->dayEntries);
        }
        
        $i = 0;

        $data = new VismaJournalTransaction();
        $data->description =   new VismaValueObject("Overføring fra GetShop, dato:" .date('d.m.Y', strtotime($start)). " - ".date('d.m.Y', strtotime($end)));
        $data->financialPeriod = new VismaValueObject(date('Ym', strtotime($end)));
        $data->transactionDate = new VismaValueObject(date('Y-m-d\T00:00:00', strtotime($end)));
        $data->postPeriod = new VismaValueObject(date('Y-m-d\T00:00:00', strtotime($end)));

        $grouped = $this->parent->groupOnAccounting($allDayEntries);
        foreach ($grouped as $accountNumber => $amount) {
            $details = $this->parent->getApi()->getProductManager()->getAccountingDetail($accountNumber);

            $i++;
            $line = new VismaJournalTransactionLine();
            $line->lineNumber = new VismaValueObject($i);
            $line->accountNumber = new VismaValueObject($accountNumber);
            $line->referenceNumber = new VismaValueObject("");
            $line->transactionDescription = new VismaValueObject("");

            if ($amount > 0) {
                $line->debitAmountInCurrency = new VismaValueObject($amount);
                $line->creditAmountInCurrency = new VismaValueObject(0);
            } else {
                $amount *= -1;
                $line->creditAmountInCurrency = new VismaValueObject($amount);
                $line->debitAmountInCurrency = new VismaValueObject(0);
            }

            if ($details->subaccountid) {
                $line->setSubAccountIdAndValue($details->subaccountid, $details->subaccountvalue);
            }

            if (in_array($accountNumber, $taxAccounts)) {
                $line->vatId = new VismaValueObject($this->parent->getTaxCodeForAccount($accountNumber));
            } else {
                $line->vatCodeId = new VismaValueObject($this->parent->getTaxCodeForAccount($accountNumber));
            }

            $data->journalTransactionLines[] = $line;
        }

        $allDays[] = $data;
        
        return $allDays;
    }

    public function startNewSession() {
        $result = $this->api->getOAuthManager()->startNewOAuthSession(
            "https://integration.visma.net/API/resources/oauth/authorize", 
            "getshop_as_test_ae37op", 
            "financialstasks", 
            "e3b9d2a8-20cb-42eb-85d0-94eb79e9f6f6", 
            "https://integration.visma.net/API/security/api/v2/token");
    
        return $result;
    }

    public function sendData($vismaDays) {
        $oauthSessionId = isset($_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_VISMA_NET_OAUTHSESSION']) ? $_SESSION['ns_e6570c0a_8240_4971_be34_2e67f0253fd3_VISMA_NET_OAUTHSESSION'] : false;
        $result = $this->api->getOAuthManager()->getCurrentOAuthSession($oauthSessionId);
        $extramessage = "";
        
        $values = $this->api->getGetShopAccountingManager()->getConfigs("VISMA_NET");
        $vismaCompanyId = $values->vismacompanyid;
        
        if (!$vismaCompanyId) {
            return "Could not transfer, did not find the Visma Company Id, please specify it under config -> systems";
        }
        
        if (!$result->accessToken) {
            return "Lost access, please login to Visma again.";
        }
        
        $i = 0;
        foreach ($vismaDays as $day) {
            $i++;
            
            $jsonData = json_encode($day);
            
            file_put_contents("/tmp/vismanet_json_debug".$i.".txt", $jsonData);
            
            $ch = curl_init();
            curl_setopt($ch, constant("CURLOPT_" . 'URL'), "https://integration.visma.net/API/controller/api/v1/journaltransaction");
            curl_setopt($ch, constant("CURLOPT_" . 'POST'), true);
            curl_setopt($ch, constant("CURLOPT_" . 'POSTFIELDS'), $jsonData);
            curl_setopt($ch, CURLOPT_HTTPHEADER, array('authorization: Bearer '.$result->accessToken, "ipp-application-type: Visma.net Financials", "ipp-company-id: ".$vismaCompanyId, 'Content-Type: application/json'));
            
            ob_start();
            curl_exec($ch);
            $output = ob_get_contents();
            ob_end_clean();

            $info = curl_getinfo($ch);
            
            if ($info['http_code'] === 400) {
                $extramessage .= $day->description->value." | <span style='color: red'> Failed | ".$output." </span><br/>";
                break;
            }
            
            if ($info['http_code'] === 201) {
                $extramessage .= $day->description->value." | Sucessfully<br/>";
            } else {
                $extramessage .= $day->description->value." | Failed<br/>";
            }
        }
        
        return $extramessage;
    }

}

class VismaValueObject {
    public $value;
    
    function __construct($value) {
        $this->value = $value;
    }

}

class VismaJournalTransaction {
    public $batchNumber;
    public $hold;
    public $transactionDate;
    public $postPeriod;
    public $currencyId;
    public $autoReversing;
    public $createVatTransaction;
    public $skipVatAmountValidation;
    public $branch;
    public $ledger;
    public $description;
    
    public $controlTotalInCurrency = 0;
    public $journalTransactionLines = array();
    public $financialPeriod = 0;
    
    function __construct() {
        $this->hold = new VismaValueObject(true);
        $this->currencyId = new VismaValueObject("NOK");
        $this->autoReversing = new VismaValueObject(false);
        $this->createVatTransaction = new VismaValueObject(true);
        $this->skipVatAmountValidation = new VismaValueObject(true);
        $this->branch = new VismaValueObject("2");
        $this->description = new VismaValueObject("Overføring fra GetShop Booking");
        $this->ledger = new VismaValueObject("1");
    }
}

class VismaJournalTransactionLine {
    public $operation = "Insert";
    public $lineNumber;
    public $accountNumber;
    public $subaccount;
    public $referenceNumber;
    public $transactionDescription;
    public $debitAmountInCurrency;
    public $creditAmountInCurrency;
    public $vatCodeId;
    public $vatId;
    public $branch;
    
    function __construct() {
        $this->branch = new VismaValueObject("1    ");
        $this->subaccount = new \stdClass();
        $this->subaccount->segmentId = "1";
        $this->subaccount->segmentValue = "0";
    }
    
    function setSubAccountIdAndValue($id, $value) {
        $this->subaccount = new \stdClass();
        $this->subaccount->segmentId = $id;
        $this->subaccount->segmentValue = "$value";
    }

}