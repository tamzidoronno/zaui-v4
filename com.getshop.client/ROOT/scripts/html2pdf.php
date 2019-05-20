<?php
header('Content-Type: application/pdf');
header('Content-Disposition: attachment; filename="'.$_POST['filename'].'"');
header('Content-Transfer-Encoding: binary');
header('Cache-Control: must-revalidate');
header('Pragma: public');


chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
header('Content-Type: application/pdf');
$data = $factory->getApi()->getGetShop()->getBase64EncodedPDFWebPageFromHtml($_POST['data']);
echo base64_decode($data);