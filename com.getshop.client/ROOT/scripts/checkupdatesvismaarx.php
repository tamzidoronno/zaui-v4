<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$user = $factory->getApi()->getUserManager()->logOn($_GET['user'], $_GET['password']);
$factory->getApi()->getHotelBookingManager()->checkForWelcomeMessagesToSend();

$orders = $factory->getApi()->getOrderManager()->getOrdersToCapture();
//print_r($orders);
foreach($orders as $order) {
    $app = new \ns_def1e922_972f_4557_a315_a751a9b9eff1\Netaxept();
    $app->order = $order;
    $app->setOrderId($order->id);
    $app->collectOrder();
}

echo "done";
?>
