<?php

chdir("../../../");

include '../loader.php';
$factory = IocContainer::getFactorySingelton();


$events = $factory->getApi()->getEventBookingManager()->getAllEvents("booking");

function removeCommas($data) {
    return str_replace(",", "_", $data);
}


// Id, Name, daycount, days, location, sublocation
foreach ($events as $event) {
    
    echo removeCommas($event->id);
    
    echo ",";
    echo removeCommas($event->bookingItemType->name);
    
    echo ",";
    echo removeCommas(count($event->days));
    
    echo ",";
    $i = 1;
    foreach ($event->days as $day) {
        echo date('d.m.Y', strtotime($day->startDate));
        if ($i != count($event->days)) {
            echo "|";
        }
        $i++;
    }
    
    echo ",";
    echo removeCommas($event->location->name);
    echo ",";
    echo removeCommas($event->subLocation->name);
    echo "<br/>";
}
