<?php
chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$factory->getApi()->getUserManager()->logOn($_GET['username'], $_GET['password']);

$routes = $factory->getApi()->getTrackAndTraceManager()->getRoutesCompletedPast24Hours();

if (!count($routes)) {
    echo "No routes completed within the past 24 hours";
    die();
}



$retRoutes = array();

foreach ($routes as $route) {
    $stdClass = new \stdClass();
    $stdClass->startLat = $route->startInfo->lat;
    $stdClass->startLon = $route->startInfo->lon;
    $stdClass->endLat = $route->completedInfo->completedLat;
    $stdClass->endLon = $route->completedInfo->completedLon;
    $stdClass->routeId = $route->id;
    $stdClass->driverId = $route->startInfo->startedByUserId;
    $stdClass->completedTime = date('m/d/Y H:i', strtotime($route->completedInfo->completedTimeStamp));
    $stdClass->startedTime = date('m/d/Y H:i', strtotime($route->startInfo->startedTimeStamp));
    $stdClass->deliveryServiceDate = date('m/d/Y H:i', strtotime($route->deliveryServiceDate));;
    $retRoutes[] = $stdClass;
}

echo json_encode($retRoutes);
?>
