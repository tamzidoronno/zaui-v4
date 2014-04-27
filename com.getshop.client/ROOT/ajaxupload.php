<?php
include '../loader.php';

$file = $_FILES['FileInput'];
$tmpname = $file['tmp_name'];
$name = $file['name'];
$type = $file['type'];

if(!file_exists("/thundashop/files")) {
    mkdir("/thundashop/files");
}

$factory = IocContainer::getFactorySingelton();

$content = file_get_contents($tmpname);
$content = base64_encode($content);

$dbobj = new core_utilmanager_data_FileObject();
$dbobj->filename = $name;
$dbobj->dataType = $type;
$dbobj->dataObject = $content;

$id = $factory->getApi()->getUtilManager()->saveFile($dbobj);


$result = array();
$result['id'] = $id;
$result['name'] = $name;
echo json_encode($result);
?>
