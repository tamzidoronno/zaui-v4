<?php
chdir("../");
error_log("Date" . str_replace(" ", "+", $_GET['date']));
$date_formatted = str_replace(" ", "+", $_GET['date']);

error_log("sCRIPT RUN. Date" . $_GET['date']);
error_log("Date" . $_GET['date']);
error_log("Date" . $_GET['date']);
include '../loader.php';
$factory = IocContainer::getFactorySingelton(false);
$content = $factory->getApi()->getOrderManager()->getBalanceInDetails($_GET['accountId'], $date_formatted);
header('Content-Disposition: attachment; filename=balanceIn_"' . $_GET['accountId'] ."_"  . '.csv"');
header('Content-Type: text/plain');
echo $content;