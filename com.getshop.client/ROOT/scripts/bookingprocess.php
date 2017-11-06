<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$method = $_GET['method'];
$data = json_decode($_POST['body']);

$domain = "default";
$result = $factory->getApi()->getPmsBookingProcess()->$method($domain, $data);
if(isset($_GET['dump'])) {
    echo "<pre>";
    print_r($result);
    echo "</pre>";
} else {
    echo json_encode($result);
}
?>