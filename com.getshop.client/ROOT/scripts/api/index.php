<?php
/**
 * This file is based on api.php form the parent folder. 
 * 
 * the major change here is that this file does NOT support login but expects a valid session to be provided. that session will have to be setup after
 * valid authentication in login.php
 */

header('Access-Control-Allow-Origin: *');

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
    session_start();
    session_id( $gssid );

    if( $_SESSION['authenticated'] )
    {

        // we assume a valid and authenticated user here
        $event = $_POST;

        $config = new ConfigReader();
        
        $commhelper = new CommunicationHelper();
        $commhelper->port = $config->getConfig("port");
        $commhelper->sessionId = session_id();
        $commhelper->host = $config->getConfig("backenddb");
        $commhelper->connect();
        
        
        if (!isset($event['args'])) {
            $event['args'] = array();
        }
        
        if(isset($event['getshop_json_body'])) {
            $event['args'] = json_decode($event['getshop_json_body']);
            unset($event['getshop_json_body']);
        }
        
        $result = $commhelper->sendMessage($event);
        if ($commhelper->errorCodes) {
            echo json_encode($commhelper->errorCodes);
            http_response_code(400);
            die();
        }
        echo json_encode($result);
        die("\n");
    }
}
header("HTTP/1.1 401 Unauthorized");
echo "Go away";
die();

?>