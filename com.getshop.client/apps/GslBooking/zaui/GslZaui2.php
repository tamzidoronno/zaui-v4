<?php

// do some stuff here...
// keep all stuff related to API calls and everything here...

$activity_list = file_get_contents('activity_list.xml');
$activity_list = json_decode(json_encode(simplexml_load_string($activity_list)), true);
$timestamp = strtotime($activity_list['Timestamp']);

if((time()-$timestamp) > 300) {

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

    file_put_contents("activity_list.xml", $data);

    $activities_xml = json_decode(json_encode(simplexml_load_string($data)), true);
    $activities = $activities_xml['Tour'];
} else {
    $activities = $activity_list['Tour'];
}

$short_activities = [];
$available_activities = [];

foreach($activities as $k=>$activity){
//    if($k > 2){
//        break;
//    }
    $prod_code = $activity['SupplierProductCode'];
    $act_av = [
        'supplierProductCode' => $prod_code,
        'supplierProductName' => gettype($activity['SupplierProductName']) == "array" && empty($activity['SupplierProductName']) ? null : $activity['SupplierProductName'],
        'tourDescription' => gettype($activity['TourDescription']) == "array" && empty($activity['TourDescription']) ? null : $activity['TourDescription'],
        'image' => null,
        'tours' => [],
    ];

    array_push($short_activities, $act_av);
}

$url = "https://api.zaui.io/v1/";

$input_xml = '<?xml version="1.0" encoding="UTF-8"?>
<BatchCheckAvailabilityRequest xmlns="https://api.zaui.io/api/01">
	<ApiKey>8bf6895a9a5a92b932d3c0aa9b24a8c7ba0b10d498983cea8eef17f35f2fb95b</ApiKey>
	<ResellerId>1436</ResellerId>
	<SupplierId>200</SupplierId>
	<ExternalReference>10051374722992616</ExternalReference>
	<Timestamp>1577121674745</Timestamp>
	<StartDate>2021-03-30</StartDate>
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

foreach($availability['BatchTourAvailability'] as $available){
    $activity_key = null;

    foreach($short_activities as $k=>$sa){
        if($sa['supplierProductCode'] == $available['SupplierProductCode']){
            $activity_key = $k;
            break;
        }
    }

    if(!is_null($activity_key)){
        $activity = $short_activities[$activity_key];

        $tour = [
            'availabilityStatus' => $available['AvailabilityStatus']['Status'],
            'tourDepartureTime' => $available['TourOptions']['TourDepartureTime']
        ];
        array_push($short_activities[$activity_key]['tours'], $tour);
    }
}

foreach ($short_activities as $sa){
    if(!empty($sa['tours'])){
        array_push($available_activities, $sa);
    }
}
//print_r("<pre>");
//print_r($activities);
//print_r('</pre>');
//exit();

?>