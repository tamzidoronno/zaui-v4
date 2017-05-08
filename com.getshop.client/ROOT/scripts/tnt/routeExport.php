<?
chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();


    
$factory->getApi()->getUserManager()->logOn($_GET['username'], $_GET['password']);
$start = date("M d, Y h:i:s A", strtotime($_GET['from']));
$end = date("M d, Y h:i:s A", strtotime($_GET['to']));

$routeId = isset($_GET["routeId"]) ? $_GET["routeId"] : "";
$currentState = isset($_GET['currentState']) && $_GET['currentState'] == "true" ? true : false;
$route = $factory->getApi()->getTrackAndTraceManager()->getExportedData($start, $end); //"2121 Tue 12/06/2016");
//$toPrint = array();
//
//if (isset($_GET['orderreference'])) {
//    foreach ($route as $order) {
//        if (trim($order->ORReferenceNumber) == $_GET['orderreference']) {
//            $toPrint[] = $order;            
//        }
//    }
//} else if (isset($_GET['podbarcode'])) {
//    foreach ($route as $order) {
//        if (trim($order->PODBarcodeID) == $_GET['podbarcode']) {
//            $toPrint[] = $order;            
//        }
//    }
//} else {
//    $toPrint = $route;
//}
//echo "<pre>"; print_r($toPrint); echo "</pre>";
echo json_encode($route);
?>
