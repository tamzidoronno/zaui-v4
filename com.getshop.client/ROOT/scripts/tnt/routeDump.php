<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$factory->getApi()->getUserManager()->logOn($_GET['username'], $_GET['password']);

$routeId = isset($_GET["routeId"]) ? $_GET["routeId"] : "";

$routes = $factory->getApi()->getTrackAndTraceManager()->getRoutesById($routeId);
echo json_encode($routes[0]);

?>