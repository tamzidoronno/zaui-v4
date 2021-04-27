<?php
/*
    this file is supposed to be run via a cronjob and cli; can be run from
    browser as well though, if immediate updates are needed
*/


include_once ('config.php');

// Create connection
$conn = new mysqli($servername, $username, $password);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

//api call
$url = "https://api.zaui.io/v1/";

$startDate = date('Y-m-d', time());
$six_months = time() + 89*86400;
$endDate = date('Y-m-d', $six_months);

$input_xml = '<?xml version="1.0" encoding="UTF-8"?>
<BatchCheckAvailabilityRequest xmlns="https://api.zaui.io/api/01">
<ApiKey>' . $api_key . '</ApiKey>     
<ResellerId>' . $reseller_id . '</ResellerId>    
<SupplierId>' . $supplier_id . '</SupplierId>
<ExternalReference>10051374722992616</ExternalReference>
<Timestamp>' . time() . '</Timestamp>
<StartDate>' . $startDate . '</StartDate>
<EndDate>' . $endDate . '</EndDate>
</BatchCheckAvailabilityRequest>
';

$ch = curl_init();
curl_setopt($ch, CURLOPT_URL, $url);
curl_setopt($ch, CURLOPT_POSTFIELDS,
    $input_xml);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 300);
$data = curl_exec($ch);
curl_close($ch);

$availability = json_decode(json_encode(simplexml_load_string($data)), true);
//end api call

//delete old list
$sql = "TRUNCATE TABLE getshop_zaui_cache.availability";
if ($conn->query($sql) === TRUE) {
    echo "Table truncated successfully \n";
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}

//save new list
$sql = "INSERT INTO getshop_zaui_cache.availability (xml_response, created_at) VALUES ('" . $data . "', '" . time() . "')";

if ($conn->query($sql) === TRUE) {
    echo "New record created successfully \n";
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}

$conn->close();

