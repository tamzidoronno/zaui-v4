<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$bookingId = $_GET['bookingid'];
$domain = $_GET['engine'];
$redirect = $factory->getApi()->getPmsInvoiceManager()->getRedirectForBooking($domain, $bookingId);
if(!$redirect) {
    echo "Sorry, we seems to have a problem here. We have been informed about this issue and will send you a payment link.";
} else {
    header('location:' . $redirect);
}
?>