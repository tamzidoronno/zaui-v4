<?php
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$ecom = $factory->getApi()->getStoreApplicationPool()->getApplication("9de54ce1-f7a0-4729-b128-b062dc70dcce");
$ecomsettings = $factory->getApplicationPool()->createInstace($ecom);
$res = $ecomsettings->getConfigurationSetting("paymentLinkMethod");
$storeId = $factory->getStore()->id;
if(!$res) {
    echo "No payment method has been set correctly.";
    return;
}
$roomBookingId = $_GET['id'];

$engine = "default";
if($storeId == "a152b5bd-80b6-417b-b661-c7c522ccf305") { $engine = "demo"; } //Fast Hotel Svolver
if($storeId == "3b647c76-9b41-4c2a-80db-d96212af0789") { $engine = "demo"; } //Fast Hotel Havna
if($storeId == "e625c003-9754-4d66-8bab-d1452f4d5562") { $engine = "demo"; } //Fast Hotel Lofoten

if($_GET['goto-redirect-url']){
    $_SESSION['goto-redirect-url'] = rawurldecode($_GET['goto-redirect-url']);
}


$multipleIds = (array)$factory->getApi()->getPaymentManager()->getMultiplePaymentMethods();
if(sizeof($multipleIds) > 1 && !isset($_GET['chosenpaymentmethod'])) {
    include("choosepaymentmethod.php");
    return;
}

if (isset($_GET['start']) && isset($_GET['end'])) {
    $javaStart = date('c', strtotime($_GET['start']));
    $javaEnd = date('c', strtotime($_GET['end']));
    $orderId = $factory->getApi()->getPmsInvoiceManager()->autoCreateOrderForBookingAndRoomBetweenDates($engine, $roomBookingId, $res, $javaStart, $javaEnd);
} else {
    $orderId = $factory->getApi()->getPmsInvoiceManager()->autoCreateOrderForBookingAndRoom($engine, $roomBookingId, $res);
}


if($orderId) {
    $factory->getApi()->getOrderManager()->changeOrderTypeByCheckout($orderId, $_GET['chosenpaymentmethod']);
    
    echo "Please wait...";
    header("location:/?changeGetShopModule=cms&page=cart&payorder=". $orderId);
} else {
    echo "We where not able to find your payment, please contact us.";
}
?>