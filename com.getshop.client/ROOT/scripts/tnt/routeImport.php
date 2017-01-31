<?php
chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$user = $factory->getApi()->getUserManager()->logOn($_GET['username'], $_GET['password']);


if (!$user) {
    echo "ACCESS DENIED";
    return;
}

if (!isset($_FILES['content'])) {
    ?>
    <form action="routeImport.php?username=<? echo $_GET['username']; ?>&password=<? echo $_GET['password']; ?>" method="post" enctype="multipart/form-data">
        Select image to upload:
        <input type="file" name="content" id="fileToUpload">
        <input type="submit" value="Upload Image" name="submit">
    </form>
    <?
} else {
    $content = file_get_contents($_FILES['content']['tmp_name']);
    $factory->getApi()->getTrackAndTraceManager()->loadData($content, $_FILES['content']['name']);
}