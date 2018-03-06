<?php
header('Content-Type: application/javascript');
$content = file_get_contents("../../../apps/GslBooking/GslBookingInject.js");
echo $content;
?>