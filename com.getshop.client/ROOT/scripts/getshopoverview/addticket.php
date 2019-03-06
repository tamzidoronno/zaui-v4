<?php
chdir("../../");
include '../loader.php';

$factory = IocContainer::getFactorySingelton();

$timeinvoice = (($_POST['hour']) * 60 + $_POST['minute']);


$ticket = new core_ticket_Ticket();
$ticket->title = $_POST['description'];
$ticket->timeInvoice = $timeinvoice / 60;
$ticket->timeSpent = $timeinvoice / 60;
$ticket->currentState = "COMPLETED";
$ticket->externalId = "";
$ticket->type = "SUPPORT";
$ticket->userId = $_POST['roomid'];


$factory->getApi()->getTicketManager()->saveTicket($ticket);

include 'ticketlist.php';
?>
