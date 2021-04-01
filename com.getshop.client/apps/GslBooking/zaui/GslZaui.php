<?php

//Load cached available activities from database
if(isset($_GET['getActivities'])){
    $servername = "localhost";
    $username = "test";
    $password = "test";

// Create connection
    $conn = new mysqli($servername, $username, $password);

// Check connection
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }

//getting activity list
    $sql = "SELECT xml_response FROM getshop_zaui_cache.activity_list LIMIT 1";
    $result = $conn->query($sql);

    if($result->num_rows == 0){
        die("ERROR: Activity list not found!");
    }

    $activities_sql = $result->fetch_assoc()['xml_response'];

    $activities_xml = json_decode(json_encode(simplexml_load_string($activities_sql)), true);
    $activities = $activities_xml['Tour'];

//getting batch availability
    $sql = "SELECT xml_response FROM getshop_zaui_cache.availability LIMIT 1";
    $result = $conn->query($sql);

    if($result->num_rows == 0){
        die("ERROR: Batch availability list not found!");
    }

    $availability_sql = $result->fetch_assoc()['xml_response'];
    $availability_xml = json_decode(json_encode(simplexml_load_string($availability_sql)), true);

    $availability = $availability_xml['BatchTourAvailability'];

    $available_activities = [];
    $date = date("Y-m-d", strtotime($_GET['startDate']));

//checking which activities are available
    foreach($activities as $activity){
        $prod_code = $activity['SupplierProductCode'];
        $act_av = [
            'supplierProductCode' => $prod_code,
            'supplierProductName' => gettype($activity['SupplierProductName']) == "array" && empty($activity['SupplierProductName']) ? null : $activity['SupplierProductName'],
            'tourDescription' => gettype($activity['TourDescription']) == "array" && empty($activity['TourDescription']) ? null : $activity['TourDescription'],
            'image' => null,
            'tours' => null,
        ];

        foreach($availability as $available){
            if($available['SupplierProductCode'] == $prod_code && $available['Date'] == $date){
                $act_av['tours'] = true;
                array_push($available_activities, $act_av);
                break;
            }
        }
    }
}
//end loading cached

//check availability call after Show more buttons is pressed on front end
if(isset($_GET['checkAvailability']) && isset($_GET['prodCode']) && isset($_GET['adults']) && isset($_GET['children'])){
    $prod_code = $_GET['prodCode'];
    $adults = $_GET['adults'];
    $children = $_GET['children'];
    $total = (int)$adults + (int)$children;

    $url = "https://api.zaui.io/v1/";

    $date = date("Y-m-d", strtotime($_GET['startDate']));

    $input_xml = '<?xml version="1.0" encoding="UTF-8"?>
<CheckAvailabilityRequest xmlns="https://api.zaui.io/api/01">
	<ApiKey>8bf6895a9a5a92b932d3c0aa9b24a8c7ba0b10d498983cea8eef17f35f2fb95b</ApiKey>     
	<ResellerId>1436</ResellerId>    
	<SupplierId>200</SupplierId>       
	<Timestamp>' . time() . '</Timestamp>
	<StartDate>' . $date . '</StartDate>
	<SupplierProductCode>' . $prod_code .  '</SupplierProductCode>
	<TourOptions>
		<SupplierOptionCode></SupplierOptionCode>
	</TourOptions>
	<TravellerMix>
		<Senior></Senior>
		<Adult>' . $adults . '</Adult>
		<Student></Student>
		<Child>' . $children . '</Child>
		<Infant></Infant>
		<Total>' . $total . '</Total>
	</TravellerMix>
	<PickupLocation>
		<SupplierPickupCode></SupplierPickupCode>
	</PickupLocation>	
	<DropoffLocation>
		<SupplierDropoffCode></SupplierDropoffCode>
	</DropoffLocation>	
</CheckAvailabilityRequest>
';

    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_POSTFIELDS,
        $input_xml);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
    curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 300);
    $data = curl_exec($ch);
    curl_close($ch);

    $tours_xml = json_decode(json_encode(simplexml_load_string($data)), true);
    $tours = $tours_xml['TourAvailability'];

    echo json_encode($tours);
    exit();
}
//End check availability

//end loading available activities
//print_r("<pre>");
//print_r($available_activities);
//print_r('</pre>');
//exit();

