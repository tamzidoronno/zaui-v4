<?php
header('Access-Control-Allow-Origin: *');
$content = file_get_contents("../../../apps/GslBooking/template/gslfront_1.phtml");
echo $content;
?>