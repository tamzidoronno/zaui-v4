<?php
/**
 * This file is based on api.php form the parent folder. 
 * 
 * the major change here is that this file does NOT support login but expects a valid session to be provided. that session will have to be setup after
 * valid authentication in login.php
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

/*
 * look for a GSSID GetShopSessionID
 */
$gssid = '';
$clientheaders = getallheaders();

//adhere to https://tools.ietf.org/html/rfc6750#section-2.1
if( isset($clientheaders['Authorization']) )
{
    $gssid = str_replace('Bearer:','', $clientheaders['Authorization'] );
    $gssid = trim($gssid);
}
else if( $_REQUEST['GSSID'] )
{
    //fallback for testing
    $gssid = $_REQUEST['GSSID'];
}

if($gssid != '')
{
    session_id( $gssid );
    session_start();

    if( $_SESSION['authenticated'] )
    {

        // we assume a valid and authenticated user here
        $request = file_get_contents('php://input');
        $event = $_POST;
        try{
            $json = json_decode($request, true);
            $event = $json;
        } catch (Exception $e) {
        
        }

        $event['sessionId'] = $gssid;

        $config = new ConfigReader();
        
        $commhelper = new CommunicationHelper();
        $commhelper->port = $config->getConfig('port');
        $commhelper->sessionId = session_id();
        $commhelper->host = $config->getConfig('backenddb');
        $commhelper->connect();
        
        //login to log us out!
        $factory = IocContainer::getFactorySingelton(false);
        $logon = $factory->getApi()->getUserManager()->logonUsingToken($gssid);
        $factory->getApi()->getUserManager()->logout();
        $_SESSION['authenticated'] = false;

        die('{"result":"ok"}');

    }
}
header('HTTP/1.1 401 Unauthorized');
echo '<h1>Unauthorized!</h1>';
die();

?>