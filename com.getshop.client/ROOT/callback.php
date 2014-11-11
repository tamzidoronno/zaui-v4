<?php
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$data = http_build_query($_GET);

if (!isset($_GET['app'])) {
    return;
}

$id = $_GET['app'];
$application = $factory->getApplicationPool()->getApplicationInstance($id);

if(isset($_GET['callback_method'])) {
    call_user_method($_GET['callback_method'], $application);
} else {
    if ($application != null) {
        $application->paymentCallback();
    }
}

?>