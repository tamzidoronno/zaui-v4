<?php

// make this config work in both browser and for command line calls with store ID as 1st parameter
if(!isset( $storeId ) && isset($argv) && isset($argv[1]))
{
    $storeId = $argv[1];
}

if( isset( $storeId ) && file_exists('/thundashopimages/zaui-config-' . $storeId . '.php' ) )
{
    include_once('/thundashopimages/zaui-config-' . $storeId . '.php');
}
else
{
    if(!isset($_SERVER['HTTP_HOST']))
    {
        //default to our virtual development hotel
        $_SERVER['HTTP_HOST'] = '20528gc9.getshop.com';
    }

    //MySQL login credentials
    $servername = "localhost";
    $username = "getshopzaui";
    $password = "getshopzauiHemmeligt2021";

    //API credentials
    $api_key = "8bf6895a9a5a92b932d3c0aa9b24a8c7ba0b10d498983cea8eef17f35f2fb95b";
    $reseller_id = "1436";
    $supplier_id = "200";

    $gsZauiUser = 'zaui@getshop.no';
    $gsZauiPass = 'gsZaui';

    if(isset($argv) && isset($argv[1]))
    {
        $storeId = $argv[1];
        include_once('../../loader.php');

    }

}

