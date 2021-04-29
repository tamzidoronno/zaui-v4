<?php

// ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###
// ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###
// REQUEST HANDLERS
// ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###
// ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###

/** ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###
    list activities
*/
function gslZauiListActivities($lang,$startdate,$enddate)
{
    global $servername, $username, $password;
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

    $activities_iterator =  new SimpleXMLIterator($activities_sql);
    $activities_xml = sxiToArray($activities_iterator);
    $activities = $activities_xml['Tour'];

//getting batch availability
    $sql = "SELECT xml_response FROM getshop_zaui_cache.availability LIMIT 1";
    $result = $conn->query($sql);

    if($result->num_rows == 0){
        die("ERROR: Batch availability list not found!");
    }

    $availability_sql = $result->fetch_assoc()['xml_response'];

    $availability_iterator = new SimpleXMLIterator($availability_sql);
    $availability_xml = sxiToArray($availability_iterator);

    $availability = $availability_xml['BatchTourAvailability'];

    $available_activities = [];
    $date_start = date("Y-m-d", strtotime($startdate));
    $date_end = date("Y-m-d", strtotime($enddate));

//checking which activities are available
    foreach($activities as $activity){
        $prod_code = $activity['SupplierProductCode'][0];
        $act_av = [
            'supplierProductCode' => $prod_code,
            'supplierProductName' => gettype($activity['SupplierProductName']) == "array" && empty($activity['SupplierProductName']) ? null : $activity['SupplierProductName'][0],
            'tourDescription' => gettype($activity['TourDescription']) == "array" && empty($activity['TourDescription']) ? null : html_entity_decode($activity['TourDescription'][0]),
            'image' => gettype($activity['TourImage']) == "array" && empty($activity['TourImage']) ? null : $activity['TourImage'][0],
            'tours' => null,
        ];

        foreach($availability as $available){
            if($available['SupplierProductCode'][0] == $prod_code && ( $available['Date'][0] >= $date_start && $available['Date'][0] <= $date_end ) ){
                if($available['AvailabilityStatus'][0]['Status'][0] != "UNAVAILABLE") {
                    $act_av['tours'] = true;
                    array_push($available_activities, $act_av);
                    break;
                }
            }
        }
    }

    //check if we have custom headers text + intro for given language... and add to response if we do
    $uioverrides = new stdClass();
    if($lang)
    {
        switch($lang)
        {
            case 'en':
                global $gsZauiHeadline_en, $gsZauiIntro_en;
                if(isset($gsZauiHeadline_en)) $uioverrides->headline = $gsZauiHeadline_en;
                if(isset($gsZauiHeadline_en)) $uioverrides->intro = $gsZauiIntro_en;
                break;
            case 'no':
                global $gsZauiHeadline_no, $gsZauiIntro_no;
                if(isset($gsZauiHeadline_no)) $uioverrides->headline = $gsZauiHeadline_no;
                if(isset($gsZauiHeadline_no)) $uioverrides->intro = $gsZauiIntro_no;
                break;
            default:
                //do nothing
                break;
        }
    }
    $return = (object) array('uioverrides' => $uioverrides,'activities' => $available_activities);
    ob_clean();
    echo json_encode($return, JSON_UNESCAPED_UNICODE);
    exit();
} // end of gslZauiListActivities

/** ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###
    check availability of a given tour for a given daten
*/
function gslZauiCheckAvailability($prod_code, $adults, $children, $startdate, $enddate)
{
    global $api_key, $reseller_id, $supplier_id;
    $total = (int)$adults + (int)$children;

    $url = "https://api.zaui.io/v1/";

    $date_start = date("Y-m-d", strtotime($startdate));
    $date_end = date("Y-m-d", strtotime($enddate));
    $input_xml = '<?xml version="1.0" encoding="UTF-8"?>
<CheckAvailabilityRequest xmlns="https://api.zaui.io/api/01">
	<ApiKey>' . $api_key . '</ApiKey>
	<ResellerId>' . $reseller_id . '</ResellerId>
	<SupplierId>' . $supplier_id . '</SupplierId>
	<Timestamp>' . time() . '</Timestamp>
	<StartDate>' . $date_start . '</StartDate>
	<EndDate>' . $date_end . '</EndDate>
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
    curl_setopt($ch, CURLOPT_POSTFIELDS, $input_xml);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
    curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 300);
    $data = curl_exec($ch);
    curl_close($ch);

    $tours_iterator = new SimpleXMLIterator($data);
    $tours_xml = sxiToArray($tours_iterator);
    $tours = $tours_xml['TourAvailability'];

    echo json_encode($tours);
    exit();
} // end of gslZauiCheckAvailability

/** ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###
    create an addon in GS for a given activity on a booking
*/
function gslZauiCreateAddon($prod_code, $tourDepartureTime, $tourPrice, $tourTaxes, $tourDate)
{
    global $servername, $username, $password, $gsZauiUser, $gsZauiPass, $gsZauiTaxGroup, $gsZauiTaxRate;
    global $api_key, $reseller_id, $supplier_id;

    if(!$tourTaxes) $tourTaxes = 0;

    $conn = new mysqli($servername, $username, $password);
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

    $activities_iterator =  new SimpleXMLIterator($activities_sql);
    $activities_xml = sxiToArray($activities_iterator);
    $activities = $activities_xml['Tour'];

    foreach($activities as $activity){
        if($activity['SupplierProductCode'][0] == $prod_code){
            $activity_name = $activity['SupplierProductName'][0];
            break;
        }
    }

    if(isset($activity_name)) {
        //Create addon
        $factory = IocContainer::getFactorySingelton();
        $factory->getApi()->getUserManager()->logOn($gsZauiUser, $gsZauiPass);

        $product = $factory->getApi()->getProductManager()->createProduct();
        $psm = new \ns_c5a4b5bf_365c_48d1_aeef_480c62edd897\PsmConfigurationAddons();

        $zauiAddonName = "Zaui, " . $activity_name . ", " . $tourDate . ( $tourDepartureTime != '00:00:00' ? ' - '. $tourDepartureTime : '' ) . ", "  . time() . "";

        if ($product) {
            $product->name = $zauiAddonName;
            $product->tag = "addon";
            $product->isSingle = true;
            $product->price = doubleval($tourPrice);
            $product->priceExTaxes = doubleval( doubleval($tourPrice) - doubleval($tourTaxes) );
            $product->taxGroupObject->groupNumber = ( $gsZauiTaxGroup ? (int)$gsZauiTaxGroup : 0);
            $product->taxGroupObject->taxRate = ( $gsZauiTaxRate ? (int)$gsZauiTaxRate : 0);

            $product = $factory->getApi()->getProductManager()->saveProduct($product);
            $alladdons = $factory->getApi()->getPmsManager()->getConfiguration($psm->getSelectedMultilevelDomainName());

            $conf = new \core_pmsmanager_PmsBookingAddonItem();
            $conf->productId = $product->id;
            $conf->isSingle = true;
            $conf->noRefundable = true;
            $conf->isIncludedInRoomPrice = false;
            $conf->isUniqueOnOrder = true;
            $conf->alwaysAddAddon = false;
            $conf->includedInBookingItemTypes = array();
            $conf->displayInBookingProcess = array();
            $conf->descriptionWeb = $zauiAddonName;
            $conf->bookingicon = '';
            $conf->count = 1;

            $alladdons->addonConfiguration->{-100000} = $conf;


            $factory->getApi()->getPmsManager()->saveConfiguration($psm->getSelectedMultilevelDomainName(), $alladdons);

            //send back product id of addon
            echo json_encode(['product_id' => $product->id,'date' => $tourDate]);
            exit();
        } else {
            //$psm->createProductError = "Failed to create a new product, make sure the account you are trying to create a product on is correct set up.";
            echo 'Failed to add activity!';
        }
    } else {
        echo 'This activity does not exist!';
    }
    exit();
} // end of gslZauiCreateAddon


/** ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###
    book in Zaui
*/
function gslZauiBookTour($date, $bookingReference, $prod_code, $tourDepartureTime, $travellers, $total, $orderId)
{
    global $servername, $username, $password;
    global $api_key, $reseller_id, $supplier_id;

    ob_clean();

    //create payment request in zaui
    $url = "https://api.zaui.io/v1/";
    //api call
    $input_xml = '<?xml version="1.0" encoding="UTF-8"?>
<BookingCreateRequest xmlns="https://api.zaui.io/api/01">
	<ApiKey>' . $api_key . '</ApiKey>
	<ResellerId>' . $reseller_id . '</ResellerId>
	<SupplierId>' . $supplier_id . '</SupplierId>
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
        $ageBand = $traveller['isChild'] == "false" ? "ADULT" : "CHILD";

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
		<AgeBand>' . $ageBand . '</AgeBand>
		<LeadTraveller></LeadTraveller>
	</Traveller>
        ';
    }

    $input_xml .=
        '<TravellerMix>
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

    $booking_iterator = new SimpleXMLIterator($data);
    $booking = sxiToArray($booking_iterator);

    // Create connection
    $conn = new mysqli($servername, $username, $password);

// Check connection
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }

    //save booking data
    $sql = "INSERT INTO getshop_zaui_cache.booking_log (
        orderId,
        supplierProductCode,
        bookingReference,
        supplierConfirmationNumber,
        serverResponse,
        createdAt) VALUES (
        '" . $orderId . "',
        '" . $prod_code . "',
        '" . $bookingReference . "',
        '" . json_encode($booking, JSON_UNESCAPED_UNICODE ) . "',
        '" . $booking['SupplierConfirmationNumber'][0] . "',
        '" . $data . "',
        '" . time() . "')";

    if ($conn->query($sql) === TRUE) {
        echo "SUCCESS-";
    } else {
        Header('HTTP/1.0 500 Internal server error');
        echo "Error: " . $sql . "<br>" . $conn->error;
    }

    $conn->close();
    die( $booking['SupplierConfirmationNumber'][0] );
}

// ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###
// ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###
// HELPER FUNCTIONS
// ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###
// ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###/ ##### ==== ###

/**
    helper function to convert XML to an array
*/
function sxiToArray($sxi){
    $a = array();
    for( $sxi->rewind(); $sxi->valid(); $sxi->next() ) {
        if(!array_key_exists($sxi->key(), $a)){
            $a[$sxi->key()] = array();
        }

        if($sxi->hasChildren()){
            $a[$sxi->key()][] = sxiToArray($sxi->current());
        }
        else{
            $a[$sxi->key()][] = (string) $sxi->current();
        }
    }
    return $a;
}