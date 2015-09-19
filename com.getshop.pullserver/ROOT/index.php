<?php
$actual_link = "http://$_SERVER[HTTP_HOST]$_SERVER[REQUEST_URI]";
$startKeyWord = "pullserver-";
$storeId = substr($actual_link, strpos($actual_link, $startKeyWord)+strlen($startKeyWord));
$storeId = substr($storeId, 0, strpos($storeId, "."));
$firstSplit = explode(".", $actual_link);
if (count($firstSplit) > 0) {
    $args = explode("_", $firstSplit[0]);
    if (count($args) != 3) {
        die("Invalid url");
    }
} else {
    die("Invalid url");
}


$key = $args[1];
$storeId = $args[2];

session_start();
include '../events/API2.php';
include 'loader.php';

$api = new GetShopApi(25554, 'localhost', session_id());
$api->getStoreManager()->initializeStore("pullserver.getshop.com", session_id());
$api->getUserManager()->logOn("post@getshop.com", "g4kkg4kk");

$pullMessage = new core_pullserver_data_PullMessage();
$pullMessage->body = file_get_contents("php://input");
$pullMessage->getVariables = json_encode($_GET);
$pullMessage->postVariables = json_encode($_POST);
$pullMessage->keyId = $key;
$pullMessage->belongsToStore = $storeId;
$api->getPullServerManager()->savePullMessage($pullMessage);
?>