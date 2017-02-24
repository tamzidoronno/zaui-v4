<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$user = $factory->getApi()->getUserManager()->getUserById($_GET['userId']);

$fileToUse = null;
foreach($user->files as $file) {
    if($file->fileId == $_GET['id']) {
        $fileToUse = $file;
    }
}

if(!$fileToUse) {
    echo "File does not exists";
    exit(0);
}

$filename = "../uploadedfiles/" . $fileToUse->fileId;
if (!file_exists($filename)) {
    echo "File does not exists";
    exit(0);
}

header('Pragma: public');  // required
header('Expires: 0');  // no cache
header("Content-type:". $fileToUse->contentType);
header("Content-disposition: filename='".$fileToUse->fileName . "'");

echo file_get_contents($filename);
?>