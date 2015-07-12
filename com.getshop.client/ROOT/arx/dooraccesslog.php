<center>
<a class='nextpage' href="?page=doors">Go back to doors</a>
</center>
<?
/* @var $api GetShopApi */
$externalId = $_GET['id'];
$day = 0;
if(isset($_GET['nextpage'])) {
    $day = $_GET['nextpage'];
}
$nextPage = $day + 1;
$startTime = time()-86400 - ($day*86400);
$endTime = time() - ($day*86400);
echo "Showing: ". date("d-m-Y", $startTime) . " - " . date("d-m-Y", $endTime) . "<br><br>";

$result = $api->getArxManager()->getLogForDoor($externalId, $startTime*1000, $endTime*1000);
if(!$result) {
    echo "No events found.";
} else {
    foreach($result as $res) {
        /* @var $res core_arx_AccessLog */
        echo "<div class='accesslogentry'>";
        echo date("d-m-Y H:i:s", $res->timestamp/1000) . "<bR>";
        echo $res->personName . " " . $res->card . "<br>";
        echo $res->type . "<br>";
        echo "<pre>";
        echo htmlentities($res->dac_properties);
        echo "</pre>";
        echo "</div>";
    }
}
?>
<br><br><br>
<center>
<a class='nextpage' href="?page=dooraccesslog&id=<? echo $externalId; ?>&nextpage=<? echo $nextPage; ?>">Go back one more days</a>
</center>
<br><br><br>
<br><br><br>
<br><br><br>

<style>
    .accesslogentry { border: solid 1px; padding: 10px; }
    .nextpage {
        text-decoration: none;
        color:#FFF;
        height: 20px;
        text-align: center;
        padding: 10px;
        border-radius: 3px;
        background-color: #bf580d;
        margin-bottom: 20px;
        display:block;
    }
</style>