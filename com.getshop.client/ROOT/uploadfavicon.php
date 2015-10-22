<?php
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$request_body = file_get_contents('php://input');
$request_body = substr($request_body, strpos($request_body, "base64")+6);
$request_body = base64_decode($request_body);
$fileId = \FileUpload::storeFile($request_body);

$factory->getApi()->getStoreManager()->setImageIdToFavicon($fileId);
?>

