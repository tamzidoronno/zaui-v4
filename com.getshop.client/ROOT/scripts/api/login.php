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

chdir('../../');
include '../loader.php';

$factory = IocContainer::getFactorySingelton(false);

$event = $_REQUEST;

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