<?
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$api = $factory->getApi();
$api->getSedoxProductManager()->login($_POST['username'], $_POST['password']);
$res = $api->getUserManager()->isLoggedIn() ? "true" : "false";
$user = $api->getSedoxProductManager()->getSedoxUserAccount();
if ($user != null) {
    \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::setLoggedOn($api->getUserManager()->getLoggedOnUser());
    if ($user->canUseExternalProgram) {
        echo "true";
        return;
    }
}
echo "false";
?>