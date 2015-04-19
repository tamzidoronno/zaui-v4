<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$user = $factory->getApi()->getUserManager()->logOn($_GET['user'], $_GET['password']);
$factory->getApi()->getHotelBookingManager()->checkForOrdersToGenerate();
$factory->getApi()->getHotelBookingManager()->checkForWelcomeMessagesToSend();
echo "done";
?>
