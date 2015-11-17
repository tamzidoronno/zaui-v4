<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$factory->getApi()->getUserManager()->logOn($_GET['user'], $_GET['password']);
$factory->getApi()->getHotelBookingManager()->buildRecurringOrders();
$factory->getApi()->getHotelBookingManager()->checkForOrdersToCapture();
?>