<?php

/*
    this file is supposed to be run via a cronjob and cli; can be run from
    browser as well though, if immediate updates are needed
*/

//Cron to save activity list in database
include_once ('config.php');

// Create connection
$conn = new mysqli($servername, $username, $password);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

//api call
$url = "https://api.zaui.io/v1/";
$input_xml = '<?xml version="1.0" encoding="UTF-8"?>
<ActivityListRequest xmlns="http://toursgds.com/api/01">  
	<ApiKey>' . $api_key . '</ApiKey>     
	<ResellerId>' . $reseller_id . '</ResellerId>    
	<SupplierId>' . $supplier_id . '</SupplierId>
	<Timestamp>' . time() . '</Timestamp>
</ActivityListRequest>
';

$ch = curl_init();
curl_setopt($ch, CURLOPT_URL, $url);
curl_setopt($ch, CURLOPT_POSTFIELDS,
    $input_xml);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 300);
$data = curl_exec($ch);
curl_close($ch);
//end api call

//remove old list
$sql = "TRUNCATE TABLE {$zaui_dbname}.activity_list";
if ($conn->query($sql) === TRUE) {
    echo "Table truncated successfully";
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}
//save new list
$sql = "INSERT INTO {$zaui_dbname}.activity_list (xml_response, created_at) VALUES ('" . $data . "', '" . time() . "')";

if ($conn->query($sql) === TRUE) {
    echo "New record created successfully";
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}

$conn->close();

//print_r("<pre>");
//print_r($activities_xml);
//print_r('</pre>');
//exit();