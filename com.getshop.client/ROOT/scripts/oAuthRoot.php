<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$state = $_GET['state'];
$code = $_GET['code'];
$redirect = $factory->getApi()->getOAuthManager()->getStateRedirect($state);
$redirectUrl = $redirect . "/scripts/oauth.php?state=".$state."&code=".$code;
if(!stristr($redirectUrl, "http")) {
    $redirectUrl = "http://" . $redirectUrl;
}
header('Location:'.$redirectUrl);
?>