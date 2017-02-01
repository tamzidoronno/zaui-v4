<?php
chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$user = $factory->getApi()->getUserManager()->logOn($_GET['username'], $_GET['password']);


if (!$user) {
    echo "ACCESS DENIED";
    return;
}

if (!isset($_FILES['file'])) {
    ?>
    <form action="routeImport.php?username=<? echo $_GET['username']; ?>&password=<? echo $_GET['password']; ?>" method="post" enctype="multipart/form-data">
        Select image to upload:
        <input type="file" name="file" id="fileToUpload">
        <input type="submit" value="Upload Image" name="submit">
    </form>
    <?
} else {
    $file = file_get_contents($_FILES['file']['tmp_name']);
    $factory->getApi()->getTrackAndTraceManager()->loadData($file, $_FILES['file']['name']);
}
?>