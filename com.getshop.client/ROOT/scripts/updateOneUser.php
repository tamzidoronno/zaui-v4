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
?>