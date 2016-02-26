<?php
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
header('content-type:image/x-icon');
//echo getcwd();
//echo $factory->getStore()->favicon;
if ($factory->getStore()->favicon) {
    $uuid = $factory->getStore()->favicon;
    if($factory->getApi()->getUUIDSecurityManager()->hasAccess($uuid, true, false)) {
        $content = file_get_contents("../uploadedfiles/" . $factory->getStore()->favicon);
    }
//    echo base64_encode($content);
    echo $content;
    return;
}
header("HTTP/1.0 404 Not Found");
?>
