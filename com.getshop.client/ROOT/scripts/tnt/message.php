<?php
/**
 * 
 * This api is invoked to add the following message types:
 * 
 * type=driver -- A message is sent to the driver and will pop up as soon as he/she opens the app.
 * /scripts/tnt/message.php?username={username}&password={password}&type=driver&driverid={driverid}&message={message}
 * 
 * type=destination -- This will add message to the destination 
 * /scripts/tnt/message.php?username={username}&password={password}&type=destination&routeid={routeid}&stopseq={stopseq}&message={message}
 */

function validateRoute($routeId, $factory) {
    /* @var $factory Factory */
    $route = $factory->getApi()->getTrackAndTraceManager()->getRoutesById($routeId);
    
    if (!$route[0]) {
        echo "Route not found";
        http_response_code(400);
        die();
    }
    
    return $route[0];
}

function validateUser($userId, $factory) {
    /* @var $factory Factory */
    $user = $factory->getApi()->getUserManager()->getUserById($userId);
    
    if (!$user) {
        echo "Driver not found";
        http_response_code(400);
        die();
    }
}

function validateDestination($stopseq, $route) {
    /* @var $route core_trackandtrace_Route */
    $found = false;
    foreach ($route->destinations as $dest) {
        if ($dest->seq == $stopseq) {
            $found = $dest;
        }
    }
    
    if (!$found) {
        echo "Invalid stop sequence given";
        http_response_code(400);
        die();
    }
    
    return $found;
}

chdir("../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$user = $factory->getApi()->getUserManager()->logOn($_GET['username'], $_GET['password']);

if (!$user) {
    echo "ACCESS DENIED";
    http_response_code(403);
    return;
}

if(!isset($_GET['message']) || !$_GET['message']) {
    echo "Message can not be empty";
    http_response_code(400);
    die();
}

if (!isset($_GET['type']) || !$_GET['type']) {
    echo "Type can not be empty";
    http_response_code(400);
    die();
}

if ($_GET['type'] == "driver") {
    validateUser(@$_GET['driverid'], $factory);
    $factory->getApi()->getTrackAndTraceManager()->sendMessageToDriver($_GET['driverid'], $_GET['message']);
} else if ($_GET['type'] == "destination") {
    $route = validateRoute(@$_GET['routeid'], $factory);
    $dest = validateDestination(@$_GET['stopseq'], $route);
    $factory->getApi()->getTrackAndTraceManager()->setInstructionOnDestination($_GET['routeid'], $dest->id, $_GET['message']);
} else {
    echo "invalid type";
    http_response_code(400);
    die();
}



