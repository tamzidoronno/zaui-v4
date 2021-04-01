<?php
//Cron to save activity list in database
$servername = "localhost";
$username = "test";
$password = "test";

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
	<ApiKey>8bf6895a9a5a92b932d3c0aa9b24a8c7ba0b10d498983cea8eef17f35f2fb95b
	</ApiKey>     
	<ResellerId>1436</ResellerId>    
	<SupplierId>200</SupplierId>       
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
$sql = "TRUNCATE TABLE getshop_zaui_cache.activity_list";
if ($conn->query($sql) === TRUE) {
    echo "Table truncated successfully";
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}
//save new list
$sql = "INSERT INTO getshop_zaui_cache.activity_list (xml_response, created_at) VALUES ('" . $data . "', '" . time() . "')";

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