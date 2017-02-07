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
    $events = $factory->getApi()->getEventBookingManager()->getEvents("booking");
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

function makeEnd() {
    echo "</div>";
        echo "<div class='footer'>";

        echo date("d/m-Y", time())."&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;".date("H:i", time());
        echo "</div>";
    echo "</div>";
}

$events = getEvents($factory);
?>
<style>
    .col {
        display: inline-block;
        vertical-align: top; 
    }
    .row {
        padding: 1px;
        padding: 4px;
    }
    .col1 { width: 300px; }
    .col2 { width: 400px; }
    .col3 { width: 227px; }
    .col4 { width: 100px; text-align: center;}

    .page {
        width: 1049px; 
        height: 1484px; 
        box-sizing: border-box; 
        font-size: 14px; 
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
        
    }

    .bodycontent {
        padding-top: 40px;
        height: 1304px;
        box-sizing: border-box;
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
<?
$numberOfRowsEachPage = 35;
$i = 0;
foreach ($events as $monthName => $month) {
    $useMonthName = $monthName;
    foreach ($month as $event) {
        if ($i == 0) {
            echo "<div class='page'>";
            echo "<div class='pageheader'><img src='http://promeisterse30.3.0.local.getshop.com/displayImage.php?id=444ebe28-701f-44cc-be1c-9ba7e7bff243&width=247'/></div>";
                
            echo "<div class='bodycontent'>";
         
            if ($useMonthName) {
                echo "<h1>" . str_replace("_", " - ", $monthName) . "</h1>";
                $useMonthName = false;
            }
            
            ?>
            <div class='row'>
                <div class="col col1 location"><? echo $factory->__w("Location"); ?></div>
                <div class="col col2 eventname"><? echo $factory->__w("Event name"); ?></div>
                <div class='col col3 daysinformation'><? echo $factory->__w("Dates"); ?></div>
                <div class="col col4 availablespots"><? echo $factory->__w("Available spots"); ?></div>
            </div>
            <?
        }
        
        if ($useMonthName) {
            echo "<h1>" . str_replace("_", " - ", $monthName) . "</h1>";
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
            <div class='col col4'><? echo $event->bookingItem->freeSpots; ?> </div>
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

$types = $factory->getApi()->getEventBookingManager()->getBookingItemTypes("booking");

foreach ($types as $type) {
    ?>
    <div class='page'>
        <div class='pageheader'>
            <img src='http://promeisterse30.3.0.local.getshop.com/displayImage.php?id=444ebe28-701f-44cc-be1c-9ba7e7bff243&width=247'/>
        </div>
        <div class='bodycontent'>
            <h1><? echo $type->name;?></h1>
            <div>
                <?
                $javaPage = $factory->getApi()->getPageManager()->getPage($type->pageId);
                $page = new Page($javaPage, $factory);
                $layout = $page->javapage->layout;
                $page->printArea($layout->areas->body, false);
                ?>
            </div>
        </div>
        <div class='footer'>
        
        </div>
    </div>
    <?
}