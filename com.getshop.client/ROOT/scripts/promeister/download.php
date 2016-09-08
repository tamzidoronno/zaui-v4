<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

header('Content-type: application/pdf');
header('Content-Disposition: attachment; filename="downloads.pdf"');

chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$id = session_id();
session_write_close();

$generator = "createEventDiplomas";

if ($factory->getStore()->id == "17f52f76-2775-4165-87b4-279a860ee92c") {
    $generator = "createEventDiplomasNo";
}
    
$url = "http://$_SERVER[HTTP_HOST]/scripts/promeister/$generator.php?id=".  session_id()."&eventId=".$_GET['eventId'];

if (isset($_GET['userId'])) {
    $url .= "&userId=".$_GET['userId'];
}
$base64 = $factory->getApi()->getGetShop()->getBase64EncodedPDFWebPage($url);
echo base64_decode($base64);
?>