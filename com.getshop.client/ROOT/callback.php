<?php
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$id = $_GET['app'];
$application = $factory->getApplicationPool()->getApplicationSetting($id);
 
$appcreate = "\\ns_".str_replace("-", "_", $application->id)."\\".$application->appName;

$app = new $appcreate();
$app->paymentCallback();
 
?>