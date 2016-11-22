<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$result = $factory->getApi()->getSedoxProductManager()->syncFromMagento($_GET['userid']);

$suser = $factory->getApi()->getSedoxProductManager()->getSedoxUserAccountById($_GET['userid']);
$user = $factory->getApi()->getUserManager()->getUserById($_GET['userid']);


echo "<pre>";
print_r($user);
print_r($suser);
echo "</pre>";
?>