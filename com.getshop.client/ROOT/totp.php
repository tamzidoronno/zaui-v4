<?
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$instance = new \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login();

$redirect = "";
if(isset($_GET['redirectto'])) {
    $redirect = $_GET['redirectto'];
}
if(isset($_POST['redirect'])) {
    $redirect = $_POST['redirect'];
}

$cookieFound = isset($_COOKIE['gstoken']) ? "found" : "not found";
echo "<center><b>$cookieFound</b></center>";

if (isset($_COOKIE['gstoken'])) {
    $user = $factory->getApi()->getUserManager()->logonUsingToken($_COOKIE['gstoken']);
    if ($user) {
        \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::setLoggedOn($user);
        if($redirect) {
            header('location: ' . $redirect);
        }
    } else {
        unset($_COOKIE['gstoken']);
    }
}

$settings = $factory->getApplicationPool()->getApplicationSetting("d755efca-9e02-4e88-92c2-37a3413f3f41");
$settingsInstance = $factory->getApplicationPool()->createInstace($settings);


$username = "";
$password = "";
$verificationcode = "";

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

if (isset($_POST['verificationcode']))
    $verificationcode = $_POST['verificationcode'];

if ($verificationcode && $username && $password) {
    $instance->totpLoginProcess($username, $password, $verificationcode);
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
            
                
                <?php
                if (ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject() != null) {
                    $user = ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject();
                    echo "<center>Welcome back ".$user->fullName."<br>"
                            . "Please wait while we are logging you on.</center>";
                } else {
                    ?>
                    <form id='getshoploginform' method="POST" action="/totp.php" name="loginform" class="loginform">
                        <input type="hidden" name="redirect" value="<?php echo $redirect; ?>">
                        <div class="form">
                            <div class="username">Username / Email<br><input id='gsloginusername' class="tstextfield" name="username" type="textfield" value='<? echo $username; ?>' style="height:40px;width:100%;"></input></div>
                            <bR>
                            <div class="password">Password<br><input class="tstextfield" name="password" type="password" value='<? echo $password; ?>' style="height:40px;width:100%;"></input></div>
                            <input type="hidden" name="loginbutton" value="true"/>
                            <bR>
                            <div class="verificationcode">Verification code<br><input class="tstextfield" name="verificationcode" type="password" value='' style="height:40px;width:100%;"></input></div>

                            <input class="loginbutton" type="submit" value="login" style="height:40px; margin-top: 20px;width:100%;"/>                
                        </div>
                    </form>
                <?php
                }
                
                ?>
            
        </div>
    </div>
</div>
<?php

if(isset($_POST['username'])) {
    if(!ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()) {
        echo "<center><h1 id='loginfailed'>Login failed</h1></center>";
        ?>
        <script>
            setTimeout(function() {
                var element = document.getElementById("loginfailed");
                element.parentNode.removeChild(element);
            }, "1000");
        </script>
        <?php
    }
}

if (ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject() != null) {
    $productionMode = $factory->getApi()->getStoreManager()->isProductMode();
    
    $actual_link = "https://$_SERVER[HTTP_HOST]/totp.php";
    
    if (!$productionMode) {
        $actual_link = "";
    }
    
    if (!isset($_COOKIE['gstoken'])) {
        $tokenId = $factory->getApi()->getUserManager()->createTokenAccess();
        
        setcookie( 'gstoken', $tokenId, time()+60*60*24*60, '/totp.php', $_SERVER['HTTP_HOST'], $productionMode, true);
    } else {
        setcookie( 'gstoken', $_COOKIE['gstoken'], time()+60*60*24*60, '/totp.php', $_SERVER['HTTP_HOST'], $productionMode, true);
    }
    
    if(isset($_POST['redirect'])) {
        echo "<script>document.location = '".$_POST['redirect']."';</script>";
    }
    
    $modules = $factory->getApi()->getPageManager()->getModules();
    if(sizeof($modules) > 1) {
        echo "<center><h1>Select a module</h1></center>";
        foreach($modules as $module) {
            if($module->name == "PMS") {
                echo "<a class='gs_ignorenavigate' href='/pms.php' style='text-decoration:none;'>";
                echo "<div style='background-color:#fff; border: solid 1px #bbb;margin: auto; width: 200px; padding: 10px;margin-bottom: 5px; cursor:pointer;'><span class='" . $module->fontAwesome . "'></span>PMS</div>";
                echo "</a>";
            } elseif($module->name == "Salespoint") {
                echo "<a class='gs_ignorenavigate' href='/pos.php' style='text-decoration:none;'>";
                echo "<div style='background-color:#fff; border: solid 1px #bbb;margin: auto; width: 200px; padding: 10px;margin-bottom: 5px; cursor:pointer;'><span class='" . $module->fontAwesome . "'></span>Salespoint</div>";
                echo "</a>";
            } elseif(strtolower($module->name) == "apac") {
                echo "<a class='gs_ignorenavigate' href='/apac.php' style='text-decoration:none;'>";
                echo "<div style='background-color:#fff; border: solid 1px #bbb;margin: auto; width: 200px; padding: 10px;margin-bottom: 5px; cursor:pointer;'><span class='" . $module->fontAwesome . "'></span>APAC</div>";
                echo "</a>";
            } else {
                echo "<a class='gs_ignorenavigate' href='/?changeGetShopModule=".strtolower($module->id)."&scopeid=NEW' style='text-decoration:none;'>";
                echo "<div style='background-color:#fff; border: solid 1px #bbb;margin: auto; width: 200px; padding: 10px;margin-bottom: 5px; cursor:pointer;'><span class='" .$module->fontAwesome . "'></span> " . $module->name . "</div>";
                echo "</a>";    
            }
        }
    } else {
        $module = $modules[0];
        echo "<script>document.location = '/?changeGetShopModule=".strtolower($module->id)."&scopeid=NEW'</script>";
    }
}
?>
<script>
    document.getElementById("gsloginusername").focus();
</script>