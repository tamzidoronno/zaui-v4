<?
/* @var $api GetShopApi */
$username = "";
$hostname = "";
$password = "";
$failedLogon = false;

if(isset($_POST['logon']) || isset($_POST['username'])) {
    $username = $_POST['username'];
    $password = $_POST['password'];
    $hostname = $_POST['hostname'];
    $loggedon = $api->getArxManager()->logonToArx($hostname, $username, $password);
    if($loggedon) {
        header("location: /arx");
        return;
    } else {
        $failedLogon = true;
    }
}

?>
<center>
<img src="https://no.getshop.com/displayImage.php?id=d40d2676-54bc-41f0-b791-9ddc3d1433a0&crop=true&x=87&x2=675&y=96&y2=630&rotation=0" style="width:80px;">
<h1>Arx services<br>mobilized</h1>
</center>
<form action='/arx/' method='POST' id='logonform'>
    <div class="inputbox">
        Hostname
    <input type="text" value='<? echo $hostname; ?>' name='hostname'>
    </div>
    <div class="inputbox">
        Username
    <input type="text" value='<? echo $username; ?>' name='username'>
    </div>
    <div class="inputbox">
        Password
    <input type="password" value='<? echo $password; ?>' name='password'>
    </div>
    <div class="inputbox">
        <div class='logonbutton'>
            Sign in
        </div>
    </div>
    <input type='submit' value='test' style='display:none;'>
    <div class="inputbox">
    <? 
    if($failedLogon) {
        echo "* Failed to log on, make sure your details are correct.";
    }
    ?>
    </div>
</form>

<div class="inputbox">
    <div class='savedetails'>
        Add another setup
    </div>
</div>
<center>
Created by GetShop<br> post@getshop.com <br>+47 940 10 704
</center>
<style>
    .logonbutton, .savedetails { font-size: 20px; text-transform: uppercase; padding: 5px;text-align: center; padding-top: 20px; padding-bottom: 20px; border-radius: 3px; background-color:#bf580d; margin-top: 15px; color:#fff; }
    .inputbox { margin: 10px; }
    .inputbox input { width: 100%; border-radius: 3px; font-size: 20px; padding: 4px; border: solid 1px #e17f38; }
</style>

<script>
    var hostname = localStorage.getItem('hostname');
    if(hostname && !$('input[name="hostname"]').val()) {
        $('input[name="hostname"]').val(hostname);
    }
    var username = localStorage.getItem('username');
    if(username && !$('input[name="username"]').val()) {
        $('input[name="username"]').val(username);
    }
    var password = localStorage.getItem('password');
    if(hostname && !$('input[name="password"]').val()) {
        $('input[name="password"]').val(password);
    }
    
$('.logonbutton').click( function() {
    $( "#logonform" ).submit();
});

$( "#logonform" ).submit(function() {
    localStorage.setItem('hostname',$('input[name="hostname"]').val());
    localStorage.setItem('username',$('input[name="username"]').val());
    localStorage.setItem('password',$('input[name="password"]').val());
});

$( "#logonform" ).submit(function( event ) {
    $('.logonbutton').html('<i class="fa fa-spin fa-spinner"></i>');
});

</script>

