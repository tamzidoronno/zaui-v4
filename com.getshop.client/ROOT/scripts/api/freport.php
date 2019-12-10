<?php
chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$timezone = $factory->getStore()->timeZone;
if($timezone) {
    date_default_timezone_set($timezone);
}

if(isset($_GET['username'])) {
    $username = $_GET['username'];
    $password = $_GET['password'];
    $login = $factory->getFactory()->getApi()->getUserManager()->logOn($username, $password);
    if(!$login) {
        echo "Login failed.";
        return;
    }
}

$start = date("c", strtotime($_GET['start'] . " 00:00"));
$end = date("c", strtotime($_GET['end'] . " 00:00"));
$accountFinReport = new \ns_e6570c0a_8240_4971_be34_2e67f0253fd3\AccountFinanceReport();
$rows = $accountFinReport->getFreportRows($start, $end);

header('Content-Type: application/json; charset=utf-8');
echo json_encode($rows);

?>