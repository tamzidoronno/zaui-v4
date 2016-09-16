
<?php
chdir("../");
include '../loader.php';

$factory = IocContainer::getFactorySingelton();

if(isset($_GET['username'])) {
    $username = $_GET['username'];
    $password = $_GET['password'];
    $factory->getFactory()->getApi()->getUserManager()->logOn($username, $password);
}

if(!$factory->getApi()->getUserManager()->getLoggedOnUser()) {
    echo "Logg inn før du kan se denne oversikten";
    echo "<form action=''>";
    echo "<input name='username'><br>";
    echo "<input name='password' type='password'><br>";
    echo "<input type='submit' value='Logg inn'>";
    echo "</form>";
    return;
}

echo "Add the orders you wish to extract<br>";
echo "<form action='' method='POST'>";
echo "<textarea style='width:200px;height:100px;' name='orderIds'>" . $_POST['orderIds'] . "</textarea><br>";
echo "<input type='submit' value='Send' name='fetchorders'>";
echo "</form>";

if(isset($_POST['fetchorders'])) {
    
    
    $orders = $_POST['orderIds'];
    $orders = explode("\n", $orders);
    $comments = array();
    foreach($orders as $orderId) {
        $order = $factory->getApi()->getOrderManager()->getOrderByincrementOrderId($orderId);
        if(!$orderId) {
            continue;
        }
        $comments[$orderId] = $order->invoiceNote;
        $first = true;
        
        
    }
    
    echo "<h1>Comments</h1>";
    echo "<table border='1' width='70%'>";
    echo "<tr><th>OrderId</th><th>Comment</th></tr>";
    foreach($comments as $orderId => $comment) {
        echo "<tr>";
        echo "<td>" . $orderId . "</td>";
        echo "<td>" . $comment . "</td>";
        
        echo "</tr>";
    }
    echo "</table>";
}

/**
 * 
 * @param core_hotelbookingmanager_UsersBookingData $bdata
 */
function getGuestName($bdata) {
    $result =array();
    foreach($bdata->references as $id => $reference) {
        /* @var $reference core_hotelbookingmanager_BookingReference */
        foreach($reference->roomsReserved as $room) {
            /* @var $room core_hotelbookingmanager_RoomInformation */
            foreach($room->visitors as $visitor) {
                $result[] = $visitor->name;
            }
        }
    }
    return $result;
    
}


?>