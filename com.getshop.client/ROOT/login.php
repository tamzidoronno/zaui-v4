<?
include '../loader.php';

$factory = IocContainer::getFactorySingelton();

if (isset($_GET['refcode'])) {
    $user = $factory->getApi()->getUserManager()->logonUsingRefNumber($_GET['refcode']);
    
    if ($user) {
        ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::setLoggedOn($user);
    }
}

$instance = new \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login();
$instance->preProcess();

$settings = $factory->getApplicationPool()->getApplicationSetting("d755efca-9e02-4e88-92c2-37a3413f3f41");
$settingsInstance = $factory->getApplicationPool()->createInstace($settings);
$doubleauth = $settingsInstance->getConfigurationSetting("doubleauthentication") == "true" || isset($_GET['doubleauth']);

$username = "";
$password = "";

$redirect = "";
if(isset($_GET['redirectto'])) {
    $redirect = $_GET['redirectto'];
}
if(isset($_POST['redirect'])) {
    $redirect = $_POST['redirect'];
}

if (isset($_GET['username'])) {
    $username = $_GET['username'];
}

if (isset($_GET['password'])) {
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
    .form-style-6{
        font: 95% Arial, Helvetica, sans-serif;
        max-width: 400px;
        margin: 10px auto;
        padding: 16px;
        background: #F7F7F7;
    }
    .form-style-6 h1{
        background: #43D1AF;
        padding: 20px 0;
        font-size: 140%;
        font-weight: 300;
        text-align: center;
        color: #fff;
        margin: -16px -16px 16px -16px;
    }
    .form-style-6 input[type="text"],
    .form-style-6 input[type="password"],
    .form-style-6 input[type="date"],
    .form-style-6 input[type="datetime"],
    .form-style-6 input[type="email"],
    .form-style-6 input[type="number"],
    .form-style-6 input[type="search"],
    .form-style-6 input[type="time"],
    .form-style-6 input[type="url"],
    .form-style-6 textarea,
    .form-style-6 select 
    {
        -webkit-transition: all 0.30s ease-in-out;
        -moz-transition: all 0.30s ease-in-out;
        -ms-transition: all 0.30s ease-in-out;
        -o-transition: all 0.30s ease-in-out;
        outline: none;
        box-sizing: border-box;
        -webkit-box-sizing: border-box;
        -moz-box-sizing: border-box;
        width: 100%;
        background: #fff;
        margin-bottom: 4%;
        border: 1px solid #ccc;
        padding: 3%;
        color: #555;
        font: 95% Arial, Helvetica, sans-serif;
    }
    .form-style-6 input[type="text"]:focus,
    .form-style-6 input[type="password"]:focus,
    .form-style-6 input[type="date"]:focus,
    .form-style-6 input[type="datetime"]:focus,
    .form-style-6 input[type="email"]:focus,
    .form-style-6 input[type="number"]:focus,
    .form-style-6 input[type="search"]:focus,
    .form-style-6 input[type="time"]:focus,
    .form-style-6 input[type="url"]:focus,
    .form-style-6 textarea:focus,
    .form-style-6 select:focus
    {
        box-shadow: 0 0 5px #43D1AF;
        padding: 3%;
        border: 1px solid #43D1AF;
    }

    .form-style-6 input[type="submit"],
    .form-style-6 input[type="button"]{
        box-sizing: border-box;
        -webkit-box-sizing: border-box;
        -moz-box-sizing: border-box;
        width: 100%;
        padding: 3%;
        background: #43D1AF;
        border-bottom: 2px solid #30C29E;
        border-top-style: none;
        border-right-style: none;
        border-left-style: none;    
        color: #fff;
    }
    .form-style-6 input[type="submit"]:hover,
    .form-style-6 input[type="button"]:hover{
        background: #2EBC99;        background: #43D1AF;
    }

    .error {
        color: red;
        margin-bottom: 15px;
    }
</style>

<div style="text-align: center;padding:10px;">
    <img src="https://www.getshop.com/displayImage.php?id=78c64104-ffe0-45d0-a554-87573d34ae7f&height=100&width=100"></img>
    <br>
    <br>
    <div class="inner <? echo $notloggedInClass; ?>">
        <div  class="form-style-6" >
            <h1>
                <? 
                $text = ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject() != null ? "Welcome Back" : "Please log in";
                echo $factory->__f($text); ?>
            
            </h1>
            <form id='getshoploginform' method="POST" action="/login.php<? echo $doubleauth ? "?doubleauth=true" : ""; ?>" name="loginform" class="loginform">
                <input type="hidden" name="redirect" value="<?php echo $redirect; ?>">
                <?php
                if (ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject() != null) {
                    $user = ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject();

                    $modules = array();
                    
                    if (ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject() != null) {
                        $modules = $factory->getApi()->getPageManager()->getModules();
                    }
                    
                    if (sizeof($modules) > 1) {
                        echo "Please select a module: <br/><br/>";
                        foreach ($modules as $module) {
                            if($module->name == "PMS") {
                                echo "<a class='gs_ignorenavigate' href='/pms.php' style='text-decoration:none;'>";
                                echo "<div style='background-color:#fff; border: solid 1px #bbb;margin: auto; width: 200px; padding: 10px;margin-bottom: 5px; cursor:pointer;'><span class='" . $module->fontAwesome . "'></span>PMS</div>";
                                echo "</a>";
                            } elseif($module->name == "Salespoint") {
                                echo "<a class='gs_ignorenavigate' href='/pos.php' style='text-decoration:none;'>";
                                echo "<div style='background-color:#fff; border: solid 1px #bbb;margin: auto; width: 200px; padding: 10px;margin-bottom: 5px; cursor:pointer;'><span class='" . $module->fontAwesome . "'></span>Salespoint</div>";
                                echo "</a>";
                            } elseif (strtolower($module->id) == "pmsconference") {
                                echo "<a class='gs_ignorenavigate' href='/pmsconference.php'><div class='gs_framework_module $moduleActiveClass'><i class='fa fa-group'></i><br/> Conference</div></a>";
                            } elseif(strtolower($module->name) == "apac") {
                                echo "<a class='gs_ignorenavigate' href='/apac.php' style='text-decoration:none;'>";
                                echo "<div style='background-color:#fff; border: solid 1px #bbb;margin: auto; width: 200px; padding: 10px;margin-bottom: 5px; cursor:pointer;'><span class='" . $module->fontAwesome . "'></span>APAC</div>";
                                echo "</a>";
                            } elseif(strtolower($module->name) == "invoicing") {
                                echo "<a class='gs_ignorenavigate' href='/invoicing.php' style='text-decoration:none;'>";
                                echo "<div style='background-color:#fff; border: solid 1px #bbb;margin: auto; width: 200px; padding: 10px;margin-bottom: 5px; cursor:pointer;'><span class='" . $module->fontAwesome . "'></span>Invoicing</div>";
                                echo "</a>";
                            } elseif(strtolower($module->name) == "settings") {
                                echo "<a class='gs_ignorenavigate' href='/settings.php' style='text-decoration:none;'>";
                                echo "<div style='background-color:#fff; border: solid 1px #bbb;margin: auto; width: 200px; padding: 10px;margin-bottom: 5px; cursor:pointer;'><span class='" . $module->fontAwesome . "'></span>Settings</div>";
                                echo "</a>";
                            } else {
                                echo "<a class='gs_ignorenavigate' href='/?changeGetShopModule=" . strtolower($module->id) . "&scopeid=NEW' style='text-decoration:none;'>";
                                echo "<div style='background-color:#fff; border: solid 1px #bbb;margin: auto; width: 200px; padding: 10px;margin-bottom: 5px; cursor:pointer;'><span class='" . $module->fontAwesome . "'></span> " . $module->name . "</div>";
                                echo "</a>";
                            }
                        }
                    } else {
                        echo "<br> Please wait while we are logging you on.</center>";
                        $module = $modules[0];
                        echo "<script>document.location = '/?changeGetShopModule=" . strtolower($module->id) . "&scopeid=NEW'</script>";
                    }
                } else {
                    ?>
                    <div class="form">
                        <div class="username"><input id='gsloginusername' class="tstextfield" name="username" type="text" placeholder="Username / Email" value='<? echo $username; ?>' style="height:40px;width:100%;"></input></div>
                        <bR>
                        <div class="password"><input class="tstextfield" name="password" type="password" placeholder="Password" value='<? echo $password; ?>' style="height:40px;width:100%;"></input></div>
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
                    <?php
                }
                ?>
            </form>
        </div>
    </div>
</div>
<div style='text-align: center; '>
    <?php
    $gets = "";
    foreach($_GET as $k => $val) {
        $gets .= $k . "=" . $val . "&";
    }
    ?>
    <a href='totp.php?<?php echo $gets; ?>' style='color:blue; text-decoration: none;'>Totp login</a>
</div>
<?php
if (isset($_POST['username'])) {
    if (!ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()) {
        echo "<center><h1 id='loginfailed'>Login failed</h1></center>";
        ?>
        <script>
            setTimeout(function () {
                var element = document.getElementById("loginfailed");
                element.parentNode.removeChild(element);
            }, "1000");
        </script>
        <?php
    }
}

if (isset($_GET['autologin'])) {
    ?>
    <script>
        document.getElementById("getshoploginform").submit();
    </script>
<? }
?>
<script>
    document.getElementById("gsloginusername").focus();
</script>