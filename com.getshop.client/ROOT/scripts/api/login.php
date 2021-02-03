<?php
/**
 * This file is based on api.php form the parent folder. all it is supposed to do is login the user and return its data or an error.
 */

//some custom response headers
header('Access-Control-Allow-Origin: *');
header("Access-Control-Allow-Credentials: true");
header('Access-Control-Allow-Methods: GET, PUT, POST, DELETE, OPTIONS');
header('Access-Control-Max-Age: 1000');
header('Access-Control-Allow-Headers: Origin, Content-Type, X-Auth-Token , Authorization');

// we are an API endpoint. enable CORS
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    if (isset($_SERVER['HTTP_ACCESS_CONTROL_REQUEST_HEADERS']))
        header("Access-Control-Allow-Headers: {$_SERVER['HTTP_ACCESS_CONTROL_REQUEST_HEADERS']}");
    exit(0);
}

chdir('../../');
include '../loader.php';

$factory = IocContainer::getFactorySingelton(false);


$request = file_get_contents('php://input');
$event = false;
try{
    $event = json_decode($request, true);
} catch (Exception $e) {

}

if(!$event) $event = $_REQUEST;

if (isset($event['username']) && isset($event['password'])) {
   $logon = $factory->getApi()->getUserManager()->logOn($event['username'], $event['password']);
   if(!$logon) {
       echo "Failed logon";
       header("HTTP/1.1 401 Unauthorized");
       return;
   }

   $logon->sessionId = session_id();

   $_SESSION['authenticated'] = true;
   $_SESSION['username'] = $event['username'];

   ob_clean();
   echo json_encode($logon);
   die("\n");
}

header("HTTP/1.1 401 Unauthorized");
echo "Go away";
die();

?>