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

echo json_encode($available_activities);

//echo "here comes the integration";

?>