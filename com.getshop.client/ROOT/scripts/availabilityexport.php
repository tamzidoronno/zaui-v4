<?php

chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();


$timezone = $factory->getStore()->timeZone;
if($timezone) {
    date_default_timezone_set($timezone);
}

if(isset($_GET['username'])) {
    $username = $_GET['username'];
    $password = $_GET['password'];
    $login = $factory->getFactory()->getApi()->getUserManager()->logOn($username, $password);
    if(!$login) {
        echo "Login failed.";
        return;
    }
}

$types = $factory->getApi()->getBookingEngine()->getBookingItemTypes("default");


$toprint = array();
foreach($types as $type) {
    $filter = new \core_pmsmanager_PmsBookingFilter();
    $filter->startDate = date("c", strtotime($_GET['start']." 00:00"));
    $filter->endDate = date("c", strtotime($_GET['end']." 23:59"));
    $filter->includeVirtual = false;
    $filter->fromPms = true;
    $filter->removeAddonsIncludedInRoomPrice = true;
    $filter->segments = array();
    $filter->includeNonBookable = false;
    $filter->typeFilter = array();
    $filter->typeFilter[] = $type->id;
    $data = $factory->getApi()->getPmsReportManager()->getCoverageReport("default", $filter);

    if(isset($_GET['showhtml'])) {
        echo "<h1>" . $type->name . "</h1>";
        echo "<table>";
        echo "<tr>";
        echo "<th>date</th>";
        echo "<th>roomsAvailable</th>";
        echo "<th>roomsTaken</th>";
        echo "<th>roomsArriving</th>";
        echo "<th>roomsDeparting</th>";
        echo "<th>guestsStaying</th>";
        echo "<th>guestsArriving</th>";
        echo "<th>guestsDeparting</th>";
        echo "<th>roomsSold</th>";
        echo "<th>groupRoomsSold</th>";
        echo "<th>groupRoomsArriving</th>";
        echo "<th>groupRoomsDeparting</th>";
        echo "<th>groupRoomsTaken</th>";
        echo "<th>avgPrice</th>";
        echo "<th>totalPrice</th>";
        echo "<th>occupancy</th>";
        echo "<tr>";

        foreach($data->entries as $entry) {
            echo "<tr>";
            echo "<td>" .date("d.m.Y", strtotime($entry->date)). "</td>";
            echo "<td align='center'>" .$entry->roomsAvailable. "</td>";
            echo "<td>" .$entry->roomsTaken. "</td>";
            echo "<td>" .$entry->roomsArriving. "</td>";
            echo "<td>" .$entry->roomsDeparting. "</td>";
            echo "<td>" .$entry->guestsStaying. "</td>";
            echo "<td>" .$entry->guestsArriving. "</td>";
            echo "<td>" .$entry->guestsDeparting. "</td>";
            echo "<td>" .$entry->roomsSold. "</td>";
            echo "<td>" .$entry->groupRoomsSold. "</td>";
            echo "<td>" .$entry->groupRoomsArriving. "</td>";
            echo "<td>" .$entry->groupRoomsDeparting. "</td>";
            echo "<td>" .$entry->groupRoomsTaken. "</td>";
            echo "<td>" .round($entry->avgPrice). "</td>";
            echo "<td>" .round($entry->totalPrice). "</td>";
            echo "<td>" .round($entry->occupancy). "</td>";
            echo "</tr>";
        }
        echo "</table>";

    } else {
        $toprintobj = new stdClass();
        $toprintobj->name = $type->name;
        $toprintobj->categoryId = $type->id;
        $toprintobj->days = $data->entries;
        foreach($toprintobj->days as $i => $day) {
            $toprintobj->days[$i]->date = date("d.m.Y", strtotime($day->date));
        }
        $toprint[] = $toprintobj;
    }
}
header('Content-type:application/json;charset=utf-8');
echo json_encode($toprint);

?>