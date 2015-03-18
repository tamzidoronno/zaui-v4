<?php

 header('Access-Control-Allow-Origin: *');  

session_start();
ob_start();
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$appBase = new ApplicationBase();

$result = array();
if(isset($_FILES['files'])) {
    foreach($_FILES['files']['tmp_name'] as $name) {
        $data = file_get_contents($name);
        $fileId = \FileUpload::storeFile($data);
        $result[] = $fileId;
    }
    echo json_encode($result);
}
?>