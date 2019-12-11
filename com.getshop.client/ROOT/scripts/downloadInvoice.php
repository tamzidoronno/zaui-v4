<?php
header('Pragma: public');  // required
header('Expires: 0');  // no cache
header("Content-type:application/pdf");
header("Content-Disposition:attachment;filename=".$_GET['incrementalOrderId'].".pdf");

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

$res = $factory->getApi()->getInvoiceManager()->getBase64EncodedInvoice($_GET['orderId']);
echo base64_decode($res);
?>