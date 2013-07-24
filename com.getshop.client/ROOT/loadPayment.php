<?php
session_id($_GET['session_id']);
include '../loader.php';

$id = $_GET['id'];
$factory = IocContainer::getFactorySingelton();

$factory->loadJavascriptFiles();
$application = $factory->getApplicationPool()->getApplicationInstance($id);
$application->order = $factory->getApi()->getOrderManager()->getOrder($_GET['orderid']);
$application->preProcess();
?>
