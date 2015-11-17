<?php
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$data = http_build_query($_GET);

if (!isset($_GET['app'])) {
    return;
}

$id = $_GET['app'];
$cartManager = new ns_900e5f6b_4113_46ad_82df_8dafe7872c99\CartManager();
$application = null;

foreach ($cartManager->getPaymentApplications() as $paymentApp) {
    if ($paymentApp->applicationSettings->id == $_GET['app']) {
        $application = $paymentApp;
        break;
    }
}

if(isset($_GET['callback_method'])) {
    call_user_method($_GET['callback_method'], $application);
} else {
    if ($application != null) {
        $application->paymentCallback();
    }
}

?>