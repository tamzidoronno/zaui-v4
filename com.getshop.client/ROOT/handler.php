<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of handler
 *
 * @author ktonder
 */
include '../loader.php';

function checkCreateApp() {
    if (isset($_POST['core']['appid']) && $_POST['core']['appid'] == "CreateStore") {
        IocContainer::$factory = new Factory();
        $createApp = new CreateStore();
        $createApp->$_POST['event']();
        die();
    }
}

class handler {
    public function route(Factory $factory) {
        $id = $_POST['core']['appid'];
        $application = $factory->getApplicationPool()->getApplicationInstance($id);
        $application->$_POST['event']();
    }
    
    public function applicationManager($event) {
        $applicationManager = new \ApplicationManager();
        $applicationManager->$event();
    }
}

checkCreateApp();

$factory = IocContainer::getFactorySingelton();
$handler = new handler();
if (isset($_POST['core']['appid'])) {
    $handler->route($factory);
} else {
    $handler->applicationManager($_POST['event']);
}

if(!isset($_POST['synchron'])) {
    $factory->run(true);
}
?>
