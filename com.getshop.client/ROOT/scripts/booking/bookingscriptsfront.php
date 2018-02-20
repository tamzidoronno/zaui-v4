<?php
header('Content-Type: application/javascript');
$content = file_get_contents("../../../apps/GslBookingFront/javascript/GslBookingFront.js");
echo $content;
?>
