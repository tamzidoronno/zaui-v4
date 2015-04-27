<?
include_once '../loader.php';
$factory = IocContainer::getFactorySingelton(false);
header('Content-Type: text/css');

 $themeApp = $factory->getApplicationPool()->getSelectedThemeApp();

$done = array();
foreach ($factory->getApplicationPool()->getAllApplicationSettings() as $app) {

    if ($app->type != "ThemeApplication") {
        $appSettingsId = $app->id;
        if (in_array($appSettingsId, $done)) {
            continue;
        }
        doApp($app, false, $factory);
        $done[] = $appSettingsId;
    }
}

doApp($themeApp, true, $factory);
ob_start();
function doApp($app, $isTheme, $factory) {
    $appId = $factory->convertUUIDtoString($app->id);
    $cssFileName = $app->appName . ".css";
    $folder = "../app/" . $appId . "/skin/";

    if (!file_exists("cssfolder")) {
        mkdir("cssfolder");
    }
    if (!file_exists("cssfolder/$appId")) {
        mkdir("cssfolder/$appId");
    }

    if (file_exists($folder)) {
        $files = scandir($folder);

        foreach ($files as $file) {
            $cssFile = "$folder$file";

            if (is_file($cssFile)) {
                if (!strstr($cssFile, ".css")) {
                    continue;
                }
//                    echo "asdfasdf : ".$cssFile."<br/>";
                $cssFile = file_get_contents($cssFile);
                $cssFile = str_replace("{IMAGEFOLDER}", "/showApplicationImages.php?appNamespace=" . urlencode($appId) . "&image=skin/images/", $cssFile);

                if ($isTheme) {
                    if ($factory->isMobile() && $file != "mobile.css") {
                        continue;
                    } else if (!$factory->isMobile() && $file == "mobile.css") {
                        continue;
                    }
                }
                echo $cssFile . "\n\n";
            }
        }
    }
}

$content = ob_get_contents();

echo $content;
?>