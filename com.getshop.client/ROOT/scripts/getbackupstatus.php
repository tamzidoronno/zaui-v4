<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$url = "http://system.3.0.local.getshop.com/scripts/systembackupstatus.php?id=" . $_GET['id'];
if($factory->isProductionMode()) {
    $url = "https://system.getshop.com/scripts/systembackupstatus.php?id=" . $_GET['id'];
}
$content = file_get_contents($url);
echo $content;
?>