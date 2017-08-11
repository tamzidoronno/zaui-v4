<?php

ob_start();
include('downloadCoursesPdf.php');
$content = ob_get_contents();
ob_end_clean();


$content = base64_encode($content);
header('Content-Type: application/pdf');
$base64 = $factory->getApi()->getGetShop()->getBase64EncodedPDFWebPageFromHtml($content);
echo base64_decode($base64);
?>