<?php

// do some stuff here...
// keep all stuff related to API calls and everything here...
$url = "https://api.zaui.io/v1/";

$input_xml = '<?xml version="1.0" encoding="UTF-8"?>
<BatchCheckAvailabilityRequest xmlns="https://api.zaui.io/api/01">
	<ApiKey>8bf6895a9a5a92b932d3c0aa9b24a8c7ba0b10d498983cea8eef17f35f2fb95b</ApiKey>
	<ResellerId>1436</ResellerId>
	<SupplierId>200</SupplierId>
	<ExternalReference>10051374722992616</ExternalReference>
	<Timestamp>1577121674745</Timestamp>
	<StartDate>2021-03-26</StartDate>
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

print_r("<pre>");
print_r($availability);
print_r("</pre>");
exit();

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
$available_activities = [];

foreach($activities as $k=>$activity){
    if($k > 2){
        break;
    }
    $prod_code = $activity['SupplierProductCode'];
    $act_av = [
        'supplierProductCode' => $prod_code,
        'supplierProductName' => gettype($activity['SupplierProductName']) == "array" && empty($activity['SupplierProductName']) ? null : $activity['SupplierProductName'],
        'tourDescription' => gettype($activity['TourDescription']) == "array" && empty($activity['TourDescription']) ? null : $activity['TourDescription'],
        'image' => null,
        'tours' => [],
    ];

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
        if(gettype($available) == "array" && isset($available['AvailabilityStatus'])){
            $tour = [
                'availabilityStatus' => $available['AvailabilityStatus']['Status'],
                'tourDepartureTime' => $available['TourOptions']['TourDepartureTime']
            ];
            array_push($act_av['tours'], $tour);
        }
    }
    if(!empty($act_av['tours'])){
        array_push($available_activities, $act_av);
    }
}


?>