<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$factory->getApi()->getUserManager()->logOn("wubook@getshop.com", "fasfsda211");
$factory->getApi()->getWubookManager()->fetchNewBookings("default");

?>
