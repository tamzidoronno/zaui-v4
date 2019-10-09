<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

if (!isset($_GET['token'])) {
    echo "Missing token as a GET varible, please specify your token";
    return;
}

$hasAccess = $factory->getApi()->getExternalPosManager()->hasAccess($_GET['token']);

if (!$hasAccess) {
    echo "Access denied";
    return;
}

if (!isset($_GET['action']) || !$_GET['action']) {
    echo "Dont know what to do, please specify one of the following action";
    echo "<br/> &action=fetchproducts - List all products available";
    echo "<br/> &action=roomlist - List all guests available for posting";
    return;
}

$action = $_GET['action'];

if ($action == "fetchproducts") {
    $retList = $factory->getApi()->getExternalPosManager()->getProducts($_GET['token']);
    echo json_encode($retList);
    return;
}

if ($action == "roomlist") {
    $retList = $factory->getApi()->getExternalPosManager()->getRoomList($_GET['token']);
    echo json_encode($retList);
    return;
}

if ($action == "debitroom") {
    $entityBody = file_get_contents('php://input');
    $data = json_decode($entityBody);
    
    echo $factory->getApi()->getExternalPosManager()->addToRoom($_GET['token'], $_GET['roomid'], $data);
    return;
}

if ($action == "creditroom") {
    $factory->getApi()->getExternalPosManager()->removeTransaction($_GET['token'], $_GET['transactionid']);
    return;
}

if ($action == "addtransactionrow") {
    $factory->getApi()->getExternalPosManager()->addEndOfDayTransaction($_GET['token'], $_GET['batchid'], $_GET['accountnumber'], $_GET['transactionamount']);
    return;
}

echo "Unkown action, please check you action parameter";