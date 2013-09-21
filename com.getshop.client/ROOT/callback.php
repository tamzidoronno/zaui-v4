<?php
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

if (!isset($_GET['app'])) {
    return;
}

$id = $_GET['app'];
$application = $factory->getApplicationPool()->getApplicationInstance($id);
if ($application != null) {
    $application->paymentCallback();
}
?>