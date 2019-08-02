<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$attahcment  = $factory->getSystemGetShopApi()->getTicketManager()->getAttachment($_GET['attachmentid']);
header('Content-Type: '.$attahcment->type);
header('Content-Disposition: inline; filename="'.$attahcment->name.'"');
echo base64_decode($attahcment->base64Content);
?>