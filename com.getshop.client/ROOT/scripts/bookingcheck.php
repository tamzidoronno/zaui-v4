<?php

chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();


$names = $factory->getApi()->getStoreManager()->getMultiLevelNames();
foreach ($names as $name) {
    $factory->getApi()->getBookingEngine()->checkConsistency($name);
}