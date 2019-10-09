<?php


chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();


if(isset($_GET['username'])) {
    $username = $_GET['username'];
    $password = $_GET['password'];
    $login = $factory->getFactory()->getApi()->getUserManager()->logOn($username, $password);
    if(!$login) {
        echo "Login failed.";
        return;
    }
}


$filter = new \core_pmsmanager_PmsBookingFilter();
$filter->startDate = date("c", strtotime($_GET['start']." 00:00"));
$filter->endDate = date("c", strtotime($_GET['end']." 23:59"));
$data = $factory->getApi()->getPmsReportManager()->getFilteredBookings("default", $filter);

$formater = new ns_961efe75_e13b_4c9a_a0ce_8d3906b4bd73\PmsSearchBookingColumnFormatters(null);
$channels = $formater->getChannels();

if(isset($_GET['showtypes'])) {
    echo "<table width='100%'>";
    echo "<tr>";
    echo "<th align='left'>Id</th>";
    echo "<th align='left'>arrivalDate</th>";
    echo "<th align='left'>bookedDate</th>";
    echo "<th align='left'>checkoutDate</th>";
    echo "<th align='left'>numberOfRooms</th>";
    echo "<th align='left'>price</th>";
    echo "<th align='left'>channel</th>";
    echo "<th align='left'>Channel readable</th>";
    echo "</tr>";
    
    foreach($data as $res) {
        echo "<tr>";
        echo "<td>" . $res->bookingId . "</td>";
        echo "<td>".date("d.m.Y", strtotime($res->arrivalDate))."</td>";
        echo "<td>".date("d.m.Y", strtotime($res->bookedDate))."</td>";
        echo "<td>".date("d.m.Y", strtotime($res->checkoutDate))."</td>";
        echo "<td>".$res->numberOfRooms."</td>";
        echo "<td>".$res->price."</td>";
        echo "<td>".$res->channel."</td>";
        echo "<td>".$channels[$res->channel]."</td>";
        echo "</tr>";
    }
} else {
    foreach($data as $res) {
        $res->channelReadable = $channels[$res->channel];
    }
    
    header('Content-type:application/json;charset=utf-8');
    echo json_encode($data);
}



?>