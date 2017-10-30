<?php
chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$user = $factory->getApi()->getUserManager()->logOn($_GET['username'], $_GET['password']);
$factory->getApi()->getTrackAndTraceManager()->deleteReplyMessage($_GET['messageId']);