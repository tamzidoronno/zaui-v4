<h1 style='text-align: center;'>Step 3 / 4 - Restart your device</h1>
Ok, we have established that your internet connection is working as supposed to.<br>
Try to restart the device by disconnecting the power to it.<br>
<br>
<b>Instruction on how to restart the unit:</b><br>
Disconnect the usb plug from the slot in the picture below, and connect it again. That should restart the box.<br>
<img src='images/getshoplockbox3_step3.png' style='width:100%;'>
<span class='continuetostep4button'>Ok, I have restarted the device, continue to step 4</span>
<style>
    .continuetostep4button {
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
</script>
