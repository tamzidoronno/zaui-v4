<?php
header('Access-Control-Allow-Origin: *');

chdir("../");
include '../loader.php';

$factory = IocContainer::getFactorySingelton(false);

$event = $_POST;

if (isset($event['username']) && isset($event['password'])) {
   $factory->getApi()->getUserManager()->logOn(json_decode($event['username']), json_decode($event['password']));
}

$config = new ConfigReader();

$commhelper = new CommunicationHelper();
$commhelper->port = $config->getConfig("port");
$commhelper->sessionId = session_id();
$commhelper->host = $config->getConfig("backenddb");
$commhelper->connect();

if (!isset($event['args'])) {
    $event['args'] = array();
}

$result = $commhelper->sendMessage($event);
if ($commhelper->errorCodes) {
    echo json_encode($commhelper->errorCodes);
    http_response_code(400);
    die();
}
echo json_encode($result);
?>