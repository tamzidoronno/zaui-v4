<?php

/*
 * This file is used for downloading tuningfiles for SedoxProductViewer
 */
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$product = $factory->getApi()->getSedoxProductManager()->getProductById($_SESSION['sedox_current_productid']);
$productName = $product->brand." ".$product->model." ".$product->engineSize." ".$product->power." ".$product->year." ".$product->originalChecksum;

$fileArray = explode(":-:", base64_decode(urldecode($_GET['files'])));
$filename = "$productName.zip";

if(count($fileArray) == 1) {
    
    foreach ($product->binaryFiles as $file) {
        if ($file->id == $fileArray[0]) {
            $filename = $file->id." - ".$productName." ".$file->fileType.".bin";
        }
    }
}


header("Content-Type: application/octet-stream");

header("Pragma: public");
header("Expires: 0");
header("Cache-Control: must-revalidate, post-check=0, pre-check=0");
header("Cache-Control: public");
header("Content-Description: File Transfer");
header("Content-type: application/octet-stream");
header("Content-Disposition: attachment; filename=\"$filename\"");
header("Content-Transfer-Encoding: binary");


$zipFileBase64 = $factory->getApi()->getSedoxProductManager()->purchaseProduct($_SESSION['sedox_current_productid'], $fileArray);
$file = base64_decode($zipFileBase64);
echo $file;
?>