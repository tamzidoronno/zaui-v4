<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$method = $_GET['method'];
$data = json_decode($_GET['body']);

$domain = "default";
$result = $factory->getApi()->getPmsBookingProcess()->$method($domain, $data);
if(isset($_GET['dump'])) {
    echo "<pre>";
    print_r($result);
    echo "</pre>";
} else {
    echo $_GET['callback'] . '(' . json_encode($result) . ")";
}
?>