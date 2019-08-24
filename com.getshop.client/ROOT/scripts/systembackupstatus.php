<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$id = $_GET['id'];
if(isset($_GET['startrecovery'])) {
    $ip = $_GET['ip'];
    $password = $_GET['password'];
    $factory->getApi()->getGetShop()->startRecoveryForUnit($id, $ip, $password);
} else if(isset($_GET['recoverymessage'])) {
    $factory->getApi()->getGetShop()->setRecoveryStatusForUnit($id, $_GET['recoverymessage']);
} else {
    echo $factory->getApi()->getGetShop()->getRecoveryStatusForUnit($id);
}
?>