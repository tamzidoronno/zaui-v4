<?php

/*
 * This file is used for downloading tuningfiles for SedoxProductViewer
 */
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$product = $factory->getApi()->getSedoxProductManager()->getProductById($_SESSION['sedox_current_productid']);
$productName = $product->brand." ".$product->model." ".$product->engineSize." ".$product->power." ".$product->year." ".$product->originalChecksum;

header("Content-Type: application/octet-stream");

header("Pragma: public");
header("Expires: 0");
header("Cache-Control: must-revalidate, post-check=0, pre-check=0");
header("Cache-Control: public");
header("Content-Description: File Transfer");
header("Content-type: application/octet-stream");
header("Content-Disposition: attachment; filename=$productName.zip");
header("Content-Transfer-Encoding: binary");

$fileArray = explode(":-:", base64_decode(urldecode($_GET['files'])));
$zipFileBase64 = $factory->getApi()->getSedoxProductManager()->purchaseProduct($_SESSION['sedox_current_productid'], $fileArray);
$file = base64_decode($zipFileBase64);
echo $file;
?>