<?php

chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$factory->getApi()->getUserManager()->logOn($_GET['username'], $_GET['password']);
$route = $factory->getApi()->getTrackAndTraceManager()->deleteRoute($_GET["routeId"]); //"2121 Tue 12/06/2016");