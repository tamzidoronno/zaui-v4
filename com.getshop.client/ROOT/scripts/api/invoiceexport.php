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

$incid = $_GET['id'];

$order = $factory->getApi()->getOrderManager()->getOrderByincrementOrderId($incid);
$invoiceingoverduelist = new ns_b7fb195b_8cea_4d7b_922e_dee665940de2\InvoicingOverdueList();

$amount = $invoiceingoverduelist->getTotalAmountForOrder($order);
$paidAmount = $invoiceingoverduelist->getTotalPaidAmount($order);
$user = $factory->getApi()->getUserManager()->getUserById($order->userId);


$result = new stdClass();

$result->invoicenumber = $order->incrementOrderId;
$result->invoiceDate = date("d.m.Y", strtotime($order->rowCreatedDate));
$result->dueDate = date("d.m.Y", strtotime($order->dueDate));
$result->amount = $amount;
$result->paidAmount = $paidAmount;
$result->customerId = $user->customerId;
$result->invoiceNote = $order->invoiceNote;
$result->name = $order->cart->address->fullName;
$result->address1 = $order->cart->address->address;
$result->address2 = $order->cart->address->address2;
$result->postCode = $order->cart->address->postCode;
$result->countryCode = $order->cart->address->countrycode;
$result->currency = $order->currency;
$result->kid = $order->kid;
$result->language = $order->language;
$result->email = $user->emailAddress;
$result->mobile = $user->cellPhone;
if(isset($user->company->vatNumber)) {
    $result->vatNumber = $user->company->vatNumber;
} else {
    $result->vatNumber = "";
}
if(!$result->language) { $result->language = "no"; }
if(!$result->currency) { $result->currency = "nok"; }


$actual_link = (isset($_SERVER['HTTPS']) && $_SERVER['HTTPS'] === 'on' ? "https" : "http") . "://$_SERVER[HTTP_HOST]";


$result->invoicePath = $actual_link."/scripts/downloadInvoice.php?orderId=".$order->id."&incrementalOrderId=".$order->incrementOrderId;


if(!$result->countryCode) {
    $result->countryCode = "no";
}

if(!$result->currency) {
    $result->currency = "nok";
}

header('Content-Type: application/json; charset=utf-8');
echo json_encode($result);
?>