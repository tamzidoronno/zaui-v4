<?php
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$ecom = $factory->getApi()->getStoreApplicationPool()->getApplication("9de54ce1-f7a0-4729-b128-b062dc70dcce");
$ecomsettings = $factory->getApplicationPool()->createInstace($ecom);
$res = $ecomsettings->getConfigurationSetting("paymentLinkMethod");
if(!$res) {
    echo "No payment method has been set correctly.";
    return;
}
$roomBookingId = $_GET['id'];

$orderId = $factory->getApi()->getPosManager()->autoCreateOrderForBookingAndRoom($roomBookingId, $res);
if($orderId) {
    echo "Please wait...";
    header("location:/?page=cart&payorder=". $orderId);
} else {
    echo "We where not able to find your payment, please contact us.";
}
?>
