<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton(false);
header("Content-type: application/json; charset=utf-8");
$result = $factory->getApi()->getGdsManager()->getMessages($_GET['token']);
echo json_encode($result);
?>