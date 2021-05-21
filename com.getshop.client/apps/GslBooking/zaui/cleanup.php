<?php

/*
    file that shall be run by cron to clean up products and addons from the zaui process
    which dont have any valid bookings attached to them.


*/

include_once('config.php');

/*
    go through addons that have been created by the script
*/
function cleanupAddons()
{

    global $servername, $username, $password, $gsZauiUser, $gsZauiPass, $gsZauiTaxGroup, $gsZauiTaxRate;
    global $api_key, $reseller_id, $supplier_id;

    $conn = new mysqli($servername, $username, $password);
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }
    // connect to GetShop
    $factory = IocContainer::getFactorySingelton();
    $factory->getApi()->getUserManager()->logOn($gsZauiUser, $gsZauiPass);
    $psm = new \ns_c5a4b5bf_365c_48d1_aeef_480c62edd897\PsmConfigurationAddons();

    $alladdons = $factory->getApi()->getPmsManager()->getConfiguration($psm->getSelectedMultilevelDomainName());

    $sql = "SELECT * FROM getshop_zaui_cache.booking_log ORDER BY ID DESC";
    $result = $conn->query($sql);

    $zauiorderstokeep = [];
    $addonstokeep = [];

    echo '<pre>';
    foreach($result as $rvalue)
    {
        echo 'we got a Zaui cache item . PmsBooking ID ' . $rvalue['orderId'] . ":: and it has a product... ". $rvalue['supplierConfirmationNumber'] ."\n";
        //for    some reason this call dies 
        $booking = $factory->getApi()->getPmsManager()->getBooking('default',$rvalue['orderId']);

        // really hard to find a good indicator for bookings that have been deleted...
        // some research revealded that deleted bookings dont have any orderIds anymore...
        if(count($booking->orderIds) < 1 )
        {
            echo 'Booking ' . $booking->incrementBookingId . ' was deleted';
        }
        else
        {
            echo 'Booking is valid ' . $booking->incrementBookingId . ', dont touch its addons.';

            array_push($zauiorderstokeep,$rvalue->id);
            if($booking->rooms[0]->addons && is_array( $booking->rooms[0]->addons ))
            {
                for($i = 0; $i < count( $booking->rooms[0]->addons ); $i++)
                {
                    array_push($addonstokeep, $booking->rooms[0]->addons[$i]->productId);
                }
            }
            echo "\n addons to keep save are " . print_r($booking->rooms[0]->addons,1);
        }
        //print_r($booking);
    }
    echo "Addons to keep are ";
    print_r($addonstokeep);

    // go trough result set once more and tell zaui to delete everything that
    foreach($result as $rvalue)
    {
        if(!in_array($rvalue->id, $zauiorderstokeep))
        {
            echo 'DELETE THIS ZAUI ORDER ' . $rvalue->supplierConfirmationNumber;
        }
        else
        {
            echo 'This Zaui order is connected to a valid booking: ' . $rvalue->supplierConfirmationNumber;
        }
    }
    /*
        go through all of them and check if their name contains zaui....
    */
    foreach($alladdons->addonConfiguration as $addon)
    {
        if( strpos($addon->name,'Zaui') !== false )
        {
            if(in_array($addon->productId,$addonstokeep) )
            {
                echo 'Keeping this addon ' . $addon->name;
            }
            else
            {
                // make sure we dont delete anything that doesnt match our name scheme....
                $tmp = explode(', ',$addon->name);
                if($tmp[0]=='Zaui' && isset($tmp[1]))
                {
                    $product = $factory->getApi()->getProductManager()->getProduct($addon->productId);
                    if( ( ( time() - strtotime( $product->rowCreatedDate ) ) / 3600 ) > 24 )
                    {
                        $product->deactivated = true;
                        $product = $factory->getApi()->getProductManager()->saveProduct($product);
                        //echo 'We dont want this addon anymore, and can deactivate it (via its product) : ' . $addon->name;
                    }
                    else
                    {
                        //echo 'Keeping this ' . print_r($product,1);
                    }
                }
            }
            //echo 'we got a Zaui addon . ' . $addon->name . ":: and it has a product... ". $product->name ."\n";
            echo "\n\n\n";
        }

    }
    //print_r($alladdons);
    die('done');

}

/*
    check all registered zaui bookings we have, check if they are connected to a
    valid booking in the CMS and delete them if not.
*/
function cancelZauiBooking($bookingReference, $supplierConfirmationNumber)
{
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
}



// call them functions

cleanupAddons();

?>