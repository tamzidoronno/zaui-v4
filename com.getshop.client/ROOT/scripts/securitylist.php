<h1>Firelist <?php echo date("d.m.Y H:i"); ?></h1>
    
<?php

chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

function convertToJavaDate($time) {
    $time = strtotime($time);
    return date("M d, Y h:i:s A", $time);
}        
    
$bookingfilter = new core_pmsmanager_PmsBookingFilter();
$bookingfilter->startDate = convertToJavaDate(date("d.m.Y H:i", time()));
$bookingfilter->endDate = convertToJavaDate(date("d.m.Y, H:i", time()+1000));
$bookingfilter->filterType = "active";
$bookingfilter->includeDeleted = false;

$list = $factory->getApi()->getPmsManager()->getSimpleRooms($_GET['engine'], $bookingfilter);
$tmplist = array();
foreach($list as $r) {
    $tmplist[$r->room] = $r;
}
ksort($tmplist);
$list = $tmplist;
?>
<table cellspacing='1' cellpadding='1'>
    <tr bgcolor='#fff' style='font-weight: bold;'>
        <td>Code</td>
        <td>Room</td>
        <td>GC</td>
        <td>Guest</td>
        <td>Booker</td>
        <td>Room type</td>
        <td>Check-in</td>
        <td>Check-out</td>
        <td>Total</td>
        <td>Unpaid</td>
    </tr>
<?php
$guestCount = 0;
$roomCount = 0;
foreach($list as $room) {
    $guests = array();
    foreach((array)$room->guest as $g) {
        if($g->isDisabled) {
            $guests[] = "<span style='color:red; font-weight:bold;'>" . $g->name . " is disabled.</span>";
        } else {
            $guests[] = $g->name;
        }
    }
    if($_GET['useCheckinInstead']) {
        if(!$room->checkedIn || $room->checkedOut) {
            continue;
        } 
    } else {
        if(!$room->transferredToArx) {
            continue;
        }
    }
    
    $guestCount += $room->numberOfGuests;
    $roomCount++;
    
    echo "<tr bgcolor='#fff'>";
    echo "<td>" . $room->code . "</td>";
    echo "<td>" . $room->room . "</td>";
    echo "<td>" . $room->numberOfGuests . "</td>";
    echo "<td>" . join("<br>", $guests) . "</td>";
    echo "<td>" . $room->owner . "</td>";
    echo "<td>" . $room->roomType . "</td>";
    echo "<td>" . date("d.m.Y H:i", $room->start/1000). "</td>";
    echo "<td>" . date("d.m.Y H:i", $room->end/1000). "</td>";
    echo "<td>" . round($room->totalCost) . "</td>";
    echo "<td>" . round($room->totalUnpaidCost) . "</td>";
    echo "</tr>";
    $addons = (array)$room->addons;
    if(count($addons) > 0) {
        echo "<tr>";
        echo "<td></td>";
        echo "<td colspan='99' style='height:10px;background-color:#fff;border-bottom: solid 1px #efefef;'>";
        foreach($room->addons as $addon) {
            echo "<div>" . date("d.m.Y", strtotime($addon->date)) . " : " . $addon->count . " x " . $addon->name . "</div>";
        }
        echo "</td>";
        echo "</tr>";
    }
}
echo "</table>";
?>
<br>
Total number of guests <?php echo $guestCount; ?>, number of rooms: <?php echo $roomCount; ?>
<style>
    td { font-size: 12px; }
</style>