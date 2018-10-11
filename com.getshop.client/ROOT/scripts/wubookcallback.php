<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
if(isset($_POST['rcode'])) {
    $rcode = $_POST['rcode'];
}
if(isset($_GET['rcode'])) {
    $rcode = $_GET['rcode'];
}
$factory->getApi()->getWubookManager()->fetchBookingFromCallback("default", $rcode);
file_put_contents("/tmp/wubook", json_encode($_POST) . "----" . json_encode($_GET) . " ---- " . $rcode);
?>