<?php
include '../loader.php';

$factory = IocContainer::getFactorySingelton();

$file = $_FILES['file'];
$name = $file['name'];
$type = $file['type'];
$tmp_name = $file['tmp_name'];
$size = $file['size'];

$listId = $_GET['listid'];

$data = file_get_contents($tmp_name);
$fileId = \FileUpload::storeFile($data);

$entry = new core_filemanager_FileEntry();
$entry->id = $fileId;
$entry->name = $file['name'];
$entry->type = $file['type'];
$entry->size = $file['size'];

$factory->getApi()->getFileManager()->addFileEntry($listId, $entry);

?>