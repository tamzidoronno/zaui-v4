<?php
chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();


$lead = $factory->getApi()->getGetShop()->getLead($_POST['leadid']);
foreach($_POST as $key => $val) {
    if($key == "id") {
        continue;
    }
    if($key == "followUpDate") {
        if($val) {
            $lead->followUpDate = date("c", strtotime($val));
        } else {
            $lead->followUpDate = null;
        }
    } else {
        $lead->{$key} = $val;
    }
}
$factory->getApi()->getGetShop()->saveLead($lead);
?>

