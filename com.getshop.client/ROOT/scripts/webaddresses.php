<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton(false, false);
$store = $factory->getStore();
echo "Main: ".$store->webAddress;
echo "<br/>Primary: ".$store->webAddressPrimary;
echo "<br/><br/><b>Additional:</b><br>";
foreach ($store->additionalDomainNames as $add) {
    echo $add."<br/>";
}