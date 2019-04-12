<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$object = new core_gsd_IotDeviceInformation();
$object->identification = $_GET['identification'];

$object = $factory->getApi()->getGdsManager()->getIotDeviceInformation($object);
if($object->address) {
    $result = array();
    $result['token'] = $object->token;
    $result['address'] = $object->address;
    echo json_encode($result);
}
?>
