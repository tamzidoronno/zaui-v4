<!DOCTYPE html PUBLIC "-//WAPFORUM//DTD XHTML Mobile 1.2//EN"
"http://www.openmobilealliance.org/tech/DTD/xhtml-mobile12.dtd">


<body>
    <?php
    include '../loader.php';
    $factory = IocContainer::getFactorySingelton();
    $factory->loadJavascriptFiles(false);
    $app = $factory->getApplicationPool()->getApplicationsInstancesByNamespace("ns_" . str_replace("-","_",$_GET['app']));
    $app = $app[0];
    echo "<div class='applicationarea middle inline' area='middle'>";
    echo "<div class='app ".$app->applicationSettings->appName."' appsettingsid='".$app->applicationSettings->id."' app='".$app->applicationSettings->appName."' appid='".$app->configuration->id."'>";
    $app->renderMobile();
    echo "</div>";
    echo "</div>";
    ?>
</body>
