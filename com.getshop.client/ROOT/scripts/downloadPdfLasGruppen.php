<?php

header('Content-type: application/pdf');
header('Content-Disposition: attachment; filename="bestilling.pdf"');

chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$id = session_id();
session_write_close();

$base64 = $factory->getApi()->getUtilManager()->getBase64EncodedPDFWebPage(\ns_7004f275_a10f_4857_8255_843c2c7fb3ab\LasGruppenOrderSchema::$url."/scripts/generatePdfLasgruppen.php?id=$id");
echo base64_decode($base64);
?>