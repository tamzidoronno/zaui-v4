<?php
header('Access-Control-Allow-Origin: *');
$content = file_get_contents("../../../apps/GslBookingFront/template/gslbookingfront_1.phtml");
echo $content;
?>