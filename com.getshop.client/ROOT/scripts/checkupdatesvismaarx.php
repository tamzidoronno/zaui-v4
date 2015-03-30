<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$factory->getApi()->getHotelBookingManager()->checkForOrdersToGenerate();
$factory->getApi()->getHotelBookingManager()->checkForWelcomeMessagesToSend();
echo "done";
?>
