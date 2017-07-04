<?php
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

function getEvents($factory) {
    $events = $factory->getApi()->getEventBookingManager()->getEventsForPdf("booking");
    if (!count($events)) {
        return array();
    }

    $retArray = array();

    foreach ($events as $event) {
        $startDate = $event->days[0]->startDate;
        $date = getdate(strtotime($startDate));
        $key = $date["month"] . "_" . $date["year"];
        
        if (!isset($retArray[$key])) {
            $retArray[$key] = array();
        }
        $retArray[$key][] = $event;
    }

    return $retArray;
}

function translate($name) {
    $name = str_replace("February", "Februari", $name);
    $name = str_replace("March", "Mars", $name);
    $name = str_replace("April", "April", $name);
    $name = str_replace("May", "Maj", $name);
    $name = str_replace("June", "Juni", $name);
    $name = str_replace("July", "Juli", $name);
    $name = str_replace("August", "Augusti", $name);
    $name = str_replace("September", "September", $name);
    $name = str_replace("October", "Oktober", $name);
    $name = str_replace("November", "November", $name);
    $name = str_replace("December", "December", $name);
    
    return $name;
}

function makeEnd() {
    echo "</div>";
        echo "<div class='footer'>";

//        echo "www.promeisteracademy.se";
        echo "</div>";
    echo "</div>";
}

$events = getEvents($factory);
?>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <style>
        body {
            font-family: Arial, Helvetica, sans-serif;
        }
        .col {
            display: inline-block;
            vertical-align: top; 
        }
        .row {
            padding: 1px;
            padding: 4px;
        }
        .col1 { width: 300px; }
        .col2 { width: 260px; }
        .col3 { width: 227px; }
        .col4 { width: 100px; text-align: center;}

        .page {
            width: 1049px; 
            height: 1520px; 
            box-sizing: border-box; 
            font-size: 14px;
            padding-right: 50px;
            padding-left: 50px;
        }

        .row:last-child {
            border-bottom: solid 0px;
        }

        .row:nth-child(odd) {
            background-color: #F8F8F8;
        }


        .pageheader {
            background-color: #000;
            height: 80px;
            padding: 20px;
            margin-left: 20px;
            margin-right: 20px;
        }

        .bodycontent {
            padding-top: 40px;
            height: 1304px;
            box-sizing: border-box;
            padding-left: 20px;
            padding-right: 20px;
        }

        h1 {
            text-align: center;
            border-bottom: solid 1px #EEE;
            margin-top: 30px;
        }

        .footer{ 
            height: 100px; 
            text-align: center;
            color: #888;
            font-style: italic;
        }

        .order_mask,
        .mask,
        .gsucell,
        .DayEventLister {
            display: none;
        }

        .gsucell[cellid="6825509f-48ef-4617-9463-929876490891"],
        .gsucell[cellid="ce9004f2-4a5d-4657-9b64-e1243e18f031"] {
            display: block;
        }

    </style>
</head>
<?
$numberOfRowsEachPage = 25;
$i = 0;
$webaddress = $_SERVER['SERVER_NAME'];
$url = "http:///scripts/promeister/downloadCoursesPdf.php";

foreach ($events as $monthName => $month) {
    $useMonthName = $monthName;
    foreach ($month as $event) {
        if ($i == 0) {
            echo "<div class='page'>";
            echo "<div class='pageheader'><img src='http://$webaddress/displayImage.php?id=444ebe28-701f-44cc-be1c-9ba7e7bff243&width=247'/></div>";
                
            echo "<div class='bodycontent'>";
         
            if ($useMonthName) {
                
                echo "<h1>" . translate(str_replace("_", " - ", $monthName)) . "</h1>";
                $useMonthName = false;
            }
            
            ?>
            <div class='row'>
                <div class="col col1 location"><b><? echo $factory->__w("Location"); ?></b></div>
                <div class="col col2 eventname"><b><? echo $factory->__w("Event name"); ?></b></div>
                <div class='col col3 daysinformation'><b><? echo $factory->__w("Dates"); ?></b></div>
            </div>
            <?
        }
        
        if ($useMonthName) {
            echo "<h1>" . translate(str_replace("_", " - ", $monthName)) . "</h1>";
            $useMonthName = false;
        }
        
        $i++;
        /* @var $event \core_eventbooking_Event */
        if ($event->isHidden)
            continue;

        
        $name = $event->bookingItemType->name;
        $location = $event->location->name . " - " . $event->subLocation->name;

        $date = "";

        foreach ($event->days as $day) {
            $date .= "<div>" . ns_d5444395_4535_4854_9dc1_81b769f5a0c3\Event::formatDates($day->startDate, $day->endDate) . "</div>";
        }
        ?>
        <div class="row">
            <div class="col col1"><? echo $location; ?></div>
            <div class="col col2"><? echo $name; ?></div>
            <div class="col col3"><? echo $date; ?></div>
        </div>
        <?
        
        if ($i > $numberOfRowsEachPage) {
            $i = 0;
            makeEnd();
        }
    }
}
if ($i>0) {
    makeEnd();
}