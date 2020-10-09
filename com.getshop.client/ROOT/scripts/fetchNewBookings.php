<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$factory->getApi()->getWubookManager()->fetchNewBookings("default");

?>
