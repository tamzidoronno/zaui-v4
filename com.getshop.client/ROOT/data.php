<?php
/*
 *
 * we send very complex post data a ttimes and php seems to handle that not too well sometimes....
 * so we double check and fix :)
*/

if(!isset($_POST['core']))
{
    function get_real_post() {
        function set_nested_value(&$arr, &$keys, &$value) {
            $key = array_shift($keys);
            if (count($keys)) {
                // Got deeper to go
                if (!array_key_exists($key, $arr)) {
                    // Make sure we can get deeper if we've not hit this key before
                    $arr[$key] = array();
                } elseif (!is_array($arr[$key])) {
                    // This should never be relevant for well formed input data
                    throw new Exception("Setting a value and an array with the same key: $key");
                }
                set_nested_value($arr[$key], $keys, $value);
            } elseif (empty($key)) {
                // Setting an Array
                $arr[] = $value;
            } else {
                // Setting an Object
                $arr[$key] = $value;
            }
        }

        $input = array();
        $parts = array();
        $pairs = explode("&", file_get_contents("php://input"));
        foreach ($pairs as $pair) {
            $key_value = explode("=", $pair, 2);
            preg_match_all("/([a-zA-Z0-9]*)(?:\[([^\[\]]*(?:(?R)[^\[\]]*)*)\])?/", urldecode($key_value[0]), $parts);
            $keys = array($parts[1][0]);
            if (!empty($parts[2][0])) {
                array_pop($parts[2]); // Remove the blank one on the end
                $keys = array_merge($keys, $parts[2]);
            }
            $value = urldecode($key_value[1]);
            if ($value == "true") {
                $value = true;
            } else if ($value == "false") {
                $value = false;
            } else if (is_numeric($value)) {
                if (strpos($value, ".") !== false) {
                    $num = floatval($value);
                } else {
                    $num = intval($value);
                }
                if (strval($num) === $value) {
                    $value = $num;
                }
            }
            set_nested_value($input, $keys, $value);
        }
        return $input;
    }
    $_POST = get_real_post();
}

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
    if(isset($_POST['core']['fromappid'])) {
        $ret['gs_alsoupdate'][] = $_POST['core']['fromappid'];
    }
}
echo json_encode($ret);

unset($_SESSION['firstloadpage']);
?>