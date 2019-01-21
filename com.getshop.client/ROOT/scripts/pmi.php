<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
header('Content-Type: application/json');

$factory->getApi()->getUserManager()->logOn($_GET['username'], $_GET['password']);
$storeId = $factory->getApi()->getStoreManager()->getMyStore()->id;
$appBase = new ApplicationBase();
$segments = $factory->getApi()->getPmsCoverageAndIncomeReportManager()->getSegments($_GET['domain']);

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
    $filter = new core_pmsmanager_PmsOrderStatsFilter();
    $filter->start = $appBase->convertToJavaDate(strtotime($_GET['start'] . " 00:00"));
    $filter->end = $appBase->convertToJavaDate(strtotime($_GET['end'] . " 23:59"));
    $filter->includeVirtual = true;
    $filter->displayType = "dayslept";
    $filter->priceType = "extaxes";
    
    $res = $factory->getApi()->getPmsInvoiceManager()->generateStatistics($_GET['domain'], $filter);
    $list = array();
    
    foreach($res->entries as $s) {
        $revenue = 0.0;
        foreach($s->priceExOrders as $productId => $orders) {
            $revenue = array_sum((array)$orders);
            $revenue = round($revenue, 2);
            $day = array();
            $day['propertyid'] = $storeId;
            $day['transactiondate'] = date("d.m.Y", strtotime($s->day));
            $day['department'] = "";
            $day['productName'] = $factory->getApi()->getProductManager()->getProduct($productId)->name;
            $day['productId'] = $productId;
            $day['revenue'] = $revenue;
            $list[] = $day;
        }
        
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