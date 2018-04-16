<?php
header('Content-Type: application/pdf');
header('Content-Disposition: inline; filename="'.$_POST['filename'].'"');
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
header('Content-Type: application/pdf');
$data = $factory->getApi()->getGetShop()->getBase64EncodedPDFWebPageFromHtml($_POST['data']);
echo base64_decode($data);