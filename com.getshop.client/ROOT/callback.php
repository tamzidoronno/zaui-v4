<?php
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$data = http_build_query($_GET);
file_put_contents("/tmp/callback", $data);

if (!isset($_GET['app'])) {
    return;
}

$id = $_GET['app'];
$application = $factory->getApplicationPool()->getApplicationInstance($id);
if ($application != null) {
    $application->paymentCallback();
}
?>