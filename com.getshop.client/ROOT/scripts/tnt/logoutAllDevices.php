<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$user = $factory->getApi()->getUserManager()->logOn($_GET['username'], $_GET['password']);

if (!$user) {
    echo "ACCESS DENIED";
    http_response_code(403);
    return;
}

$factory->getApi()->getTrackAndTraceManager()->logoutAllDevices();
echo "Message sent";