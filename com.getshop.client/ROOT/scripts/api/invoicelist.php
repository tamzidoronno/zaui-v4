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

$invoiceingoverduelist = new ns_b7fb195b_8cea_4d7b_922e_dee665940de2\InvoicingOverdueList();
$list = $invoiceingoverduelist->getAllInvoices();

$rows = array();
foreach($list as $order) {
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
?>
