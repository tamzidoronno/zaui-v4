<?php

chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$factory->getApi()->getUserManager()->logOn($_GET['username'], $_GET['password']);
$routeId = isset($_GET["routeId"]) ? $_GET["routeId"] : "";


$ids = $factory->getApi()->getTrackAndTraceManager()->getRouteIdsThatHasNotCompleted();
$retData = array();
foreach ($ids as $id) {
    $routes = $factory->getApi()->getTrackAndTraceManager()->getRoutesById($id);
    $routeData = new stdClass();
    $routeData->routeId = $id;
    $routeData->logs = $routes[0]->driverLogs;
    $retData[] = $routeData;
}

echo json_encode($retData);
?>