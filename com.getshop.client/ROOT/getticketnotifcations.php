<?php
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$result = $factory->getSystemGetShopApi()->getCustomerTicketManager()->getNiggerFriendlyTicketNotifications($factory->getStore()->id);
if($result->numberOfUnReadTickets == 0) {
    return;
}
?>
<span id="ticketnotifications" style="position:absolute; left: 0px; background-color:red; color:#fff; font-weight: bold; width: 20px;text-align: center; border-bottom-right-radius: 3px;"><?php echo $result->numberOfUnReadTickets; ?>
    <div class="ticketnotificationsarea" style='display:none;'>
        <?php
        foreach($result->unreadTickets as $id => $text) {
            echo "<a href='/getshopsupport.php?page=ticketview&ticketToken=$id' target='_fdasfasfasfs'>";
            echo "<div class='unreadtexttitle'>";
            echo $text;
            echo "<i class='fa fa-arrow-right'></i>";
            echo "</div>";
            echo "</a>";
        }
        ?>
        <a href="/getshopsupport.php">
            <div class='unreadtexttitle' target='_fdasfasfasfs' style='text-align: center;border-bottom: 0px;'>Open support center</div>
        </a>
    </div>
</span>

<style>
    .ticketnotificationsarea { position:absolute; width: 400px; border: solid 1px #bbb; text-align: left; background-color:#efefef; color:#000; top:70px; border-radius:3px; }
    .ticketnotificationsarea .unreadtexttitle { border-bottom: solid 1px #bbb; line-height: 26px; height: 26px;position:relative; cursor:pointer; padding-left: 3px;}
    .ticketnotificationsarea .unreadtexttitle i { font-size: 14px; position:absolute; top:0px; right:0px;  width: 26px; height: 26px;text-align: center;}
</style>