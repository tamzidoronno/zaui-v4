<?
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$instance = new \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login();
$instance->preProcess();

$settings = $factory->getApplicationPool()->getApplicationSetting("d755efca-9e02-4e88-92c2-37a3413f3f41");
$settingsInstance = $factory->getApplicationPool()->createInstace($settings);
$doubleauth = $settingsInstance->getConfigurationSetting("doubleauthentication") == "true" || isset($_GET['doubleauth']);

$username = "";
$password = "";

if(isset($_GET['username'])) {
    $username = $_GET['username'];
}

if(isset($_GET['password'])) {
    $password = $_GET['password'];
}

if (isset($_POST['username']))
    $username = $_POST['username'];

if (isset($_POST['password']))
    $password = $_POST['password'];

if (isset($_POST['pincoderequest']) && (!$_POST['username'] || !$_POST['password'])) {
    echo "<div class='error_login_problem'>Please enter a valid username and password to request a new pincode.</div>";
}

if (isset($_POST['pincoderequest']) && $_POST['username'] && $_POST['password']) {
    $result = $factory->getApi()->getUserManager()->requestNewPincode($_POST['username'], $_POST['password']);
    if ($result) {
        echo "<div class='error_login_info'>A new pincode has been sent to your phone.</div>";
    }
}

?>

<head>
    <meta name="viewport" content="width=device-width, user-scalable=no">
</head>
<style>
    body { background-color:#3a99d7; }
    input { padding: 3px; width: 80%; font-size: 16px; }
</style>
<style type="text/css">
    .error_login_info,
    .error_login_problem {
        width: 450px;
        box-sizing: border-box;
        border: solid 2px red;
        margin: 0 auto;
        padding: 30px;
        color: #FFF;
        font-size: 16px;
        text-align: center;
    }
    
    .error_login_info {
        border: solid 2px green;
    }
    
    .button {
        -moz-box-shadow:inset 0px 1px 0px 0px #97c4fe;
        -webkit-box-shadow:inset 0px 1px 0px 0px #97c4fe;
        box-shadow:inset 0px 1px 0px 0px #97c4fe;
        background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #3d94f6), color-stop(1, #1e62d0) );
        background:-moz-linear-gradient( center top, #3d94f6 5%, #1e62d0 100% );
        filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#3d94f6', endColorstr='#1e62d0');
        background-color:#3d94f6;
        -webkit-border-top-left-radius:2px;
        -moz-border-radius-topleft:2px;
        border-top-left-radius:2px;
        -webkit-border-top-right-radius:2px;
        -moz-border-radius-topright:2px;
        border-top-right-radius:2px;
        -webkit-border-bottom-right-radius:2px;
        -moz-border-radius-bottomright:2px;
        border-bottom-right-radius:2px;
        -webkit-border-bottom-left-radius:2px;
        -moz-border-radius-bottomleft:2px;
        border-bottom-left-radius:2px;
        text-indent:0;
        display:inline-block;
        color:#ffffff;
        font-family:Arial;
        font-size:16px;
        font-weight:bold;
        font-style:normal;
        height:35px;
        line-height:35px;
        width:82%;
        text-decoration:none;
        text-align:center;
        text-shadow:1px 1px 0px #1570cd;
    }
    .button:hover {
        background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #1e62d0), color-stop(1, #3d94f6) );
        background:-moz-linear-gradient( center top, #1e62d0 5%, #3d94f6 100% );
        filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#1e62d0', endColorstr='#3d94f6');
        background-color:#1e62d0;
    }.button:active {
        position:relative;
        top:1px;
    }</style>

<div style="text-align: center;padding:10px;">
    <img src="https://www.getshop.com/displayImage.php?id=78c64104-ffe0-45d0-a554-87573d34ae7f&height=100&width=100"></img>
                    <br>
                    <br>
    <div class="inner <? echo $notloggedInClass; ?>">
        <div  style="max-width: 400px; width:100%; display:inline-block; background-color:#FFF; box-shadow: 0px 0px 2px #000; border-radius: 5px; padding: 20px; text-align:left;">
            <form id='getshoploginform' method="POST" action="/login.php<? echo $doubleauth ? "?doubleauth=true" : ""; ?>" name="loginform" class="loginform">
                    <div class="form">
                        <div class="username">Username / Email<br><input id='gsloginusername' class="tstextfield" name="username" type="textfield" value='<? echo $username; ?>' style="height:40px;width:100%;"></input></div>
                        <bR>
                        <div class="password">Password<br><input class="tstextfield" name="password" type="password" value='<? echo $password; ?>' style="height:40px;width:100%;"></input></div>
                        <input type="hidden" name="loginbutton" value="true"/>
                        <?
                        if ($doubleauth) {
                        ?>
                        <bR>
                        <div class="pincode">Pincode<br><input class="tstextfield" name="pincode" type="password" value='' style="height:40px;width:100%;"></input></div>
                        <input class="loginbutton" type="submit" name='pincoderequest' value="Request new pincode" style="height:40px; margin-top: 20px;width:100%;"/>                

                        <?
                        }
                        ?>
                        
                        <input class="loginbutton" type="submit" value="login" style="height:40px; margin-top: 20px;width:100%;"/>                
                    </div>
            </form>
        </div>
    </div>
</div>

<? if(isset($_GET['autologin'])) { ?>
<script>
    document.getElementById("getshoploginform").submit();
</script>
<? } ?>

<?
if (ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject() != null) {
    echo "<script>document.location = '/'</script>";
}
?>
<script>
    document.getElementById("gsloginusername").focus();
</script>