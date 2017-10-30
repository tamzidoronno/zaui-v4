<?php

chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$user = $factory->getApi()->getUserManager()->logOn($_GET['username'], $_GET['password']);

if (!$user) {
    echo "ACCESS DENIED";
    http_response_code(403);
    return;
}

$replyMessages = $factory->getApi()->getTrackAndTraceManager()->getReplyMessages();
echo json_encode($replyMessages);