<?php
chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$factory->getApi()->getUserManager()->logOn($_GET['username'], $_GET['password']);
$routeId = isset($_GET["routeId"]) ? $_GET["routeId"] : "";

$data = $factory->getApi()->getTrackAndTraceManager()->getAllExportedDataForRoute($routeId);
echo json_encode($data);
?>