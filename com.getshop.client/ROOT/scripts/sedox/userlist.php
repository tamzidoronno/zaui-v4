<?php
chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$allUsers = $factory->getApi()->getUserManager()->getAllUsers();
$userIds = array();

foreach ($allUsers as $user) {
    $userIds[] = $user->id;
}

echo json_encode($userIds);
?>

