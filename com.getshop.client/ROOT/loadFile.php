<?php
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$fileId = $_GET['fileid'];

$entry = $factory->getApi()->getFileManager()->getFile($fileId);

header("content-type:" . $entry->type);
header("Content-disposition: filename=".$entry->name);

$filename = "../uploadedfiles/" . $fileId;
echo file_get_contents($filename);
?>