<?php
if(!isset($_GET['readable'])) {
    header('Content-type: application/pdf');
    header('Content-Disposition: attachment; filename="bestilling.pdf"');
}

chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$orderId = "";
if (isset($_GET['orderId'])) {
    $orderId = $_GET['orderId'];
}
$bookingid = "";
if(isset($_GET['bookingid'])) {
    $bookingid = $_GET['bookingid'];
}
$lang = "";
if(isset($_GET['language'])) {
    $lang = $_GET['language'];
}

$id = session_id();
$engine = $_GET['engine'];
$address = $_SERVER['REQUEST_SCHEME'] . '://' . $_SERVER['HTTP_HOST']."/scripts/loadContract.php?engine=".$engine."&bookingid=".$bookingid."&sessid=$id" ."language=".$lang;


if(isset($_GET['bookingid']) && $_GET['bookingid']) {
    $_SESSION['contractLoaded'] = $factory->getApi()->getPmsManager()->getContract($_GET['engine'], $_GET['bookingid']);
} else if(isset($_GET['language'])) {
    $lang = $_GET['language'];
    if($lang == "no") {
        $lang = "nb_NO";
    }
    if($lang == "en") {
        $lang = "en_en";
    }
    $_SESSION['contractLoaded'] = $factory->getApi()->getPmsManager()->getContractByLanguage($_GET['engine'], $lang);
} else {
    $_SESSION['contractLoaded'] = $factory->getApi()->getPmsManager()->getCurrenctContract($_GET['engine']);
}
session_write_close();

if(!isset($_GET['readable'])) {
    $base64 = $factory->getApi()->getGetShop()->getBase64EncodedPDFWebPage($address);
    echo base64_decode($base64);
} else {
    echo $_SESSION['contractLoaded'];
}
?>
