<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$id = $_GET['id'];
if(isset($_GET['startrecovery'])) {
    $ip = $_GET['ip'];
    $password = $_GET['password'];
    $factory->getApi()->getGetShop()->startRecoveryForUnit($id, $ip, $password);
} else if(isset($_GET['canStartDownload'])) {
    echo $factory->getApi()->getGetShop()->canStartRestoringUnit($id);
} else if(isset($_GET['recoveryCompleted'])) {
    echo $factory->getApi()->getGetShop()->recoveryCompleted($id);
} else if(isset($_GET['downloadfile'])) {
    $factory->getApi()->getGetShop()->setRecoveryStatusForUnit($id, "Preparing backup");
    $ip = $factory->getApi()->getGetShop()->getIpForUnitId($id);
    $content = file_get_contents("http://10.0.7.10:8800/backups/$ip.tar.gz");
    $factory->getApi()->getGetShop()->setRecoveryStatusForUnit($id, "Uploading backup");
    echo $content;
    $factory->getApi()->getGetShop()->setRecoveryStatusForUnit($id, "Backup has been uploaded");
} else if(isset($_GET['recoverymessage'])) {
    $factory->getApi()->getGetShop()->setRecoveryStatusForUnit($id, $_GET['recoverymessage']);
} else {
    echo $factory->getApi()->getGetShop()->getRecoveryStatusForUnit($id);
}
?>