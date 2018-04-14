<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
session_id($_GET['id']);

//chdir("../");
//include '../loader.php';
//$factory = IocContainer::getFactorySingelton();

// Defined prices
$roomLicense = 4.49;
$lockLicense = 3.72;
$accountinglicense = 128.36;
$lockPrice = 353.04;
$websitePrice = 3729.10;
$bookingPrice = 3729.10;
$terminalIndoorPrice = 7450.10;
$terminalOutdoorPrice = 12588.10;
$trainingProgramPrice = 1156;
$installLockPrice = 115.6;
$mainEntrancePrice = 1027;
$pgaPrice = 257;
$repeaterPrice = 64.22;
$serverPrice = 192.81;
$integrationToAccountingPrice = 3083.04;
$minMonthlyCost = 65;

$currency = isset($_GET['currency']) ? $_GET['currency'] : "USD";


if ($currency === "NOK") {
    $roomLicense = 35;
    $lockLicense = 29;
    $accountinglicense = 1000;
    $lockPrice = 2750;
    $websitePrice = 29000;
    $bookingPrice = 29000;
    $terminalIndoorPrice = 58000;
    $terminalOutdoorPrice = 90000;
    $trainingProgramPrice = 9000;
    $installLockPrice = 900;
    $mainEntrancePrice = 9000;
    $pgaPrice = 2000;
    $repeaterPrice = 500;
    $serverPrice = 1500;
    $integrationToAccountingPrice = 24000;
    $minMonthlyCost = 500;
}


// CALCS

$rooms=$_GET['rooms'];
$locks=$_GET['locks'];
$entrancelocks=$_GET['entrancelocks'];
$selfcheckinindoor=$_GET['selfcheckinindoor'];
$selfcheckinoutdoor=$_GET['selfcheckinoutdoor'];
$pgas=$_GET['pgas'];
$customwebsite=isset($_GET['customwebsite']) &&  $_GET['customwebsite'] === "true";
$integrationtoaccounting=isset($_GET['integrationtoaccounting']) &&  $_GET['integrationtoaccounting'] === "true";
$getshopdosetup=isset($_GET['getshopdosetup']) &&  $_GET['getshopdosetup'] === "true";
$getshopinstalllocks=isset($_GET['getshopinstalllocks']) &&  $_GET['getshopinstalllocks'] === "true";
$getshoptraining=isset($_GET['getshoptraining']) &&  $_GET['getshoptraining'] === "true";

$roomLicenceCost = ($roomLicense * $rooms);
$locksLicenceCost = ($lockLicense * $locks);

$totalMonthly = 0.0;
$totalMonthly += $roomLicenceCost;
$totalMonthly += $locksLicenceCost;
if($integrationtoaccounting) {
    $totalMonthly += $accountinglicense;
}
if($totalMonthly < 65 && $totalMonthly > 0) {
    $totalMonthly = $minMonthlyCost;
}

$lockPriceStartup = ($lockPrice * $locks);
$entranceDoorPriceTotal = ($entrancelocks * $mainEntrancePrice);
$terminalIndoorCosts = ($terminalIndoorPrice * $selfcheckinindoor);
$terminalOutdoorCosts = ($terminalOutdoorPrice * $selfcheckinoutdoor);
$pgatotalcosts = ($pgas * $pgaPrice);
$installationPrice = ($locks * $installLockPrice);

$totalSetupCost = 0;
$totalSetupCost += $lockPriceStartup;
$totalSetupCost += $terminalIndoorCosts;
$totalSetupCost += $terminalOutdoorCosts;
$totalSetupCost += $pgatotalcosts;
$totalSetupCost += $entranceDoorPriceTotal;
if($getshopinstalllocks) {
    $totalSetupCost += $installationPrice;
}
if($customwebsite) { $totalSetupCost += $websitePrice; }
if($getshopdosetup) { $totalSetupCost += $bookingPrice; }
if($getshoptraining) { $totalSetupCost += $trainingProgramPrice; }
if($integrationtoaccounting) { $totalSetupCost += $integrationToAccountingPrice; }

$repeaters = 0;
$servers = 0;
if($locks > 0) {
    $repeaters = $locks / 6;
    $servers = $locks / 30;
    $repeaters = round($repeaters);
    if($servers < 1) { $servers = 1; }
    $servers = ceil($servers);

    $totalSetupCost += ($repeaters * $repeaterPrice);
    $totalSetupCost += ($servers * $serverPrice);
}

//if ($factory->getStore()->id !== "13442b34-31e5-424c-bb23-a396b7aeb8ca") {
//    return "";
//}

$discountTotal = 0;
$discountMonthlyTotal = 0;
if (ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::isAdministrator()) {
    $discountTotal = isset($_GET['discounttotal']) && $_GET['discounttotal'] ? $totalSetupCost * ($_GET['discounttotal']/100) : 0;
    $discountMonthlyTotal = isset($_GET['discountlicense']) && $_GET['discountlicense'] ? $totalMonthly * ($_GET['discountlicense']/100) : 0;
}

if ($discountTotal) {
    $discountTotal = round($discountTotal);
}

if ($discountMonthlyTotal) {
    $discountMonthlyTotal = round($discountMonthlyTotal);
}
?>

<style>
    .headertext {
        margin-top: 30px;
    }
    
    .page {
        margin: 50px;
    }
    
    .row .count,
    .row .price,
    .row .type {
        display: inline-block;
    }
    
    .row .price { width: 250px; }
    .row .count { width: 150px; }
    .row .type { width: 450px; }
    
    .row {
        border-bottom: solid 1px #DDD;
        margin-top: 10px;
    }
    .row .description {
        font-style: italic;
        padding: 10px;
        color: grey;
    }
    
    h2 {
        margin-top: 50px;
        padding-bottom: 10px;
        border-bottom: solid 2px;
        text-align: center;
    }
    
    @media print {
        .new-page {
          page-break-before: always;
        }
    }
</style>
<div class="page">
    <div class='pagetitle'>
        <img src="https://www.getshop.com/displayImage.php?id=3e2e2471-3911-4827-b268-578246282137&crop=true&x=19&x2=290&y=20&y2=119&rotation=0&width=285"/>
    </div>
    
    <div class="headertext">
        <div style="font-weight: bold; font-size: 20px; color: red; font-style: italic; border: solid 1px red; padding: 10px; text-align: center;">
            All prices are excluded TAX / VAT.
        </div>
        <br/>
        <br/>
        This offer is valid until <span style="color: green; font-weight: bold;"> <? echo date('d/m-Y', strtotime("+14 day")); ?> 23:00 UTC−01:00</span> and is created from prices in www.getshop.com/pricing.html, to adjust the offer you can go back to this page at any time.
    </div>
    
    <h2>Startup Costs</h2>
    
    <?
    if ($locks) {
    ?>
    <div class="row">
        <div class="type">
            Locks
        </div>
        <div class="count">
            <? echo $locks." x ".$lockPrice; ?>
        </div>
        <div class="price">
            <? echo $lockPriceStartup." ".$currency; ?>
        </div>
        <div class="description">
            We provide you with <? echo $locks; ?> locks. Additionally you need batteries, 4 batteries for each lock.
        </div>
    </div>
    <?
    }
    ?>
    
    <?
    if ($entrancelocks) {
    ?>
    <div class="row">
        <div class="type">
            Common entrance access controller
        </div>
        <div class="count">
            <? echo $entrancelocks." x ".$mainEntrancePrice; ?>
        </div>
        <div class="price">
            <? echo $entranceDoorPriceTotal." ".$currency; ?>
        </div>
        <div class="description">
            A WIFI, Ethernet, 2 doors access controller. Can connect to swing door operators etc, and has possibility to hold all your guests codes.
        </div>
    </div>
    <?
    }
    ?>
    
    <?
    if ($selfcheckinindoor) {
    ?>
    <div class="row">
        <div class="type">
            Indoor checkin terminal
        </div>
        <div class="count">
            <? echo $selfcheckinindoor." x ".$terminalIndoorPrice; ?>
        </div>
        <div class="price">
            <? echo $terminalIndoorCosts." ".$currency; ?>
        </div>
        <div class="description">
            Let your guest do the checkin work for you, they can book, pay and checkin themself by using this indoor machine
        </div>
    </div>
    <?
    }
    ?>
    
    <?
    if ($selfcheckinoutdoor) {
    ?>
    <div class="row">
        <div class="type">
            Outdoor checkin terminal
        </div>
        <div class="count">
            <? echo $selfcheckinoutdoor." x ".$terminalOutdoorPrice; ?>
        </div>
        <div class="price">
            <? echo $terminalOutdoorCosts." ".$currency; ?>
        </div>
        <div class="description">
            A 32 inch waterproof and weatherproof terminal with creditcard reader, build for outdoor usage. Your guests can book, pay and receive a receipt with an accesscode trough this terminal.
        </div>
    </div>
    <?
    }
    ?>
    
    <?
    if ($pgas) {
    ?>
    <div class="row">
        <div class="type">
            PGA - Personal Guest Assistant
        </div>
        <div class="count">
            <? echo $pgas." x ".$pgaPrice; ?>
        </div>
        <div class="price">
            <? echo $pgatotalcosts." ".$currency; ?>
        </div>
        <div class="description">
            The PGA's are used at the hotel-rooms where guests can do a few operations themself, like request late checkout, personal wifi, etc.
        </div>
    </div>
    <?
    }
    ?>
    
    <?
    if($getshopinstalllocks) {
    ?>
    <div class="row">
        <div class="type">
            Installation of locks
        </div>
        <div class="count">
            <? echo $locks." x ".$installLockPrice; ?>
        </div>
        <div class="price">
            <? echo $installationPrice." ".$currency; ?>
        </div>
        <div class="description">
            GetShop personal travels to your hotel and do the installation for you of the locks, include them into the network and do a fully test of the system. Teaches you how to maintain the locks and how they works. 
            <br/>* Travel charges will be charged additionally unless else agreed upon.
        </div>
    </div>
    <?
    }
    ?>
    
    <?
    if($customwebsite) {
    ?>
    <div class="row">
        <div class="type">
            Custom HTML5 Webpage.
        </div>
        <div class="count">
            <? echo "1 x ".$websitePrice; ?>
        </div>
        <div class="price">
            <? echo $websitePrice." ".$currency; ?>
        </div>
        <div class="description">
            GetShop consulatant will setup a custom website with your designtheme or help you design a custom page for you. Fonts, Skin, colors, pictures and text are 100% customized according to your need. This will be done so in the GetShop CMS system.
        </div>
    </div>
    <?
    }
    ?>
    
    <?
    if($integrationtoaccounting) {
    ?>
    <div class="row">
        <div class="type">
            Integration with Accounting System.
        </div>
        <div class="count">
            <? echo "1 x ".$integrationToAccountingPrice; ?>
        </div>
        <div class="price">
            <? echo $integrationToAccountingPrice." ".$currency; ?>
        </div>
        <div class="description">
            GetShop will connect your system to an accounting system. Contact post@getshop.com to check if your accountingsystem is supported.
        </div>
    </div>
    <?
    }
    ?>
    
    <?
    if($getshopdosetup) {
    ?>
    <div class="row">
        <div class="type">
            Configuration and setup by GetShop
        </div>
        <div class="count">
            <? echo "1 x ".$bookingPrice; ?>
        </div>
        <div class="price">
            <? echo $bookingPrice." ".$currency; ?>
        </div>
        <div class="description">
            A GetShop consultant do all the initial configuration, setup and connects your system to the channel manager.
        </div>
    </div>
    <?
    }
    ?>
    
    <?
    if($getshoptraining) {
    ?>
    <div class="row">
        <div class="type">
            Online or onsite training. 
        </div>
        <div class="count">
            <? echo "1 x ".$trainingProgramPrice; ?>
        </div>
        <div class="price">
            <? echo $trainingProgramPrice." ".$currency; ?>
        </div>
        <div class="description">
            GetShop offers a 9 hours traning program to help you get started with the system and to get the most of it. 
            <br/>* Travel charges will be charged additionally unless else agreed upon
        </div>
    </div>
    <?
    }
    ?>
    
    <div class="row" style='font-weight: bold; color: green; font-size: 20px;'>
        <div class="type">
            Total startup cost. 
        </div>
        <div class="count">
        </div>
        <div class="price">
            <? echo $totalSetupCost." ".$currency; ?>
        </div>
        <div class="description"></div>
    </div>
    
    <?
    if ($discountTotal) {
    ?>
        <div class="row">
            <div class="type">
                Discount ( <? echo $_GET['discounttotal']; ?> % )
            </div>
            <div class="count">
            </div>
            <div class="price">
                <? echo "- ".$discountTotal." ".$currency; ?>
            </div>
            <div class="description">
                * Discount is given based upon the current selection, removing features, functions etc might affect the discount
            </div>
        </div>
    
        <div class="row" style='font-weight: bold; color: green; font-size: 20px;'>
            <div class="type">
                Total price after discount
            </div>
            <div class="count">
            </div>
            <div class="price">
                <? echo ($totalSetupCost - $discountTotal)." ".$currency; ?>
            </div>
            <div class="description"></div>
        </div>
    <?
    }
    ?>
    <div style="border: solid 2px red; padding: 10px; text-align: center; font-weight: bold; color: red;"> See page 2 for monthly costs</div>
    
    <div class='new-page'><br/></div>
    <h2>Monthly costs</h2>
    We charge monthly in advance for the following.
    
    <?
    if ($_GET['rooms']) {
    ?>
    <div class="row">
        <div class="type">
            Rooms
        </div>
        <div class="count">
            <? echo $_GET['rooms']; ?>
        </div>
        <div class="price">
            <? echo $roomLicenceCost." ".$currency." / Month"; ?>
        </div>
        <div class="description">
            Create as many categories for the rooms you want and add <? echo $_GET['rooms']; ?> rooms to your bookingsystem.
        </div>
    </div>
    <?
    }
    ?>
    
    <?
    if ($locks) {
    ?>
    
    <div class="row">
        <div class="type">
            Locks
        </div>
        <div class="count">
            <? echo $locks; ?>
        </div>
        <div class="price">
            <? echo $locksLicenceCost." ".$currency." / Month"; ?>
        </div>
        <div class="description">
            You have <? echo $locks; ?> connected to the cloud system.
        </div>
    </div>
    <?
    }
    ?>
    
    <?
    if ($integrationtoaccounting) {
    ?>
    
    <div class="row">
        <div class="type">
            Accountingsystem
        </div>
        <div class="count">
            1
        </div>
        <div class="price">
            <? echo $accountinglicense." ".$currency." / Month"; ?>
        </div>
        <div class="description">
            Your PMS will be connected with an accounting system.
        </div>
    </div>
    <?
    }
    ?>
    
    <div class="row" style='font-weight: bold; color: green; font-size: 20px;'>
        <div class="type">
            Total monthly cost
        </div>
        <div class="count">
            
        </div>
        <div class="price" style="font-weight: bold; color: green;">
            <? echo $totalMonthly." ".$currency." / Month"; ?>
        </div>
        <div class="description">
            
        </div>
    </div>
    
    <?
    if ($discountMonthlyTotal) {
    ?>
    <div class="row">
        <div class="type">
            Discount ( <? echo $_GET['discountlicense']; ?> % )
        </div>
        <div class="count">
            
        </div>
        <div class="price">
            <? echo "- ".$discountMonthlyTotal." ".$currency." / Month"; ?>
        </div>
        <div class="description">
            * Discount is given based upon the current selection, removing features, functions etc might affect the discount
        </div>
    </div>
    
    <div class="row" style='font-weight: bold; color: green; font-size: 20px;'>
        <div class="type">
            Total monthly cost after discount
        </div>
        <div class="count">
            
        </div>
        <div class="price">
            <? echo ($totalMonthly - $discountMonthlyTotal)." ".$currency." / Month"; ?>
        </div>
        <div class="description">
            
        </div>
    </div>
    <?
    }
    ?>
</div>

<?

?>
 

<div style='border: solid 1px; margin-top: 100px; margin: 30px; padding: 20px;'>
    If you have any questions regarding this offer you can contact us at post@getshop.com or by phone +47 33 20 08 08
    
    <br/>
    <br/> We wish you a happy day and looking forward to hear from you.
</div>