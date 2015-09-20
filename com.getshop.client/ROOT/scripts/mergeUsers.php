<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$factory->getApi()->getUserManager()->mergeUsers("52176229-8c6a-4626-bb7c-68b622999598", ['9ee1cbc7-0ccb-4ea4-a872-1cdb71855e18']);
$factory->getApi()->getUserManager()->mergeUsers("96063867-ee76-417d-aec6-ae592556b67b", ['428d315f-e5f1-4eac-800a-49b2ab2bcafe']);
?>