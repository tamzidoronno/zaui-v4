<?php
chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$factory->getApi()->getUserManager()->logOn($_GET['username'], $_GET['password']);

$routes = $factory->getApi()->getTrackAndTraceManager()->getRoutesById($_GET['routeId']);
if (!count($routes)) {
    echo "Route not found, please check id";
    die();
}

$stdClass = new \stdClass();
$stdClass->started = $routes[0]->startInfo->started;
$stdClass->completed = $routes[0]->completedInfo->completed;
$stdClass->completedLat = $routes[0]->completedInfo->completedLat;
$stdClass->completedLon = $routes[0]->completedInfo->completedLon;

echo json_encode($stdClass);
?>