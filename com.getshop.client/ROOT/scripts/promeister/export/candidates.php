<?php

chdir("../../../");

include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$events = $factory->getApi()->getEventBookingManager()->getAllEvents("booking");

function removeCommas($data) {
    return str_replace(",", "_", $data);
}

// eventid, name, cellphone, email, companyname, companyvat, group
foreach ($events as $event) {
    $users = $factory->getApi()->getEventBookingManager()->getUsersForEvent("booking", $event->id);
    if (!$users || !is_array($users)) {
        continue;
    }
    foreach ($users as $user) {
        $participatedStatus = @$event->participationStatus->{$user->id};
        echo removeCommas($event->id).",";
        echo removeCommas($user->fullName).",";
        echo removeCommas($user->cellPhone).",";
        echo removeCommas($user->emailAddress).",";
        if ($user->companyObject) {
            echo removeCommas($user->companyObject->name).",";
            echo removeCommas($user->companyObject->vatNumber).",";
        } else {
            echo ",";
            echo ",";
        }
        echo removeCommas($participatedStatus).",";
        
        $group = $factory->getApi()->getUserManager()->getGroup(@$user->companyObject->groupId);
        if ($group) {
            echo removeCommas($group->groupName);
        } else {
            echo ",";
        }
        echo "<br/>";
    }
    
    
    
}
