<?php
session_start();
include '../loader.php';
?>

<script type="text/javascript" src="js/datatables/jquery.js"></script>
<script type="text/javascript" src="js/datatables/jquery.dataTables.min.js"></script>
<link rel="stylesheet" type="text/css" href="js/datatables/jquery.dataTables.css"/>
    
<?
$api = IocContainer::getFactorySingelton()->getApi();
$api->getStoreManager()->initializeStore("www.getshop.com", session_id());

$list = $api->getGetShop()->getStores($_GET['code']);

if (count($api->transport->errors) > 0) {
    echo "<pre>";
    print_r($api->transport->errors);
    echo "</pre>";
    return;
}

echo "<table id='table_id'>
    <thead>
    <tr>
        <th>Registerd store</th>
        <th>Address</th>
        <th>users email</th>
        <th>last logged in</th>
        <th>username</th>
        <th>store config email</th>
    </tr>
    </thead><tbody>";

foreach ($list as $store) {
    $date = $store->created;
    $address = $store->webaddress;
    $email = $store->email;
    $lastLoggedIn = $store->lastLoggedIn;
    $username = $store->username;
    $configEmail = $store->configEmail;
    
    echo "<tr><td>$date</td><td>$address</td><td>$email</td><td>$lastLoggedIn</td><td>$username</td><td>$configEmail</td></tr>";
}
echo "</tbody></table>";
?>


<script>
 $(document).ready( function () {
	$('#table_id').dataTable();
} );
</script>