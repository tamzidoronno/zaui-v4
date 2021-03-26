<?php

// do some stuff here...
// keep all stuff related to API calls and everything here...

$url = "https://api.zaui.io/v1/";
$input_xml = '<?xml version="1.0" encoding="UTF-8"?>
<ActivityListRequest xmlns="http://toursgds.com/api/01">  
	<ApiKey>8bf6895a9a5a92b932d3c0aa9b24a8c7ba0b10d498983cea8eef17f35f2fb95b
	</ApiKey>     
	<ResellerId>1436</ResellerId>    
	<SupplierId>200</SupplierId>       
	<Timestamp>1577121674745</Timestamp>
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

$activities_xml = json_decode(json_encode(simplexml_load_string($data)), true);
$activities = $activities_xml['Tour'];

foreach($activities as $activity){
    $prod_code = $activity['SupplierProductCode'];
    $activity['ToursAvailability'] = [];

    $url = "https://api.zaui.io/v1/";

    $input_xml = '<?xml version="1.0" encoding="UTF-8"?>
<BatchCheckAvailabilityRequest xmlns="https://api.zaui.io/api/01">
	<ApiKey>8bf6895a9a5a92b932d3c0aa9b24a8c7ba0b10d498983cea8eef17f35f2fb95b</ApiKey>
	<ResellerId>1436</ResellerId>
	<SupplierId>200</SupplierId>
	<ExternalReference>10051374722992616</ExternalReference>
	<Timestamp>1577121674745</Timestamp>
	<StartDate>2021-03-26</StartDate>
	<SupplierProductCode>' . $prod_code . '</SupplierProductCode>
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

    foreach ($availability['BatchTourAvailability'] as $available){
        $tour = [
            'Availability' => gettype($available) == "array" ? $available['AvailabilityStatus']['Status'] : "UNAVAILABLE",
            'TourDepartureTime' => gettype($available) == "array" ? $available['TourOptions']['TourDepartureTime'] : null,
        ];
        array_push($activity['ToursAvailability'], $tour);
    }
}


?>