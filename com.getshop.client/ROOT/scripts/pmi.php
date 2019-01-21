<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
header('Content-Type: application/json');

$factory->getApi()->getUserManager()->logOn($_GET['username'], $_GET['password']);
$storeId = $factory->getApi()->getStoreManager()->getMyStore()->id;
$appBase = new ApplicationBase();
$segments = $factory->getApi()->getPmsCoverageAndIncomeReportManager()->getSegments($_GET['domain']);
$closeDate = strtotime($factory->getApi()->getOrderManager()->getOrderManagerSettings()->closedTilPeriode);

if($_GET['type'] == "roomrevenue") {
    $filter = new core_pmsmanager_PmsBookingFilter();
    $filter->startDate = $appBase->convertToJavaDate(strtotime($_GET['start'] . " 00:00"));
    $filter->endDate = $appBase->convertToJavaDate(strtotime($_GET['end'] . " 23:59"));
    $filter->removeAddonsIncludedInRoomPrice = true;
    $list = array();
    
    foreach($segments as $segment) {
        //Coverage + virtual orders
        $filter->segments = array();
        $filter->segments[] = $segment->id;

        $stats = $factory->getApi()->getPmsManager()->getStatistics($_GET['domain'], $filter);
        
        foreach($stats->entries as $s) {
            if(!$s->date) { 
                continue;
            }
            
            if (strtotime($s->date) >= $closeDate) {
                continue;
            }
            
            $day = array();
            $day['propertyid'] = $storeId;
            $day['date'] = date("d.m.Y", strtotime($s->date));
            $day['segment'] = $segment->name;
            $day['roomnights'] = $s->roomsRentedOut;
            $day['guestnights'] = $s->guestCount;
            $day['guestnightsadults'] = $s->guestCount;
            $day['guestnightschildren'] = 0;
            $day['revenuerooms'] = $s->totalPrice;
            $day['arrivals'] = $s->arrivals;
            $day['departures'] = $s->departures;
            $list[] = $day;
        }
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

    $list = array();
    foreach($segments as $segment) {
        //Coverage + virtual orders
        $filter->segments = array();
        $filter->segments[] = $segment->id;
        $filter->includeVirtual = false;
        $filter->removeAddonsIncludedInRoomPrice = true;
        
        $stats = $factory->getApi()->getPmsManager()->getStatistics($_GET['domain'], $filter);
    
        foreach($stats->entries as $s) {
            if(!$s->date) { 
                continue;
            }
            $day = array();
            $day['propertyid'] = $storeId;
            $day['snapshotdate'] = date("d.m.Y", time());
            $day['date'] = date("d.m.Y", strtotime($s->date));
            $day['segment'] = $segment->name;
            $day['roomnights'] = $s->roomsRentedOut;
            $day['guestnights'] = $s->guestCount;
            $day['guestnightsadults'] = $s->guestCount;
            $day['guestnightschildren'] = 0;
            $day['revenuerooms'] = $s->totalForcasted;
            $day['arrivals'] = $s->arrivals;
            $day['departures'] = $s->departures;
            $list[] = $day;
        }
    }
    
    echo json_encode($list);
}

?>