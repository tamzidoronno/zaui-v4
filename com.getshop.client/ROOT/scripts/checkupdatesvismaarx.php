<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$factory->getApi()->getHotelBookingManager()->checkForWelcomeMessagesToSend();
$factory->getApi()->getHotelBookingManager()->checkForVismaTransfer();
$factory->getApi()->getHotelBookingManager()->checkForArxTransfer();
?>
