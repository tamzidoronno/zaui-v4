<h1 style='text-align: center;'>Step 4 / 4 - Start recovery</h1>
It seems like your unit did not get back online. We are now left with two options.
<ul>
    <li>Try again</li>
    <li>Start a recovery process</li>
</ul>
<div>
    <b>1. Try again</b>
    <ul>
        <li class='retryrestart'>Click here to try again</li>
    </ul>
    
</div>

<div>
    <b>2. Recover the unit.</b>
<ul>
    <li>Find your backup usb pen</li>
    <li>Remove the old usb pen</li>
    <li>Remove insert the new usb pen</li>
    <li>Restart the unit</li>
    <li>Enter the id that comes with the back usb pen</li>
</ul>
    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    <input type='text' placeholder='GS-XXXXXX' id='recoveryid'><input type='button' class='startrecoverbutton' value='Start recovery process'>
</div>

<style>
    .retryrestart { color:blue; cursor:pointer; }
</style>

<script>
$('.retryrestart').click(function() {
    var unit = $(this).attr('chooseunit');
    window.location.href='?id=<?php echo $_GET['id']; ?>&step=3&unit=<?php echo $_GET['unit']; ?>';
});
$('.startrecoverbutton').click(function() {
    window.location.href='?id=<?php echo $_GET['id']; ?>&step=startrecovery&unit=<?php echo $_GET['unit']; ?>&backupid='+$('#recoveryid').val();
});
</script>

<script>
    
</script>