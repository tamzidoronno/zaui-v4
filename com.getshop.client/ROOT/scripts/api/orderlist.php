<?php
chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$timezone = $factory->getStore()->timeZone;
if($timezone) {
    date_default_timezone_set($timezone);
}

$validParameters = ['username','password','date', 'startDate', 'endDate', 'pageNumber', 'pageSize'];

foreach(array_keys($_GET) as $param){
    if(!in_array($param,$validParameters)){
        echo "Invalid Query Parameter";
        return;
    };
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

 $invoiceingoverduelist = new ns_b7fb195b_8cea_4d7b_922e_dee665940de2\InvoicingOverdueList();

if(isset($_GET['date'])) {
    $start = strtotime($_GET['date'].' 00:00:00');
    $end = strtotime($_GET['date'].' 23:59:59');
}
elseif(isset($_GET['startDate']) && isset($_GET['endDate'])) {
    $start = strtotime($_GET['startDate'].' 00:00:00');
    $end = strtotime($_GET['endDate'].' 23:59:59');
}
else {
    echo 'Neither date nor startDate,endDate parameters are provided';
    return;
}

$filterOptions = new \core_common_FilterOptions();
$filterOptions->startDate = date("c", $start);
$filterOptions->endDate = date("c", $end);
$filterOptions->pageNumber = $_GET['pageNumber'];
$filterOptions->pageSize = $_GET['pageSize'];
$filterOptions->removeNullOrders = true;
$filterOptions->extra = array();
$resData = $factory->getFactory()->getApi()->getOrderManager()->getOrdersFiltered($filterOptions);

$rows = array();
foreach($resData->datas as $order) {
    $object = new stdClass();
    $object->id = $order->incrementOrderId;
    $amount = $order->totalAmount;
    $object->amount = $amount;

    $paidAmount = $invoiceingoverduelist->getTotalPaidAmount($order);
    $object->paid = $paidAmount;
    $object->remaining = $amount - $paidAmount;
    
    $object->duedate = date('d.m.Y', strtotime($order->dueDate));
    $object->name = $order->cart->address->fullName;
    $rows[] = $object;
}

header('Content-Type: application/json; charset=utf-8');
echo json_encode($rows);