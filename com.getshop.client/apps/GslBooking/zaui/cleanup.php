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
    echo "<br />Addons to keep are ";
    print_r($addonstokeep);

    // go trough result set once more and tell zaui to delete everything that
    foreach($result as $rvalue)
    {
        if(!in_array($rvalue['id'], $zauiorderstokeep))
        {
            //echo '<br />DELETE THIS ZAUI ORDER :: '. $rvalue['bookingReference'] .' : ' . $rvalue['supplierConfirmationNumber'];
            if($rvalue['bookingReference']  && $rvalue['supplierConfirmationNumber'])
            {
                cancelZauiBooking( $rvalue['bookingReference']  , $rvalue['supplierConfirmationNumber'] );
                $sql = "UPDATE getshop_zaui_cache.booking_log SET bookingReference='{$rvalue['bookingReference']}__{$rvalue['supplierConfirmationNumber']}',supplierConfirmationNumber='' WHERE ID = '{$rvalue['id']}'";
                $result = $conn->query($sql);
            }
            else
            {
                echo '<br />Invalid data set or already tried to cancel? id ' . $rvalue['id'] . ' bookingref:'. $rvalue['bookingReference'] . ' SupplConfNumber:'. $rvalue['supplierConfirmationNumber'] . ':';
            }
        }
        else
        {
            echo '<br />This Zaui order is connected to a valid booking: ' . $rvalue->supplierConfirmationNumber;
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
                echo '<br />Keeping this addon ' . $addon->name;
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
    global $api_key, $reseller_id, $supplier_id;

    $url = "https://api.zaui.io/v1/";
    $input_xml = '<?xml version="1.0" encoding="UTF-8"?>
<BookingCancelRequest xmlns="https://api.zaui.io/api/01">
	<ApiKey>' . $api_key . '</ApiKey>
	<ResellerId>' . $reseller_id . '</ResellerId>
	<SupplierId>' . $supplier_id . '</SupplierId>
	<Timestamp>' . time() . '</Timestamp>
	<BookingReference>'. $bookingReference  .'</BookingReference>
	<SupplierConfirmationNumber>'. $supplierConfirmationNumber .'</SupplierConfirmationNumber>
	<CancelDate>'. date('Y-m-d') .'</CancelDate>
 	<Author>Customer Service</Author>
	<Reason>No longer traveling</Reason>
	<SupplierNote>Refunded Customer</SupplierNote>
</BookingCancelRequest>';

    $ch = curl_init();
    curl_setopt($ch, CURLOPT_URL, $url);
    curl_setopt($ch, CURLOPT_POSTFIELDS, $input_xml);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
    curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 300);
    $data = curl_exec($ch);
    curl_close($ch);

    echo '<br />' . htmlentities($input_xml) . '<hr />'. htmlentities($data);

    if($data)
    {
        $xml_iterator = new SimpleXMLIterator($data);
        $cancel_xml = sxiToArray($xml_iterator);

        print_r($cancel_xml);
    }
    else
    {
        echo '<br />No curl response? '. $url .' : ' . htmlentities($input_xml);
    }
    //  if($cancel_xml)
}



// call them functions

cleanupAddons();

?>