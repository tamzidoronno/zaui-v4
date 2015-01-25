<?
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$instance = new \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login();
$instance->preProcess();

$username = "";
$password = "";
if(isset($_GET['username'])) {
    $username = $_GET['username'];
}
if(isset($_GET['password'])) {
    $password = $_GET['password'];
}

?>

<div class="inner <? echo $notloggedInClass; ?>">
    <form id='getshoploginform' method="POST" action="/login.php" name="loginform" class="loginform">
            <div class="entry">Login</div>
            <div class="form">
                <div class="username">Username / Email<br><input class="tstextfield" name="username" type="textfield" value='<? echo $username; ?>'></input></div>
                <div class="password">Password<br><input class="tstextfield" name="password" type="password" value='<? echo $password; ?>'></input></div>
                <input type="hidden" name="loginbutton" value="true"/>
                <input class="loginbutton" type="submit" value="login"/>                
            </div>
    </form>
</div>

<? if(isset($_GET['autologin'])) { ?>
<script>
    document.getElementById("getshoploginform").submit();
</script>
<? } ?>