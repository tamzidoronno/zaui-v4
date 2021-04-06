<?php

//Include MySQL and API credentials
include ('config.php');

//Load cached available activities from database
if(isset($_GET['getActivities'])){
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

    echo json_encode($available_activities);
    exit();
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
	<ApiKey>' . $api_key . '</ApiKey>     
	<ResellerId>' . $reseller_id . '</ResellerId>    
	<SupplierId>' . $supplier_id . '</SupplierId>       
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

//Create addon out of activity
if(isset($_GET['createAddon']) && isset($_GET['prodCode']) && isset($_GET['tourDepartureTime']) && isset($_GET['tourPrice'])){
    $prod_code = $_GET['prodCode'];
    $tourDepartureTime = $_GET['tourDepartureTime'];
    $tourPrice = $_GET['tourPrice'];
    $account = 0;

    echo json_encode(['product_id' => "189cec15-67c4-4990-969c-be98eb166ce4"]);
    exit();
    //Create addon
//    $psm = new \ns_c5a4b5bf_365c_48d1_aeef_480c62edd897\PsmConfigurationAddons();
//    $product = $psm->getApi()->getProductManager()->createProductWithAccount($account);
//
//    if($product) {
//        $product->name = $prod_code . " - " . $tourDepartureTime;
//        $product->tag = "addon";
//        $product = $psm->getApi()->getProductManager()->saveProduct($product);
//        $notifications = $psm->getApi()->getPmsManager()->getConfiguration($psm->getSelectedMultilevelDomainName());
//
//        $conf = new \core_pmsmanager_PmsBookingAddonItem();
//        $found = false;
//        foreach($notifications->addonConfiguration as $tmpaddon) {
//            if($tmpaddon->productId == $product->id) {
//                $conf = $tmpaddon;
//                $found = true;
//            }
//        }
//        if(!$found) {
//            $notifications->addonConfiguration->{-100000} = $conf;
//        }
//        $conf->productId = $product->id;
//        $conf->isSingle = true;
//
//        $psm->getApi()->getPmsManager()->saveConfiguration($psm->getSelectedMultilevelDomainName(), $notifications);
//
//        //send back product id of addon
//        echo json_encode(['product_id' => $product->id]);
//        exit();
//    } else {
//        $psm->createProductError = "Failed to create a new product, make sure the account you are trying to create a product on is correct set up.";
//    }
}

//create payment request in zaui
if(isset($_GET['createBooking']) && isset($_POST['bookingReference'])){
    $url = "https://api.zaui.io/v1/";

    //vars received from ajax call
    $date = date("Y-m-d", strtotime($_POST['startDate']));
    $bookingReference = $_POST['bookingReference'];
    $prod_code = $_POST['prodCode'];
    $tourDepartureTime = $_POST['tourDepartureTime'];
    $travellers = $_POST['travellers'];
    $total = $_POST['total'];

    //api call
    $input_xml = '<?xml version="1.0" encoding="UTF-8"?>
<BookingCreateRequest xmlns="https://api.zaui.io/api/01">
	<ApiKey>' . $api_key . '</ApiKey>     
	<ResellerId>' . $reseller_id . '</ResellerId>    
	<SupplierId>' . $supplier_id . '</SupplierId>
	<ExternalReference>10051374722992616</ExternalReference>
	<Timestamp>' . time() . '</Timestamp>
	<BookingReference>' . $bookingReference . '</BookingReference>
	<TravelDate>' . $date . '</TravelDate>
	<SupplierProductCode>' . $prod_code . '</SupplierProductCode>
	<Location></Location>
	<TourOptions>
		<SupplierOptionCode></SupplierOptionCode>
		<SupplierOptionName></SupplierOptionName>
		<TourDepartureTime>' . $tourDepartureTime . '</TourDepartureTime>
		<TourDuration> </TourDuration>
	</TourOptions>
	<Inclusions>
		<Inclusion></Inclusion>
		<Inclusion></Inclusion>
	</Inclusions>';

    //each registered guest
    foreach($travellers as $traveller){
        $adult = $traveller['isChild'] == "false" ? "ADULT" : "CHILD";

        //separate first and last name
        $names = explode(" ", $traveller['name']);
        $surname = end($names);
        reset($names);
        $surname_key = array_search($surname, $names);
        unset($names[$surname_key]);
        $first_name = implode(" ", $names);

        $input_xml .= '
            <Traveller>
		<TravellerIdentifier>' . $traveller['email'] . '</TravellerIdentifier>
		<TravellerTitle></TravellerTitle>
		<GivenName>' . $first_name . '</GivenName>
		<Surname>' . $surname . '</Surname>
		<AgeBand>' . $adult . '</AgeBand>
		<LeadTraveller></LeadTraveller>
	</Traveller>
        ';
    }

    $input_xml .=
        '<TravellerMix>
		<Senior>0</Senior>
		<Adult>' . $total . '</Adult>
		<Child>0</Child>
		<Student>0</Student>
		<Infant>0</Infant>
		<Total>' . $total . '</Total>
	</TravellerMix>
	<RequiredInfo>
		<Question>
			<QuestionText></QuestionText>
			<QuestionAnswer></QuestionAnswer>
		</Question>
		<Question>
			<QuestionText></QuestionText>
			<QuestionAnswer></QuestionAnswer>
		</Question>
	</RequiredInfo>
	<SpecialRequirement></SpecialRequirement>
	<PickupPoint></PickupPoint>
	<SupplierNote></SupplierNote>
	<AdditionalRemarks>
		<Remark>
		</Remark>
	</AdditionalRemarks>
	<ContactDetail>
		<ContactType></ContactType>
		<ContactName></ContactName>
		<ContactValue></ContactValue>
	</ContactDetail>
	<PickupLocation>
		<SupplierPickupCode></SupplierPickupCode>
	</PickupLocation>
	<DropoffLocation>
		<SupplierDropoffCode></SupplierDropoffCode>
	</DropoffLocation>
</BookingCreateRequest>
';
    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_POSTFIELDS,
        $input_xml);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
    curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 300);
    $data = curl_exec($ch);
    curl_close($ch);

    //booking result
    $booking = json_decode(json_encode(simplexml_load_string($data)), true);
    echo json_encode($booking);
}

//end loading available activities
//print_r("<pre>");
//print_r($available_activities);
//print_r('</pre>');
//exit();

