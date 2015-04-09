<?
session_start();
include '../loader.php';

$path = $_GET['path'];
$path2 = $_GET['path2'];

if ($path2 == "boschlogout.php") {
    include 'bosch/boschlogout.php';
    return;
}

if ($path2 == "ping.php") {
    include 'bosch/ping.php';
    return;
}

$factory = IocContainer::getFactorySingelton(false);
$settings = $factory->getApi()->getPageManager()->getApplicationsBasedOnApplicationSettingsId("8cc26060-eef2-48ac-8174-914f533dc7ed");
$instances = [];
foreach ($settings as $setting) {
    $instances[] = $factory->getApplicationPool()->createAppInstance($setting);
}
   
$hasAccess = false;
foreach ($instances as $app) {
    if (isset($_POST['Username']))
        $app->doLogin();
    
    if ($app->hasAccess($path2)) {
        $hasAccess = true;
        break;
    }
}

if (!$hasAccess) {
    include 'bosch/boschlogin.php';
    return;
}


if (strpos($path2, ".php") > -1) {
    include $path.'/'.$path2;
} else {
    echo file_get_contents($path.'/'.$path2);
}
?>