<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$user = $factory->getApi()->getUserManager()->logOn($_GET['user'], $_GET['password']);
$factory->getApi()->getHotelBookingManager()->checkForWelcomeMessagesToSend();

$orders = $factory->getApi()->getOrderManager()->getOrdersToCapture();

$cartManager = new \ns_900e5f6b_4113_46ad_82df_8dafe7872c99\CartManager();
$netAxeptPaymentApplication = null;
foreach ($cartManager->getPaymentApplications() as $paymenti) {
    if ($paymenti->applicationSettings->id === "def1e922-972f-4557-a315-a751a9b9eff1") {
        $netAxeptPaymentApplication = $paymenti;
    }
}

if($netAxeptPaymentApplication != null) {
    foreach($orders as $order) {
        $netAxeptPaymentApplication->order = $order;
        $netAxeptPaymentApplication->setOrderId($order->id);
        $netAxeptPaymentApplication->collectOrder();
    }
}

echo "done";
?>
