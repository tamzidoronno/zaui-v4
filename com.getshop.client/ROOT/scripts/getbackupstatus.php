<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$url = "http://system.3.0.local.getshop.com/scripts/systembackupstatus.php?id=" . $_GET['id'];
if($this->getApi()->getStoreManager()->isProductMode()) {
    $url = "https://system.getshop.com/scripts/systembackupstatus.php?id=" . $_GET['id'];
}
$content = file_get_contents($url);
echo $content;
?>
