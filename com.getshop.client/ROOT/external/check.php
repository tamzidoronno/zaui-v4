<?
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$api = $factory->getApi();
$api->getSedoxProductManager()->login($_POST['username'], $_POST['password']);
$res = $api->getUserManager()->isLoggedIn() ? "true" : "false";
$user = $api->getSedoxProductManager()->getSedoxUserAccount();
if ($user != null) {
    if ($user->canUseExternalProgram) {
        echo "true";
        return;
    }
}
echo "false";
?>