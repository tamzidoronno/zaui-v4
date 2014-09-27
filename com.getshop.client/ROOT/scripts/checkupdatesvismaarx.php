<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$factory->getApi()->getHotelBookingManager()->checkForVismaTransfer();
$factory->getApi()->getHotelBookingManager()->checkForArxTransfer();
$factory->getApi()->getHotelBookingManager()->checkForWelcomeMessagesToSend();
?>
