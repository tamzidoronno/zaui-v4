<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$webaddresses = $factory->getApi()->getGetShop()->getWebAddressesForNewUseTaxes($_GET['password']);

print_r($webaddresses);
foreach ($webaddresses as $webaddr) {
    echo "https://$webaddr/scripts/fixtaxcorrectionproblems.php?password=asd9f92asdfasdfaw4r5154jnhasjdkfnasfd<br/>";
}

