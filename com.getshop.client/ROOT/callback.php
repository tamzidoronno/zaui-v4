<?php
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$cartManager = new ns_900e5f6b_4113_46ad_82df_8dafe7872c99\CartManager();
if(isset($_GET['useapp']) && $_GET['useapp'] == "netaxept") {
    $transId = json_decode(file_get_contents("php://input"), true);
    $transId = $transId['TransactionId'];
    
    foreach ($cartManager->getPaymentApplications() as $paymentApp) {
        if($paymentApp->applicationSettings->appName == "Netaxept") {
            $netaxept = $paymentApp;
        }
    }
    
    if(isset($_GET['TransactionId'])) {
        $transId = $_GET['TransactionId'];
    }
    $netaxept->handleCallBack($transId);
    return;
}
if(isset($_GET['usepaymentmethod1'])) {
    $pmethod1 = new ns_7587fdcb_ff65_4362_867a_1684cbae6aef\PaymentMethod1();
    $pmethod1->paymentCallback();
}

$data = http_build_query($_GET);
if(!$data) {
    header("location:/");
}

if (!isset($_GET['app'])) {
    return;
}

$id = $_GET['app'];
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