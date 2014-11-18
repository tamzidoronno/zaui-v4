<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$factory->getApi()->getUserManager()->logOn("crontabjob@smh.no", "crontabjob12345");
//$factory->getApi()->getHotelBookingManager()->checkForWelcomeMessagesToSend();
$factory->getApi()->getHotelBookingManager()->checkForVismaTransfer();
//$factory->getApi()->getHotelBookingManager()->checkForArxTransfer();
?>
