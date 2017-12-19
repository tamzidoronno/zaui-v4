<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$method = $_GET['method'];
$domain = "default";
if(isset($_GET['body'])) {
    $data = json_encode($_GET['body']);
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