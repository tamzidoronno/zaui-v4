<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
if ($_GET['logtype'] == "CODEUSED") {
    $factory->getApi()->getGetShopLockSystemManager()->addTransactionEntranceDoorByToken($_GET['token'], $_GET['deviceid'], $_GET['codeused']);
}