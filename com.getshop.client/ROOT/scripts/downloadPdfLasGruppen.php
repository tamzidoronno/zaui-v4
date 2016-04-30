<?php
header('Content-Type: application/pdf');
header('Content-Disposition: inline; filename="bestilling.pdf"');

chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

header('Content-Type: application/pdf');
$id = session_id();
session_write_close();

$orderId = "";
if (isset($_GET['orderId'])) {
    $orderId = $_GET['orderId'];
}
$base64 = $factory->getApi()->getGetShop()->getBase64EncodedPDFWebPage(\ns_7004f275_a10f_4857_8255_843c2c7fb3ab\LasGruppenOrderSchema::$url."/scripts/generatePdfLasgruppen.php?id=$id&orderId=$orderId");
echo base64_decode($base64);
?>