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

$url = "http://$_SERVER[HTTP_HOST]/scripts/promeister/createEventDiplomas.php?id=".  session_id()."&eventId=".$_GET['eventId'];
$base64 = $factory->getApi()->getGetShop()->getBase64EncodedPDFWebPage($url);
echo base64_decode($base64);
?>