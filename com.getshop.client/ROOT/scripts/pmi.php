<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
header('Content-Type: application/json');

$factory->getApi()->getUserManager()->logOn($_GET['username'], $_GET['password']);
$storeId = $factory->getApi()->getStoreManager()->getMyStore()->id;
$appBase = new ApplicationBase();

if($_GET['type'] == "roomrevenue") {
    $filter = new core_pmsmanager_PmsBookingFilter();
    $filter->startDate = $appBase->convertToJavaDate(strtotime($_GET['start'] . " 00:00"));
    $filter->endDate = $appBase->convertToJavaDate(strtotime($_GET['end'] . " 23:59"));
    if(isset($_GET['includevirtual']) && $_GET['includevirtual'] == "1") {
        $filter->includeVirtual = true;
    }
    
    //Coverage + virtual orders
    $stats = $factory->getApi()->getPmsManager()->getStatistics($_GET['domain'], $filter);
    $list = array();
    foreach($stats->entries as $s) {
        if(!$s->date) { 
            continue;
        }
        $day = array();
        $day['propertyid'] = $storeId;
        $day['date'] = date("d.m.Y", strtotime($s->date));
        $day['code'] = "";
        $day['segment'] = "Total";
        $day['roomnights'] = $s->roomsRentedOut;
        $day['guestnights'] = $s->guestCount;
        $day['guestnightsadults'] = $s->guestCount;
        $day['guestnightschildren'] = 0;
        $day['revenuerooms'] = $s->totalPrice;
        $day['arrivals'] = $s->arrivals;
        $day['departures'] = $s->departures;
        $list[] = $day;
    }
    
    echo json_encode($list);
}
if($_GET['type'] == "allrevenue") {
    //Income report
    $startDate = $appBase->convertToJavaDate(strtotime($_GET['start'] . " 00:00"));
    $endDate = $appBase->convertToJavaDate(strtotime($_GET['end'] . " 23:59"));
    
    $res = $factory->getApi()->getOrderManager()->getPmiResult($startDate, $endDate);
    $list = array();
    
    foreach($res as $s) {
        $day = array();
        $day['propertyid'] = $s->propertyid;
        $day['transactiondate'] = date("d.m.Y", strtotime($s->transactiondate));
        $day['department'] = $s->department;
        $day['productName'] = $s->productName;
        $day['productId'] = $s->prodcutId;
        $day['revenue'] = round($s->revenue,2);
        $list[] = $day;
    }
    echo json_encode($list);
}
if($_GET['type'] == "reservations") {
    $filter = new core_pmsmanager_PmsBookingFilter();
    $filter->startDate = $appBase->convertToJavaDate(strtotime($_GET['start'] . " 00:00"));
    $filter->endDate = $appBase->convertToJavaDate(strtotime($_GET['end'] . " 23:59"));
    $filter->includeVirtual = true;

    //Coverage + virtual orders
    $stats = $factory->getApi()->getPmsManager()->getStatistics($_GET['domain'], $filter);
    $list = array();
    foreach($stats->entries as $s) {
        if(!$s->date) { 
            continue;
        }
        $day = array();
        $day['propertyid'] = $storeId;
        $day['snapshotdate'] = date("d.m.Y", time());
        $day['date'] = date("d.m.Y", strtotime($s->date));
        $day['code'] = "";
        $day['segment'] = "Total";
        $day['roomnights'] = $s->roomsRentedOut;
        $day['guestnights'] = $s->guestCount;
        $day['guestnightsadults'] = $s->guestCount;
        $day['guestnightschildren'] = 0;
        $day['revenuerooms'] = $s->totalPrice;
        $day['arrivals'] = $s->arrivals;
        $day['departures'] = $s->departures;
        $list[] = $day;
    }
    
    echo json_encode($list);
}

?>