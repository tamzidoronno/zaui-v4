<?php

//Include MySQL and API credentials
include_once ('config.php');
include_once('functions.php');


if(isset($_GET['getActivities'])){
    //Load cached available activities from database
    gslZauiListActivities($_GET['language']);
} else if(isset($_GET['checkAvailability']) && isset($_GET['prodCode']) && isset($_GET['adults']) && isset($_GET['children'])){
    //check availability call after Show more buttons is pressed on front end
    gslZauiCheckAvailability($_GET['prodCode'], $_GET['adults'], $_GET['children'],$_GET['startDate']);
} else if (isset($_GET['createAddon']) && isset($_GET['prodCode']) && isset($_GET['tourDepartureTime']) && isset($_GET['tourPrice'])){
    //Create addon out of activity
    gslZauiCreateAddon($_GET['prodCode'], $_GET['tourDepartureTime'], $_GET['tourPrice'], $_GET['tourTaxes']);
} else if(isset($_GET['createBooking']) && isset($_POST['bookingReference'])){

    //vars received from ajax call
    $date = date("Y-m-d", strtotime($_POST['startDate']));
    $bookingReference = $_POST['bookingReference'];
    $prod_code = $_POST['prodCode'];
    $tourDepartureTime = $_POST['tourDepartureTime'];
    $travellers = $_POST['travellers'];
    $total = $_POST['total'];

    gslZauiBookTour($date, $bookingReference, $prod_code, $tourDepartureTime, $travellers, $total);

}
