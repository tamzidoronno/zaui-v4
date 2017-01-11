<?
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$route = $factory->getApi()->getTrackAndTraceManager()->getRoutesById("2121 Tue 12/06/2016");
echo json_encode($route);
?>
