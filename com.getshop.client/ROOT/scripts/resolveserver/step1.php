<h1 style='text-align: center;'>Step 1 / 4 - Identify your device</h1>
To be able to help you as best as possible to recover your lost device you will need to tell us what device you have. Please locate the device named <b><?php echo $name; ?></b> and choose the device below.

<div>
    <div class='serverbox'>
        <div><b>Z-wave server</b></div>
        <img class='image' src='https://sophos.files.wordpress.com/2018/06/shutterstock_495697993.jpg?w=640'></img>
        <div class='description'>The z-wave server is a small unit that are usually connected to a powerbank.</div>
        <div class='chooseunitbutton' chooseunit='zwaveserver'>This is the unit im trying to recover, please proceed</div>
    </div>
    <div class='serverbox'>
        <div><b>GetShop lock box 1</b></div>
        <img class='image' src='https://sophos.files.wordpress.com/2018/06/shutterstock_495697993.jpg?w=640'></img>
        <div class='description'>The getshop lock box 1 is the first version of the GetShop lock box, it was put into production in 2015 until 2018.</div>
        <div class='chooseunitbutton' chooseunit='getshoplockbox1'>This is the unit im trying to recover, please proceed</div>
    </div>
    <div class='serverbox'>
        <div><b>GetShop lock box 2</b></div>
        <img class='image' src='https://sophos.files.wordpress.com/2018/06/shutterstock_495697993.jpg?w=640'></img>
        <div class='description'>The getshop lock box 2 is the second version of the GetShop lock box, it was put into production in 2018.</div>
        <div class='chooseunitbutton' chooseunit='getshoplockbox2'>This is the unit im trying to recover, please proceed</div>
    </div>
    <div class='serverbox'>
        <div><b>GetShop lock box 3</b></div>
        <img class='image' src='https://sophos.files.wordpress.com/2018/06/shutterstock_495697993.jpg?w=640'></img>
        <div class='description'>The getshop lock box 3 is the second version of the GetShop lock box, it was put into production in 2019.</div>
        <div class='chooseunitbutton' chooseunit='getshoplockbox3'>This is the unit im trying to recover, please proceed</div>
    </div>
    <div class='serverbox'>
        <div><b>Pos/printer server</b></div>
        <img class='image' src='https://sophos.files.wordpress.com/2018/06/shutterstock_495697993.jpg?w=640'></img>
        <div class='description'>The pos/printer server is a server that handles communication between the cloud and the payment terminals / reciept printers.</div>
        <div class='chooseunitbutton' chooseunit='zwaveserver'>This is the unit im trying to recover, please proceed</div>
    </div>
</div>

<style>
.serverbox {
    background-color:#efefef;margin: 10px; padding: 10px;
    height: 120px;
}

.serverbox .image {
    width: 100px; height: 100px;
    float:left; 
    margin-right: 10px;
}
.serverbox .chooseunitbutton {
    margin-top: 10px;
    color:blue;
    cursor:pointer;
}
</style>
<script>
$('.chooseunitbutton').click(function() {
    var unit = $(this).attr('chooseunit');
    window.location.href='?id=<?php echo $_GET['id']; ?>&step=2&unit='+unit;
});
</script>
