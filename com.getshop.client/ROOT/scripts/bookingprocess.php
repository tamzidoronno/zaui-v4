<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$storeId = $factory->getStore()->id;
$domain = "default";
if($storeId == "e625c003-9754-4d66-8bab-d1452f4d5562" || $storeId == "a152b5bd-80b6-417b-b661-c7c522ccf305" || $storeId == "3b647c76-9b41-4c2a-80db-d96212af0789") {
    $domain = "demo";
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