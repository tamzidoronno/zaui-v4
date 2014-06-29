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
//If hotelbooking app exists, call it for sending confirmation email.
$instances = $factory->getApplicationPool()->getApplicationsInstancesByNamespace("ns_d16b27d9_579f_4d44_b90b_4223de0eb6f2");
if(sizeof($instances) > 0) {
    $instances[0]->sendConfirmationEmail();
}
?>