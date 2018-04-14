<?php
header('Content-Type: application/pdf');
header('Content-Disposition: inline; filename="Price Offer GetShop.pdf"');

chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

header('Content-Type: application/pdf');
$id = session_id();
session_write_close();

//$url = "http://no.3.0.local.getshop.com/".base64_decode($_GET['link'])."&id=".  $id;
$url = "https://no.getshop.com/".base64_decode($_GET['link'])."&id=".  $id;

$base64 = $factory->getApi()->getGetShop()->getBase64EncodedPDFWebPage($url);
echo base64_decode($base64);
?>