<?php
header('Content-Type: application/pdf');
header('Content-Disposition: inline; filename="courses.pdf"');

chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$id = session_id();
session_write_close();

header('Content-Type: application/pdf');

$webaddress = $_SERVER['SERVER_NAME'];
$url = "http://$webaddress/scripts/promeister/downloadCoursesPdf.php";

$base64 = $factory->getApi()->getGetShop()->getBase64EncodedPDFWebPage($url);
echo base64_decode($base64);

?>