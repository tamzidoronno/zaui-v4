<?php

chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$factory->getApi()->getUserManager()->logOn($_GET['username'], $_GET['password']);
$routeId = isset($_GET["routeId"]) ? $_GET["routeId"] : "";

$routes = $factory->getApi()->getTrackAndTraceManager()->getRoutesById($routeId);
if (!$routes) {
    echo "Route not found";
    die();
}

echo json_encode($routes[0]->driverLogs);

?>