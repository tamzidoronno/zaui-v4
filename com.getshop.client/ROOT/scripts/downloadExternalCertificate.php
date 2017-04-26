<?php

chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();


$mans = $factory->getApi()->getEventBookingManager()->getManuallyAddedEvents("booking", $_GET['userId']);
$found = false;

foreach ($mans as $man) {
    foreach ($man->files as $file) {
        if ($file->fileId === $_GET['id']) {
            $found = $file;
            break;
        }
    }
}

if (!$found) {
    echo "Did not find file";
    die();
}

$filename = "../uploadedfiles/" . $_GET['id'];
if (!file_exists($filename)) {
    echo "File does not exists";
    exit(0);
}

header('Pragma: public');  // required
header('Expires: 0');  // no cache
header("Content-type:". $file->contentType);
header("Content-disposition: filename='".$file->fileName . "'");

echo file_get_contents($filename);
?>