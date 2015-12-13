<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$login = $factory->getApi()->getUserManager()->logOn($_GET['username'], $_GET['password']);
if(!$login) {
    echo "Not logged on";
    return;
}

if($_GET['type'] == "orders") {
    $orders = $factory->getApi()->getOrderManager()->getOrders(null, null, null);
    echo json_encode($orders);
}
if($_GET['type'] == "users") {
    $orders = $factory->getApi()->getUserManager()->getAllUsers();
    echo json_encode($orders);
}

