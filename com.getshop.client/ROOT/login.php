<?
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
?>

<div class="inner <? echo $notloggedInClass; ?>">
    <form method="POST" action="/index.php" name="loginform" class="loginform">
            <div class="entry">Login</div>
            <div class="form">
                <div class="username">Username / Email<br><input class="tstextfield" name="username" type="textfield"></input></div>
                <div class="password">Password<br><input class="tstextfield" name="password" type="password"></input></div>
                <input type="hidden" name="loginbutton" value="true"/>
                <input class="loginbutton" type="submit" value="login"/>                
            </div>
    </form>
</div>
