<?
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$factory->getApi()->getUserManager()->logOn($_GET['username'], $_GET['password']);
$route = $factory->getApi()->getTrackAndTraceManager()->getExport($_GET["routeId"]); //"2121 Tue 12/06/2016");
echo json_encode($route);
?>
