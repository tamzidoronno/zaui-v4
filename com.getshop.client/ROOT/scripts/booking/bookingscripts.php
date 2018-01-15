<?php
header('Content-Type: application/javascript');
$content = file_get_contents("../../../apps/GslBooking/javascript/GslBooking.js");
echo $content;
?>