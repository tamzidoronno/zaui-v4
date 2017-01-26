<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$user = $factory->getApi()->getUserManager()->logOn($_GET['username'], $_GET['password']);
$content = file_get_contents($_FILES['content']['tmp_name']);

if (!$user) {
    echo "ACCESS DENIED";
}

$factory->getApi()->getTrackAndTraceManager()->loadData($content, $_FILES['content']['name']);