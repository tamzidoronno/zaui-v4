<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$user = $factory->getApi()->getUserManager()->getLoggedOnUser();
$sedoxUser = $factory->getApi()->getSedoxProductManager()->getSedoxUserAccount();

$retUser = [];
$retUser['fullName'] = $user->fullName;
$retUser['email'] = $user->emailAddress;
$retUser['credit'] = $sedoxUser->creditAccount->balance;

echo json_encode($retUser);
?>