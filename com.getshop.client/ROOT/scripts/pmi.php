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
        $day['code'] = "notsure";
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
    $filter = new core_pmsmanager_PmsOrderStatsFilter();
    $filter->start = $appBase->convertToJavaDate(strtotime($_GET['start'] . " 00:00"));
    $filter->end = $appBase->convertToJavaDate(strtotime($_GET['end'] . " 23:59"));
    $filter->includeVirtual = true;
    $filter->displayType = "dayslept";
    $filter->priceType = "extaxes";
    
    $res = $factory->getApi()->getPmsInvoiceManager()->generateStatistics($_GET['domain'], $filter);
    $list = array();
    
    foreach($res->entries as $s) {
        $revenue = round(array_sum((array)$s->priceEx));
        
        $day = array();
        $day['propertyid'] = $storeId;
        $day['transactiondate'] = date("d.m.Y", strtotime($s->day));
        $day['department'] = "unkown";
        $day['revenue'] = $revenue;
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
        $day['code'] = "notsure";
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