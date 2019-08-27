<?php
$id = $_GET['backupid'];
$id = str_replace("GS-", "", $id);
$id = str_replace("gs-", "", $id);
$id = str_replace("gS-", "", $id);
$id = str_replace("Gs-", "", $id);

$url = "http://system.3.0.local.getshop.com/scripts/systembackupstatus.php?id=".$id."&ip=$ip&password=543gdnt345345GBFDSGFDSernbdbgfdsg6ty545134134fdsafsVBCXS&startrecovery=true";
if($this->getApi()->getStoreManager()->isProductMode()) {
    $url = "https://system.getshop.com/scripts/systembackupstatus.php?id=".$id . "&ip=$ip&password=543gdnt345345GBFDSGFDSernbdbgfdsg6ty545134134fdsafsVBCXS&startrecovery=true";
}
file_get_contents($url);
?>

<div style='text-align: center; font-size: 20px; margin: 20pX;'>
    Starting recovery.
</div>
<style>
.loader {
  border: 16px solid #f3f3f3; /* Light grey */
  border-top: 16px solid #3498db; /* Blue */
  border-radius: 50%;
  width: 120px;
  height: 120px;
  animation: spin 2s linear infinite;
  text-align: center;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
<center>
    <div class="loader"></div>
    <br>
    <b>
    <div id='backupstatus'>......</div>
    </b>
    </br>
</center>

<script>
    $(function() {
        setInterval(function() {
            <?php
            $url = "/scripts/getbackupstatus.php?id=".$id;
            ?>
            $.get('<?php echo $url; ?>', function(res) {
                $('#backupstatus').html(res);
            });
        }, "2000");
    });
</script>
