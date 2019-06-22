<?php
header('Pragma: public');  // required
header('Expires: 0');  // no cache
//header("Content-type:application/pdf");
//header("Content-Disposition:attachment;filename=".$_GET['incrementalOrderId'].".pdf");

chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$order = $factory->getApi()->getOrderManager()->getOrder($_GET['orderId']);
$invoiceData = new \stdClass();
$invoiceData->order = $order;
$invoiceData->accountingDetails = $factory->getApi()->getOrderManager()->getAccountingDetails();
$serializedOrder = json_encode($invoiceData);

include('invoicetemplates/template2.php');
?>