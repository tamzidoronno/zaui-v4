<?php
chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$timezone = $factory->getStore()->timeZone;
$responseLog = '/var/jomreslogs/jomres.log';
$errorLog = '/var/jomreslogs/jomres_error.log';
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

try{
    $jomresBooking = $factory->getApi()->getJomresManager()->fetchBookings('default');

} catch(Exception $e){

}
try{
    $jomresAvailability = $factory->getFactory()->getApi()->getJomresManager()->updateAvailability('default');

}catch(Exception $e){

}


header('Content-Type: application/json; charset=utf-8');
echo json_encode($jomresBooking);
