<h1 style='text-align: center;'>Step 2 / 3 - Restart your device</h1>
Ok, we have established that your internet connection is working as supposed to.<br>
Try to restart the device by disconnecting the power to it,.<br>
<br>
<b>Instruction on how to restart the unit:</b><br>
Disconnect the usb plug from the slot in the picture below (power (3)), and connect it again. That should restart the box.<br>
* The indicator light should start flashing green after 10 seconds (indicator (1)).<br>
* PS: The red light is a power lamp that should always light red, that is not a problem.<br>
* If the green light do not start blinking, you can skip step 3 and go directly to the recovery process.<br>

<img src='images/zwaveserver_step3.png' style='width:100%;'>
<span class='continuetostep4button' style='background-color:green;'>Ok, I have restarted the device, continue to step 3</span>
<span class='continuetorecovery' style='background-color:red;'>The green light is not flashing in (indicator(1)), continue to recovery</span>
<style>
    .continuetostep4button,.continuetorecovery {
        margin-top: 3px;
        border-radius: 0px;
        color: #FFF;
        background: #23314e;
        background-color: #23314e;
        line-height: 34px;
        height: 37px;
         width:100%;
        display:block;
        text-align: center;
        cursor:pointer;
    }
</style>

<script>
$('.continuetostep4button').click(function() {
    window.location.href='?id=<?php echo $_GET['id']; ?>&step=wait&unit=<?php echo $_GET['unit']; ?>';
});
$('.continuetorecovery').click(function() {
    window.location.href='?id=<?php echo $_GET['id']; ?>&step=4&unit=<?php echo $_GET['unit']; ?>';
});
</script>
