<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$factory->getApi()->getUserManager()->logOn($_GET['username'], $_GET['password']);
$codes = $factory->getApi()->getGetShopLockManager()->getCodesInUse($_GET['engine']);
echo ",";
foreach($codes as $code) {
    echo $code . ",";
}
?>