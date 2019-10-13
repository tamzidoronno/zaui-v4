<?php
header('Access-Control-Allow-Origin: http://system.3.0.local.getshop.com');
chdir("../");
include '../loader.php';



$referer = $_SERVER["HTTP_REFERER"];

if(!stristr($referer, "system.getshop.com") && !stristr($referer, "system.3.0.local.getshop.com")) {
    return;
}

$factory = IocContainer::getFactorySingelton();


$ticketLight = $factory->getApi()->getTicketManager()->createLightTicketWithPassword("Support generated ticket", "234234fdsafdsaf2342gfdsgdfsgdfsg3456jglp");
$ticketLight->urgency = "normal";

$realTicket = $factory->getSystemGetShopApi()->getCustomerTicketManager()->createTicket($ticketLight);
$ticketLight->incrementalTicketId = $realTicket->incrementalId;
$factory->getApi()->getTicketManager()->updateTicket($realTicket->ticketToken, $ticketLight);

echo $realTicket->id;
?>