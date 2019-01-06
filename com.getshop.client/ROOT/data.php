<?php
$ret = array();
$ret['instanceid'] = isset($_POST['core']['instanceid']) ? $_POST['core']['instanceid'] : "";

include '../loader.php';
session_start();
$pageFactory = new \PageFactory($_POST['gs_getshopmodule']);
$page = $pageFactory->getPage($_POST['core']['pageid']);
$page->setModuleId();

$timezone =  $pageFactory->getApi()->getStoreManager()->getMyStore()->timeZone;
if($timezone) {
    date_default_timezone_set($timezone);
}

if (isset($_POST['data']['gs_getvariables']) && $_POST['data']['gs_getvariables']) {
    $_GET = json_decode($_POST['data']['gs_getvariables'], TRUE);
}

// UPDATE MODAL VARIABLES
if (isset($_POST['data']['gs_show_modal'])) {
    $_SESSION['gs_currently_showing_modal'] = $_POST['data']['gs_show_modal'];
    foreach ($_POST['data'] as $variableName => $value) {
        @$_SESSION['modal_variable_'.$_SESSION['gs_currently_showing_modal']][$variableName] = $value;
    }
}

// modal closes
if (isset($_POST['event']) && $_POST['event'] == "gs_close_modal") {
    unset($_SESSION['gs_currently_showing_modal']);
    unset($_SESSION['modal_variable_'.$_POST['data']['modalname']]);
}

if (!isset($_POST['core']['instanceid'])) {
    $page->renderPage();
    die();
}

$appInstance = $page->getAppInstance($_POST['core']['instanceid']);

if (isset($_POST['core']['appid2']) && $_POST['core']['appid2']) {
    $appInstance = $page->getExtraApplication($_POST['core']['appid2']);
}

if(!$appInstance) {
    $factory = IocContainer::getFactorySingelton();
    $apps = $factory->getApi()->getStoreApplicationPool()->getAvailableApplications();
    foreach($apps as $app) {
        if($app->id == $_POST['core']['instanceid']) {
            $appInstance = $factory->getApplicationPool()->createInstace($app);
        }
    }
}

if(isset($_POST['core']['convertDataToRawPost']) && $_POST['core']['convertDataToRawPost']) {
    foreach($_POST['data'] as $key => $val) {
        $_POST[$key] = $val;
    }
}

if (isset($_POST['event']) && $_POST['event']) {
    $appInstance->{$_POST['event']}();
}

$appInstance->page = $page;

if (isset($_POST['synchron'])) {
    die();
}

ob_start();
$appInstance->render();
$html = ob_get_contents();
ob_end_clean();
$ret['content'] = $html;
$ret['gs_alsoupdate'] = $appInstance->gsAlsoUpdate();
if (isset($_POST['core']['appid2']) && $_POST['core']['appid2']) {
    $ret['gs_alsoupdate'] = array();
    $ret['gs_alsoupdate'][] = $_POST['core']['fromappid'];
}
echo json_encode($ret);

unset($_SESSION['firstloadpage']);
?>