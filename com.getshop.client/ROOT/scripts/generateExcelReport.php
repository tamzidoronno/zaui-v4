<?php
include 'excel.php';
chdir("../");
include '../loader.php';

$factory = IocContainer::getFactorySingelton();
$year = $_GET['year'];
$bookingstats = array();
$sales = array();

$type = $_GET['type'];

$xls = new Excel($type . "_report_" . $year);
$numberOfRooms = 49;
    
$xls->home();
$xls->label("Periode");
$xls->right();
$xls->label("Credit card");
$xls->right();
$xls->label("Invoice");
$xls->right();
$xls->label("Total");
$xls->right();
$xls->label("Number of night");
$xls->right();
$xls->label("Availability");
$xls->right();
$xls->label("Covered (%)");
$xls->down();

$interval = 12;


if($type == "weekly") {
    $interval = 52;
}
if($type == "daily") {
    $interval = 365;
}
$startYear = strtotime("01.01." . $year);

for($month = 1; $month <= $interval; $month++) {
    if($type == "monthly") {
        if($month < 10) {
            $ļabel = "0" . $month . "." . $year;
        } else {
            $ļabel = $month . "." . $year;
        }

        $stats = $factory->getApi()->getHotelBookingManager()->getStatistics($year,$month,null, null);
        $days = $month == 2 ? ($year % 4 ? 28 : ($year % 100 ? 29 : ($year % 400 ? 28 : 29))) : (($month - 1) % 7 % 2 ? 30 : 31); 
        $creditcard = $factory->getApi()->getOrderManager()->getTotalSalesAmount($year,$month,null,null, "ns_def1e922_972f_4557_a315_a751a9b9eff1\Netaxept");
        $invoice = $factory->getApi()->getOrderManager()->getTotalSalesAmount($year,$month,null,null, "ns_70ace3f0_3981_11e3_aa6e_0800200c9a66\InvoicePayment");
    }  else if($type == "weekly") {
        $days = 7;
        $ļabel = date("d.m.y", strtotime($year."W".str_pad($month,2,"0",STR_PAD_LEFT)));
        $creditcard = $factory->getApi()->getOrderManager()->getTotalSalesAmount($year,null,$month,null, "ns_def1e922_972f_4557_a315_a751a9b9eff1\Netaxept");
        $invoice = $factory->getApi()->getOrderManager()->getTotalSalesAmount($year,null,$month,null, "ns_70ace3f0_3981_11e3_aa6e_0800200c9a66\InvoicePayment");
        $stats = $factory->getApi()->getHotelBookingManager()->getStatistics($year,null,$month, null);
    }  else if($type == "daily") {
        $days = 1;
        $ļabel = date("d.m.y", ($startYear+(($month-1)*86400)));
        $creditcard = $factory->getApi()->getOrderManager()->getTotalSalesAmount($year,null,null,$month, "ns_def1e922_972f_4557_a315_a751a9b9eff1\Netaxept");
        $invoice = $factory->getApi()->getOrderManager()->getTotalSalesAmount($year,null,null, $month, "ns_70ace3f0_3981_11e3_aa6e_0800200c9a66\InvoicePayment");
        $stats = $factory->getApi()->getHotelBookingManager()->getStatistics($year,null, null,$month);
    }


    $totalnights = $days*$numberOfRooms;
    $nights = $stats->numberOfNights;

    $percentage = round(($nights / $totalnights)*100, 2);

    $xls->home();
    $xls->label($ļabel);
    $xls->right();
    $xls->label($creditcard);
    $xls->right();
    $xls->label($invoice);
    $xls->right();
    $xls->label($invoice+$creditcard);
    $xls->right();
    $xls->label($nights);
    $xls->right();
    $xls->label($totalnights);
    $xls->right();
    $xls->label($percentage);
    $xls->down();
}
 $xls->send();
?>