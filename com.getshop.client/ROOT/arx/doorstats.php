<?php
/* @var $api GetShopApi */
$day = 0;
$time = time();
if(isset($_GET['date'])) {
    $time = strtotime($_GET['date'] . " 23:59");
}
$startTime = $time-86400;
$endTime = $time;

$doors = $api->getArxManager()->getAllDoors();
foreach($doors as $door) {
    $doors[$door->externalId] = $door;
}
$result = $api->getArxManager()->getLogForAllDoor($startTime*1000, $endTime*1000);

$funnyArray = array();
foreach($result as $doorId => $res) {
    $funnyArray[$doorId] = sizeof($res);
}
arsort($funnyArray);
echo "<form id='changestartdate'>";
echo "<input type='txt' class='daydate' name='date' value='" . date("d.m.Y", $time) . "'>";
echo "<input type='hidden' name='page' value='doorstats'>";
echo "</form>";

foreach($funnyArray as $doorId => $count) {
    $door = $doors[$doorId];
    /* @var $door core_arx_Door */
    echo "<a href='?page=dooraccesslog&id=".$door->externalId."' class='doorentrylink'><div class='doorentry'>";
    echo "<i class='fa fa-arrow-right' style='float:right;font-size:20px;'></i>";
    echo $door->name . " (opened " . $count . " times)";
    echo "</div></a>";
}
?>

<script>
$('.daydate').datepicker({ dateFormat: "dd.mm.yy"});
$('.daydate').on('change', function() {
   $('#changestartdate').submit();
});
</script>

<style>
    .doorstats { margin-bottom: 20px; border: solid 1px; font-size: 20px; padding: 10px; color:#fff; display:block; }
    .doorstats i { float: right; }
    .doorentry { border: solid 1px; padding: 10px; }
    .doorentry .options { display:none; }
    .doorentry.highlighet { background-color:#000; }
    .doorentry.highlighet .fa-arrow-right { display:none; }
    .doorentry.highlighet .options { display:block; }
    .doorentry .optionentry { padding: 10px; font-size: 20px; }
    .doorentry .optionentry i { width: 30px; }
    .doorentry a { color:#fff; text-decoration: none; }
    .doorentrylink { color:#fff; text-decoration: none; }
</style>
