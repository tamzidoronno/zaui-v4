<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$login = $factory->getApi()->getUserManager()->logOn($_GET['username'], $_GET['password']);
if(!$login) {
    echo "Not logged on";
    return;
}
$rooms = $factory->getApi()->getHotelBookingManager()->getAllRooms();
echo json_encode($rooms);
?>