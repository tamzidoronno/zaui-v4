# Getshop Zaui integration

## Prerequisites

The Getshop frontend server in use must have a running MySQL server and PHP connectivity to it. PHP XML modules is needed as well. PHP 7.1 or 7.2 is installed on all current Getshop frontend servers (as of April 2021)

### Create database and user:

```mysql

CREATE USER 'getshopzaui'@'localhost' IDENTIFIED BY 'getshopzauiHemmeligt2021';
CREATE DATABASE IF NOT EXISTS getshop_zaui_cache;
GRANT ALL PRIVILEGES ON getshop_zaui_cache.* TO 'getshopzaui'@'localhost';
FLUSH PRIVILEGES;


USE getshop_zaui_cache;
CREATE TABLE IF NOT EXISTS activity_list(
   xml_response MEDIUMTEXT,
   created_at VARCHAR(255)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS availability(
   xml_response MEDIUMTEXT,
   created_at VARCHAR(255)
) ENGINE=InnoDB;

CREATE TABLE `booking_response` (
  `id` int(11) NOT NULL,
  `prod_code` varchar(255) NOT NULL,
  `supplier_confirmation_number` int(11) NOT NULL,
  `created_at` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
 
ALTER TABLE `booking_response`
  ADD PRIMARY KEY (`id`);
 
ALTER TABLE `booking_response`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;

```

## Config file

A store specific config files needs to be created in the /thundashopimages folder on the server (as this folder is symlinked from the frontend). The config file of the repo will check for a valid config or use defaults (our testing environment hotel)

```php

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

    // Getshop user credentials booking will use to login server side
    $gsZauiUser = 'zaui@getshop.no';
    $gsZauiPass = 'gsZaui';

    if(isset($argv) && isset($argv[1]))
    {
        $storeId = $argv[1];
        include_once('../../loader.php');
    }

}

```

### Example for test hotel - /thundashopimages/zaui-config-687cd85e-4812-405a-9532-7748acc29d13.php

```php

    //MySQL login credentials
    $servername = "localhost";
    $username = "getshopzaui";
    $password = "getshopzauiHemmeligt2021";

    //API credentials
    $api_key = "8bf6895a9a5a92b932d3c0aa9b24a8c7ba0b10d498983cea8eef17f35f2fb95b";
    $reseller_id = "1436";
    $supplier_id = "200";

    // Getshop user credentials booking will use to login server side
    $gsZauiUser = 'zaui@getshop.no';
    $gsZauiPass = 'gsZaui';

    // this config is also used by command line scripts... set the host for sme getshop stuff to work
    if(!isset($_SERVER['HTTP_HOST']))
    {
        $_SERVER['HTTP_HOST'] = '20528gc9.getshop.com';
    }


```


## Cron job for the local data cache

The two MySQL tables are filled by the cronjobs that should be called from the command line:

php www/apps/GslBooking/zaui/batch_availability.php
php www/apps/GslBooking/zaui/activity_list.php.php

Also here we need to include a better way of making this work for several customers at a time. The included config.php should be configured to include a customer specific config based on the server URL; to keep it in line with the Getshop workings as they are today.



## Zaui integration guide (info from Petar ;)

**In the embed script:**

Add to $(&quot;#bookingprocess&quot;).getshopbooking() the line:

&quot;zauiIntegration&quot;: true,

**scripts/booking/booking-zaui.php**

Include of the main GslZaui.php file

_Namespace apps/GslBooking/zaui_

**GslZaui.php**

Main file for work between the JavaScript and the PHP code which interacts with the API

4 main functions:

- Getting activities. Loads the cached activities list from the MySQL database. Then it loads the information for Batch availability for the next 3 months. Each activity is checked afterwards for its availability on the first day of the trip.
- Checking availability. An API call is made to zaui based on a Supplier Product Code received from the JavaScript. The API call will return available hours on the given day and also the price for each specific tour
- Create addon: INCOMPLETE! This function interacts with the getshop system in order to create a new addon for the activity chosen. It receives the Product Code and Departure Time for the name and the Tour Price for the price of the addon. Then it returns the product\_id of the new addon to the JavaScript
- Create booking: Receives data such as date of the trip, booking reference id from the getshop system, Supplier Product Code (zaui), Tour Departure Time, information about the guests and total amount of guests. Then a call is made to the API in order to create a booking in the Zaui system

**activity\_list.php**

This file should be ran through a cron _every hour_

It connects to the database first. Then an API call is made to get the activity mapping list of all activities. Then the raw xml response is saved into the database so it can be used by the GslZaui.php file.

**batch\_availability.php**

This file should be ran through a cron _every 5-10 minutes_

It connects to the database first. Then an API call is made to get the batch availability of all existing activities for the next 3 months. Then the raw xml response is saved into the database so it can be used by the GslZaui.php file.

**config.php**

Contains configuration variables for the MySQL database connection and for the API call. The file is included in GslZaui.php, activity\_list.php and batch\_availability.php

_Namespace apps/GslBooking/_

**GslBookingInject.js**

Main file for interaction with the front end. Multiple new functions added

getshop\_showZauiPage() - Ajax call to load GslZaui.php to get activities. If successful it goes to getshop\_zauiPageLoad and getshop\_zauiRightSide

getshop\_zauiPageLoad(activities) - Each activity is loaded and displayed in the front end. _Pictures need to be added._ In the end of the function the transition between addon step and zaui step is made.

getshop\_zauiShowTours(prodCode) – When the Show available tours button is pressed, an ajax call is made to GslZaui.php to load specific tour times and prices. If the ajax is successful they are displayed in a table

getshop\_zauiReserveTour(prodCode, tourDepartureTime, tourPrice) – When the reserve button on a specific tour is pressed, an ajax call iss made to GslZaui.php, which creates a new addon. If successful, the product\_id of the new add on is received. Then it is added to the booking and the right side summary is updated.

getshop\_zauiRightSide() - the right side overview is shown on the zaui step

getshop\_zauiToOverviewPage() - After the zaui step is completed, it goes to the overview page

Major changes have been made to getshop\_gotopayment(e). An ajax call is made in order to create a booking the zaui system.