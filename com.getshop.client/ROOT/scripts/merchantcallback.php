<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$storeId = $factory->getStore()->id;

$gets = http_build_query($_GET);
$key = $_GET['key'];

$addr = "http://pullserver_" . $key . "_" . $storeId . ".nettmannen.no?" . $gets;

$ch = curl_init($addr);
curl_setopt($ch, CURLOPT_POST, 1);
curl_setopt($ch, CURLOPT_POSTFIELDS, http_build_query($_POST));
curl_setopt($ch, CURLOPT_FOLLOWLOCATION, 1); 
curl_setopt($ch, CURLOPT_HEADER, 0);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
$response = curl_exec($ch);


//$ch = curl_init($redirect_url);
//
//curl_setopt($ch, CURLOPT_POST, 1);
//curl_setopt($ch, CURLOPT_POSTFIELDS, $body);
//curl_setopt($ch, CURLOPT_FOLLOWLOCATION, 1);
//curl_setopt($ch, CURLOPT_HEADER, 0);
//curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
//$response = curl_exec($ch);
//
//if (!isset($response))
//    return null;
//return $response;

?>