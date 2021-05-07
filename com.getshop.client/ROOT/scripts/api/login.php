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

   // fill empty access which mean access to everything in the old solution....
   if(isset( $logon->pmsPageAccess ) && count( $logon->pmsPageAccess ) == 0 ){
       $logon->pmsPageAccess = [ "home","a90a9031-b67d-4d98-b034-f8c201a8f496",
           "048e2e10-1be3-4d77-a235-4b47e3ebfaab","0da68de9-da08-4b60-9652-3ac456da2627",
           "afe687b7-219e-4396-9e7b-2848f5ed034d","394bb905-8448-45c1-8910-e9a60f8aebc5",
           "e03b19de-d1bf-4d1c-ac40-8c100ef53366","4f66aad0-08a0-466c-9b4c-71337c1e00b7",
           "checklist","messages","getshopsupport" ];
   }
   if(isset( $logon->hasAccessToModules ) && count( $logon->hasAccessToModules ) == 0 ){
       $logon->hasAccessToModules = [ "cms","pms","pmsconference","salespoint","crm",
            "apac","settings","account","invoice","express" ];
   }

   ob_clean();
   echo json_encode($logon);
   die("\n");
}

header("HTTP/1.1 401 Unauthorized");
echo "Go away";
die();

?>