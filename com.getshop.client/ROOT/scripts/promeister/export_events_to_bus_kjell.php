
<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$events = $factory->getApi()->getEventBookingManager()->getEvents("booking");

function replaceSeperator($in, $seperator) {
    if (!$in)
        return "";
    
    return str_replace($seperator, " ", $in);
}
$i = 0;
foreach ($events as $event) {
    $seperator = "|";
    echo replaceSeperator(@$event->location->name, $seperator).$seperator;
    echo replaceSeperator(@$event->bookingItemType->name, $seperator).$seperator;
    echo replaceSeperator(@$event->mainStartDate, $seperator).$seperator;
    
    echo replaceSeperator("http://promeister.academy/?page=".@$event->bookingItem->pageId."&eventId=".@$event->id, $seperator).$seperator;
    
    echo "<br/>";
}