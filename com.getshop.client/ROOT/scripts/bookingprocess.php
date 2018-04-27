<?php
if(isset($_GET['sessionid'])) {
//    echo "Setting session id : " . $_GET['sessionid'];
    session_id($_GET['sessionid']);
}

chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();


$storeId = $factory->getStore()->id;
$domain = "default";

//lofoten.booking.fasthotels.no, havna.booking.fasthotels.no, svolver.booking.fasthotels.no
if($storeId == "e625c003-9754-4d66-8bab-d1452f4d5562" || $storeId == "a152b5bd-80b6-417b-b661-c7c522ccf305" || $storeId == "3b647c76-9b41-4c2a-80db-d96212af0789") {
    $domain = "demo";
}

if(isset($_GET['paymetmethodnames'])) {
    $paymentApps = (array)$factory->getApi()->getStoreApplicationPool()->getActivatedPaymentApplications();
    $result = array();
    foreach ($paymentApps as $id => $type) {
        $instance = $factory->getApplicationPool()->createInstace($type);
        if (method_exists($instance, "getName")) {
            $result[$type->id] = $instance->getName();
        }
    }
    echo json_encode($result);
    return;
}


$method = $_GET['method'];
if(isset($_GET['body'])) {
    $data = json_encode($_GET['body']);
    $data = str_replace("\u200e", "", $data);
    $_GET['body'] = json_decode($data, true);
    $result = $factory->getApi()->getPmsBookingProcess()->$method($domain, $_GET['body']);
} else {
    $result = $factory->getApi()->getPmsBookingProcess()->$method($domain);
}
if(isset($_GET['dump'])) {
    echo "<pre>";
    print_r($result);
    echo "</pre>";
} else {
    echo $_GET['callback'] . '(' . json_encode($result) . ")";
}
?>