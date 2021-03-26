<?php

header('Access-Control-Allow-Origin: *');
header("Access-Control-Allow-Credentials: true");
header('Access-Control-Allow-Methods: GET, PUT, POST, DELETE, OPTIONS');
header('Access-Control-Max-Age: 1000');
header('Access-Control-Allow-Headers: Origin, Content-Type, X-Auth-Token , Authorization');

chdir("../../");
include '../loader.php';

include ('../apps/GslBooking/GslZaui.php');

//call some check + render here...
print_r('<pre>');
print_r($activities);
print_r('</pre>');
echo "asssssss";
//echo "here comes the integration";

?>