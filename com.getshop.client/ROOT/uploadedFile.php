<?php
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$file = $factory->getApi()->getUtilManager()->getFile($_GET['id']);

$content = base64_decode($file->dataObject);

header('Content-Description: File Transfer');
header('Content-Type: ' . $file->dataType);
header('Content-Disposition: attachment; filename='.basename($file->filename));

echo $content;

?>
