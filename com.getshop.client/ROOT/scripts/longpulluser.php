<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton(false);
header("Content-type: application/json; charset=utf-8");

$factory->getApi()->getUserManager()->logOn("post@getshop.com", "gakkgakk");
$result = $factory->getApi()->getGdsManager()->getMessageForUser();
echo json_encode($result);
$factory->getApi()->getUserManager()->logout();
?>