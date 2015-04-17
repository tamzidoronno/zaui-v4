<?php

include '../loader.php';

$response = new core_getshop_data_SmsResponse();
$response->id = $_GET['messageId'];
$response->errCode = $_GET['err-code'];
$response->deliveryTime = date('M d, Y h:m:s A', strtotime($_GET['message-timestamp']));

$factory = IocContainer::getFactorySingelton();
$factory->getApi()->getGetShop()->saveSmsCallback($response);

$myfile = fopen("/tmp/clickatelltest.txt", "w") or die("Unable to open file!");
fwrite($myfile, json_encode($_GET));
fclose($myfile);
?>
