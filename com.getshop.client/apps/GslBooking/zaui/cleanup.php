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

    echo '<pre>';
    foreach($result as $rvalue)
    {
        echo 'we got a Zaui cache item . orderId ' . $rvalue['orderId'] . ":: and it has a product... ". $rvalue['supplierConfirmationNumber'] ."\n";
        //for    some reason this call dies 
        //$booking = $factory->getApi()->getPmsManager()->getBooking($rvalue['orderId']);
        //print_r($booking);

    }

    /*
        go through all of them and check if their name contains zaui....
    */
    foreach($alladdons->addonConfiguration as $addon)
    {
        if( strpos($addon->name,'Zaui') !== false )
        {
            $product = $product = $factory->getApi()->getProductManager()->getProduct($addon->productId);
            echo 'we got a Zaui addon . ' . $addon->name . ":: and it has a product... ". $product->name ."\n";

            //print_r($product);
            echo "\n\n\n";
        }

    }


    //print_r($alladdons);

    die();

}

/*
    check all registered zaui bookings we have, check if they are connected to a
    valid booking in the CMS and delete them if not.
*/
function cleanupZauiBookings()
{

}



// call them functions

cleanupAddons();

?>