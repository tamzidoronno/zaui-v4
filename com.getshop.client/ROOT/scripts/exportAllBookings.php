<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$login = $factory->getApi()->getUserManager()->logOn($_GET['username'], $_GET['password']);
if(!$login) {
    echo "Not logged on";
    return;
}
$reservations = $factory->getApi()->getHotelBookingManager()->getAllReservations();
echo json_encode($reservations);
?>