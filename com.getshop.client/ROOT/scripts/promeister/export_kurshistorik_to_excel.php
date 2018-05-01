<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$users = $factory->getApi()->getUserManager()->getAllUsers();

function replaceSeperator($in, $seperator) {
    if (!$in)
        return "";
    
    return str_replace($seperator, " ", $in);
}
$i = 0;
foreach ($users as $user) {
    $i++;
    
    $events = $factory->getApi()->getEventBookingManager()->getEventsForUser("booking", $user->id);
    
    if (!is_array($events))
        continue;
    
    foreach ($events as $event) {
        $seperator = "|";
        echo $user->id.$seperator;
        echo replaceSeperator($user->fullName, $seperator).$seperator;
        echo replaceSeperator(@$event->mainStartDate, $seperator).$seperator;
        echo replaceSeperator(@$event->bookingItemType->id, $seperator).$seperator;
        echo replaceSeperator(@$event->bookingItemType->name, $seperator).$seperator;
        echo replaceSeperator(@$event->location->id, $seperator).$seperator;
        echo replaceSeperator(@$event->location->name, $seperator).$seperator;
        echo replaceSeperator(@$event->subLocation->id, $seperator).$seperator;
        echo replaceSeperator(@$event->subLocation->name, $seperator);
        echo "<br/>";
    }
}