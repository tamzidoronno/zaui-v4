<?php
chdir("../");
error_log("Date" . str_replace(" ", "+", $_GET['date']));
$date_formatted = str_replace(" ", "+", $_GET['date']);
include '../loader.php';
$factory = IocContainer::getFactorySingelton(false);
$content = $factory->getApi()->getOrderManager()->getBalanceInDetails($_GET['accountId'], $date_formatted);
$filename="balanceIn_" .$_GET['accountId']  . '.csv';
header("Content-Disposition: attachment; filename={$filename}");
header('Content-Type: text/plain');
echo $content;