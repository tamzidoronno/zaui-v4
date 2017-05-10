<?php

/* 
 * How does this work?
 * 
 * http://trackandtrace.getshop.com/scripts/tnt/routeDriverChange.php?username={username}&password={password}&routeId={routeId}&userId={driverId}
 */

chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$factory->getApi()->getUserManager()->logOn($_GET['username'], $_GET['password']);

$user = $factory->getApi()->getUserManager()->getUserById($_GET['userId']);
if (!$user) {
    echo "Driver not found";
    http_response_code(400);
    die();
}

$route = $factory->getApi()->getTrackAndTraceManager()->getRoutesById($_GET['routeId']);
if (!$route) {
    echo "No route found to be deleted";
    http_response_code(400);
    die();
}


if ($_GET['add'] == "1") {
    $factory->getApi()->getTrackAndTraceManager()->addDriverToRoute($user->id, $route[0]->id);
} else {
    $factory->getApi()->getTrackAndTraceManager()->removeDriverToRoute($user->id, $route[0]->id);
}

echo "success";