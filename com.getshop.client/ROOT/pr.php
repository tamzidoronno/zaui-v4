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

$engine = "default";
if($engine == "a152b5bd-80b6-417b-b661-c7c522ccf305") { $engine = "demo"; } //Fast Hotel Svolver
if($engine == "3b647c76-9b41-4c2a-80db-d96212af0789") { $engine = "demo"; } //Fast Hotel Havna
if($engine == "e625c003-9754-4d66-8bab-d1452f4d5562") { $engine = "demo"; } //Fast Hotel Lofoten

$orderId = $factory->getApi()->getPmsInvoiceManager()->autoCreateOrderForBookingAndRoom($engine, $roomBookingId, $res);
if($orderId) {
    echo "Please wait...";
    header("location:/?page=cart&payorder=". $orderId);
} else {
    echo "We where not able to find your payment, please contact us.";
}
?>
