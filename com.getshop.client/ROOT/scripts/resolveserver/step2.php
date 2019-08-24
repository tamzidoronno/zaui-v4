<h1 style='text-align: center;'>Step 2 / 4 - Check your internet connection</h1>
The most obvious reason we can not reach the server is that the network connection has been cut between us and the server.<br>
<br>
<b>If your devices is connected to the wifi:</b><br>
<ul>
    <li>
        Have your wifi password or wifi name changed since the server was set up? If so the unit won't be able to connect to the wifi anymore.<br>
    </li>
    <li>
        Use your cellphone and try to connect to your wifi network and see if you are able to use the wifi network yourself.
    </li>
    <li>
        Try restarting your wifi gateway.
    </li>
</ul>

<br>
<b>If your device is connected with an ethernet cable:</b>
<ul>
    <li>
        Make sure your network cables are connected correctly, see image below.<br>
    </li>
    <li>
        If possible try to connect the network cable to your laptop and see if the internet connection works as supposed to.
    </li>
</ul>

<img src='images/getshoplockbox3_step2.png' style='width:100%;'>
<span class='continuetostep3button'>The internet connection seems to be fine, continue to step 3</span>
<style>
    .continuetostep3button {
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
$('.continuetostep3button').click(function() {
    var unit = $(this).attr('chooseunit');
    window.location.href='?id=<?php echo $_GET['id']; ?>&step=3&unit=<?php echo $_GET['unit']; ?>';
});
</script>
