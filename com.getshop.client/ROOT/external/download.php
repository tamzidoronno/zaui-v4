<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$productId = $_POST['productId'];
$files =  [];
$files[] = $_POST['fileid'];

$base64EncodedFile = $factory->getApi()->getSedoxProductManager()->purchaseProduct($productId, $files);
echo $base64EncodedFile;
?>