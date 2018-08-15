<?php
$users = $factory->getApi()->getUserManager()->getAllUsersSimple();
foreach($users as $usr) {
    $users[$usr->id] = $usr;
}

$factory = IocContainer::getFactorySingelton();
$filter = new \core_ticket_TicketFilter();
$tickets = $factory->getApi()->getTicketManager()->getAllTickets($filter);
$i = 0;
echo "<table width='100%'>";
foreach($tickets as $ticket) {
    echo "<tr>";
    echo "<td width='10'>" . date("d.m.Y", strtotime($ticket->dateCompleted)) . "</td>";
    echo "<td width='10'>" . $ticket->type . "</td>";
    echo "<td>" . $ticket->title . "</td>";
    echo "<td>" . round($ticket->timeInvoice,2) . "</td>";
    echo "<td>" . $ticket->currentState . "</td>";
    echo "<td>" . $users[$ticket->userId]->fullname . "</td>";
    echo "</tr>";
    $i++;
    if($i >= 20) {
        break;
    }
}
echo "</table>";
?>