<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$method = $_GET['method'];
$data = json_encode($_GET['body']);
$domain = "default";
$result = $factory->getApi()->getPmsBookingProcess()->$method($domain, $_GET['body']);
if(isset($_GET['dump'])) {
    echo "<pre>";
    print_r($result);
    echo "</pre>";
} else {
    echo $_GET['callback'] . '(' . json_encode($result) . ")";
}
?>