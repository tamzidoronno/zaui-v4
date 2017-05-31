<?php

chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$factory->getApi()->getUserManager()->logOn($_GET['username'], $_GET['password']);

$route = $factory->getApi()->getTrackAndTraceManager()->getRoutesById($_GET['routeId']);
if (!$route) {
    echo "ROUTE_NOT_FOUND";
    die();
}

$factory->getApi()->getTrackAndTraceManager()->deleteRoute($_GET["routeId"]); //"2121 Tue 12/06/2016");
echo "Deleted";