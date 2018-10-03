<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$factory->getApi()->getGetShop()->loadEhfCompanies();
?>
