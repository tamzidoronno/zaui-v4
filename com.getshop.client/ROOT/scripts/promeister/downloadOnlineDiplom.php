<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

header('Content-type: application/pdf');
header('Content-Disposition: attachment; filename="kursintyg.pdf"');

chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$id = session_id();
session_write_close();

$url = "http://$_SERVER[HTTP_HOST]/scripts/promeister/createOnlineDiplomaSe.php?id=".  session_id()."&packageId=".$_GET['packageId']."&userid=".$_GET['userid'];

$base64 = $factory->getApi()->getGetShop()->getBase64EncodedPDFWebPage($url);
echo base64_decode($base64);
?>