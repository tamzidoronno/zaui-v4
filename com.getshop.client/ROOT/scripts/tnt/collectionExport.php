<?
chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$factory->getApi()->getUserManager()->logOn($_GET['username'], $_GET['password']);
$start = date("M d, Y h:i:s A", strtotime($_GET['from']));
$end = date("M d, Y h:i:s A", strtotime($_GET['to']));

$res = $factory->getApi()->getTrackAndTraceManager()->getCompletedCollectionTasks($start, $end); //"2121 Tue 12/06/2016");
echo json_encode($res);
?>
