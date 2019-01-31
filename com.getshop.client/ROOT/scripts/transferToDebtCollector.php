<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$appName = "\\" . $_GET['app'];

$app = new $appName();
$app->setOrder($_GET['orderid']);
$tranfsered = $app->transfer();

if($tranfsered) {
    $order = $factory->getApi()->getOrderManager()->getOrder($_GET['orderid']);
    
    $entry = new core_ordermanager_data_OrderShipmentLogEntry();
    $entry->address = $app->getAddress();
    $entry->date = $app->convertToJavaDate(time());
    $entry->type = "debtCollector";
    
    $order->shipmentLog[] = $entry;
    $factory->getApi()->getOrderManager()->saveOrder($order);
}
?>
