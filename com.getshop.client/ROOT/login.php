<?
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$instance = new \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login();
$instance->preProcess();
?>

<div class="inner <? echo $notloggedInClass; ?>">
    <form method="POST" action="/login.php" name="loginform" class="loginform">
            <div class="entry">Login</div>
            <div class="form">
                <div class="username">Username / Email<br><input class="tstextfield" name="username" type="textfield"></input></div>
                <div class="password">Password<br><input class="tstextfield" name="password" type="password"></input></div>
                <input type="hidden" name="loginbutton" value="true"/>
                <input class="loginbutton" type="submit" value="login"/>                
            </div>
    </form>
</div>