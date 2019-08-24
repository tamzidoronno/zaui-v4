<?php
$url = "http://system.3.0.local.getshop.com/scripts/systembackupstatus.php?id=".$_GET['backupid']."&ip=$ip&password=543gdnt345345GBFDSGFDSernbdbgfdsg6ty545134134fdsafsVBCXS&startrecovery=true";
if($factory->isProductionMode()) {
    $url = "https://system.getshop.com/scripts/systembackupstatus.php?id=".$_GET['backupid'] . "&ip=$ip&password=543gdnt345345GBFDSGFDSernbdbgfdsg6ty545134134fdsafsVBCXS&startrecovery=true";
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
            $url = "/scripts/getbackupstatus.php?id=".$_GET['backupid'];
            ?>
            $.get('<?php echo $url; ?>', function(res) {
                $('#backupstatus').html(res);
            });
        }, "1000");
    });
</script>

<?php
//Messagingtool.
//http://system.3.0.local.getshop.com/scripts/systembackupstatus.php?id=123546&recoverymessage=jappsi
?>