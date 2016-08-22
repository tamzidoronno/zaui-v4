<?php
header('Content-Type: application/pdf');
header('Content-Disposition: inline; filename="bestilling.pdf"');

chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

header('Content-Type: application/pdf');
$id = session_id();
session_write_close();

$webaddress = $factory->getStore()->webAddressPrimary;
$url = "http://$webaddress/renderSingelApplication.php?&id=".$_GET['id'];

$base64 = $factory->getApi()->getGetShop()->getBase64EncodedPDFWebPage($url);
echo base64_decode($base64);
?>