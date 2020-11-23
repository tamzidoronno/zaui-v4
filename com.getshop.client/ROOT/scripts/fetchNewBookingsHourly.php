<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$factory->getApi()->getUserManager()->logOn("wubook@getshop.com", "fasfsda211");

$factory->getApi()->getWubookManager()->addNewBookingsPastDays("default", 2);
$factory->getApi()->getWubookManager()->updatePrices("default");
$factory->getApi()->getWubookManager()->checkForNoShowsAndMark("default");
$factory->getApi()->getWubookManager()->doubleCheckDeletedBookings("default");
?>
