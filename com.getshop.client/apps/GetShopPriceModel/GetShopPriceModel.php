<?php

namespace ns_94c0992f_85d5_4a63_a30c_685ee0f8b17e;

class GetShopPriceModel extends \WebshopApplication implements \Application {

    public function getDescription() {
        
    }

    public function getName() {
        return "GetShopPriceModel";
    }

    public function render() {

        $this->includefile("calculator");
    }

    public function log() {
        $data = json_encode($_POST['data']);
        $this->getApi()->getTrackerManager()->logTracking("GetShopPriceModel", "getshop_price_model", "changed", $data);
        $vars = implode("&", $_POST['data']);
        $link = "/scripts/pricecalculator.php?vars=var";
        foreach ($_POST['data'] as $key => $var) {
            $link .= "&" . $key . "=" . $var;
        }
    }

    public function sendOffer() {
        ob_start();
        $this->includefile("pdfdocument");
        $content = ob_get_contents();
        ob_end_clean();
        
        $pdf = $this->getApi()->getGetShop()->getBase64EncodedPDFWebPageFromHtml(base64_encode($content));
        $this->getApi()->getUtilManager()->sendPriceOffer($pdf, $_POST['data']['email']);
    }

    public function downloadPdf() {
        $this->includefile("pdfdocument");
    }

    public function calculate() {
        $_SESSION['ns_94c0992f_85d5_4a63_a30c_685ee0f8b17e_calcdata'] = $_POST['data'];
        $this->includefile("summary");
    }

    public function genereatePriceMatrix() {
        $priceObject = $this->getPriceMatrix();
       
        $rooms = $_SESSION['ns_94c0992f_85d5_4a63_a30c_685ee0f8b17e_calcdata']['rooms'];
        $locks = $_SESSION['ns_94c0992f_85d5_4a63_a30c_685ee0f8b17e_calcdata']['locks'];
        $entrancelocks = $_SESSION['ns_94c0992f_85d5_4a63_a30c_685ee0f8b17e_calcdata']['entrancelocks'];
        $restaurantEntryPoints = $_SESSION['ns_94c0992f_85d5_4a63_a30c_685ee0f8b17e_calcdata']['restaurantEntryPoints'];
        $selfcheckinindoor = $_SESSION['ns_94c0992f_85d5_4a63_a30c_685ee0f8b17e_calcdata']['selfcheckinindoor'];
        $selfcheckinoutdoor = $_SESSION['ns_94c0992f_85d5_4a63_a30c_685ee0f8b17e_calcdata']['selfcheckinoutdoor'];
        $pgas = $_SESSION['ns_94c0992f_85d5_4a63_a30c_685ee0f8b17e_calcdata']['pgas'];
        $customwebsite = isset($_SESSION['ns_94c0992f_85d5_4a63_a30c_685ee0f8b17e_calcdata']['customwebsite']) && $_SESSION['ns_94c0992f_85d5_4a63_a30c_685ee0f8b17e_calcdata']['customwebsite'] === "true";
        $integrationtoaccounting = isset($_SESSION['ns_94c0992f_85d5_4a63_a30c_685ee0f8b17e_calcdata']['integrationtoaccounting']) && $_SESSION['ns_94c0992f_85d5_4a63_a30c_685ee0f8b17e_calcdata']['integrationtoaccounting'] === "true";
        $getshopdosetup = isset($_SESSION['ns_94c0992f_85d5_4a63_a30c_685ee0f8b17e_calcdata']['getshopdosetup']) && $_SESSION['ns_94c0992f_85d5_4a63_a30c_685ee0f8b17e_calcdata']['getshopdosetup'] === "true";
        $getshopinstalllocks = isset($_SESSION['ns_94c0992f_85d5_4a63_a30c_685ee0f8b17e_calcdata']['getshopinstalllocks']) && $_SESSION['ns_94c0992f_85d5_4a63_a30c_685ee0f8b17e_calcdata']['getshopinstalllocks'] === "true";
        $getshoptraining = isset($_SESSION['ns_94c0992f_85d5_4a63_a30c_685ee0f8b17e_calcdata']['getshoptraining']) && $_SESSION['ns_94c0992f_85d5_4a63_a30c_685ee0f8b17e_calcdata']['getshoptraining'] === "true";

        $roomLicenceCost = ($priceObject->roomLicense * $rooms);
        $locksLicenceCost = ($priceObject->lockLicense * $locks);

        $totalMonthly = 0.0;
        $minTotalDiff = 0;
        $totalMonthly += $roomLicenceCost;
        $totalMonthly += $locksLicenceCost;
        if ($integrationtoaccounting) {
            $totalMonthly += $priceObject->accountinglicense;
        }
        if ($totalMonthly < $priceObject->minMonthlyCost && $totalMonthly > 0) {
            $minTotalDiff = $priceObject->minMonthlyCost - $totalMonthly;
            $totalMonthly = $priceObject->minMonthlyCost;
        }
        
        $restaurantkiosksprice = 0;
        $restaurantcost = 0;
        if($restaurantEntryPoints > 0) {
            $restaurantcost = $priceObject->restaurantCost;
            $restaurantkiosksprice += ($restaurantEntryPoints * $priceObject->restaurantEntryPoints);
        }

        $lockPriceStartup = ($priceObject->lockPrice * $locks);
        $entranceDoorPriceTotal = ($entrancelocks * $priceObject->mainEntrancePrice);
        $terminalIndoorCosts = ($priceObject->terminalIndoorPrice * $selfcheckinindoor);
        $terminalOutdoorCosts = ($priceObject->terminalOutdoorPrice * $selfcheckinoutdoor);
        $pgatotalcosts = ($pgas * $priceObject->pgaPrice);
        $installationPrice = ($locks * $priceObject->installLockPrice);

        $totalSetupCost = 0;
        $totalSetupCost += $lockPriceStartup;
        $totalSetupCost += $terminalIndoorCosts;
        $totalSetupCost += $terminalOutdoorCosts;
        $totalSetupCost += $pgatotalcosts;
        $totalSetupCost += $entranceDoorPriceTotal;
        if ($getshopinstalllocks) {
            $totalSetupCost += $installationPrice;
        }
        if ($customwebsite) {
            $totalSetupCost += $priceObject->websitePrice;
        }
        if ($getshopdosetup) {
            $totalSetupCost += $priceObject->bookingPrice;
        }
        if ($getshoptraining) {
            $totalSetupCost += $priceObject->trainingProgramPrice;
        }
        if ($integrationtoaccounting) {
            $totalSetupCost += $priceObject->integrationToAccountingPrice;
        }

        $repeaters = 0;
        $servers = 0;
        if ($locks > 0) {
            $repeaters = $locks / 6;
            $servers = $locks / 30;
            $repeaters = round($repeaters);
            if ($servers < 1) {
                $servers = 1;
            }
            $servers = ceil($servers);

            $totalSetupCost += ($repeaters * $priceObject->repeaterPrice);
            $totalSetupCost += ($servers * $priceObject->serverPrice);
        }

        $discountTotal = 0;
        $discountMonthlyTotal = 0;
        if (\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::isAdministrator()) {
            $discountTotal = isset($_SESSION['ns_94c0992f_85d5_4a63_a30c_685ee0f8b17e_calcdata']['discounttotal']) && $_SESSION['ns_94c0992f_85d5_4a63_a30c_685ee0f8b17e_calcdata']['discounttotal'] ? $totalSetupCost * ($_SESSION['ns_94c0992f_85d5_4a63_a30c_685ee0f8b17e_calcdata']['discounttotal'] / 100) : 0;
            $discountMonthlyTotal = isset($_SESSION['ns_94c0992f_85d5_4a63_a30c_685ee0f8b17e_calcdata']['discountlicense']) && $_SESSION['ns_94c0992f_85d5_4a63_a30c_685ee0f8b17e_calcdata']['discountlicense'] ? $totalMonthly * ($_SESSION['ns_94c0992f_85d5_4a63_a30c_685ee0f8b17e_calcdata']['discountlicense'] / 100) : 0;
        }

        if ($discountTotal) {
            $discountTotal = round($discountTotal);
        }

        if ($discountMonthlyTotal) {
            $discountMonthlyTotal = round($discountMonthlyTotal);
        }
        
        $totalSetupCost += $restaurantkiosksprice;
        $totalSetupCost += $restaurantcost;
        
        $priceMatrix = array();
        $priceMatrix['totalSetupCost'] = $totalSetupCost;
        $priceMatrix['lockPriceStartup'] = $lockPriceStartup;
        $priceMatrix['entranceDoorPriceTotal'] = $entranceDoorPriceTotal;
        $priceMatrix['terminalIndoorCosts'] = $terminalIndoorCosts;
        $priceMatrix['terminalOutdoorCosts'] = $terminalOutdoorCosts;
        $priceMatrix['pgatotalcosts'] = $pgatotalcosts;
        $priceMatrix['installationPrice'] = $installationPrice;
        $priceMatrix['roomLicenceCost'] = $roomLicenceCost;
        $priceMatrix['locksLicenceCost'] = $locksLicenceCost;
        $priceMatrix['discountTotal'] = $discountTotal;
        $priceMatrix['discountMonthlyTotal'] = $discountMonthlyTotal;
        $priceMatrix['servers'] = $servers;
        $priceMatrix['repeaters'] = $repeaters;
        $priceMatrix['totalMonthly'] = $totalMonthly;
        $priceMatrix['minMonthly'] = $minTotalDiff;
        
        $priceMatrix['serversCost'] = $servers * $priceObject->serverPrice;
        $priceMatrix['repeatersCost'] = $repeaters * $priceObject->repeaterPrice;
        $priceMatrix['restaurantCost'] = $restaurantcost;
        $priceMatrix['restaurantEntryPoints'] = $restaurantkiosksprice;

        return $priceMatrix;
    }

    public function getNames() {
        $names = array();
        $names['totalSetupCost'] = "Total startup cost";
        $names['lockPriceStartup'] = "Locks";
        $names['entranceDoorPriceTotal'] = "Entrance door";
        $names['terminalIndoorCosts'] = "Terminals (indoor)";
        $names['terminalOutdoorCosts'] = "Terminals (outdoor)";
        $names['pgatotalcosts'] = "Pgas";
        $names['installationPrice'] = "Installation";
        $names['roomLicenceCost'] = "Room license";
        $names['locksLicenceCost'] = "Lock license";
        $names['discountTotal'] = "Discounts";
        $names['discountMonthlyTotal'] = "Discounts monthly fee";
        $names['servers'] = "Servers";
        $names['repeaters'] = "Repeaters";
        $names['totalMonthly'] = "Total monthly cost";
        $names['serversCost'] = "Server costs";
        $names['repeatersCost'] = "Repeaters";
        $names['restaurantCost'] = "Resturant startup";
        $names['restaurantEntryPoints'] = "Resturant kiosks";
        return $names;
    }
    
    public function getPriceMatrix() {
        $appname = get_class($this);
        if (strpos($appname, "\\")) {
            $appname = substr($appname, 0, strpos($appname, "\\"));
        }
        
        $currency = "usd";
        if ($_SESSION['ns_94c0992f_85d5_4a63_a30c_685ee0f8b17e_calcdata']['currency'] === "2") {
            $currency = "nok";
        }
        
        if ($_SESSION['ns_94c0992f_85d5_4a63_a30c_685ee0f8b17e_calcdata']['currency'] === "3") {
            $currency = "eur";
        }
        
        $content = file_get_contents('../app/' . $appname ."/prices_".$currency.".json");
        return json_decode($content);
    }

}

?>