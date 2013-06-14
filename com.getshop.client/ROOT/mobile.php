<!DOCTYPE html PUBLIC "-//WAPFORUM//DTD XHTML Mobile 1.2//EN"
"http://www.openmobilealliance.org/tech/DTD/xhtml-mobile12.dtd">


<body>
    <?php
    include '../loader.php';
    $factory = IocContainer::getFactorySingelton();
    $factory->loadJavascriptFiles(false);
    
    if(isset($_POST['username'])) {
        $factory->getApi()->getUserManager()->logOn($_POST['username'], $_POST['password']);
    }
    
    if(!$factory->getApi()->getUserManager()->isLoggedIn()) {
        ?>
        <form action="" method="post">
            <div style="text-align: center;">
                <div><? echo $factory->__f("You are not logged on, please log on."); ?></div>
                <div><input type='text' name="username"></div>
                <div><input type='password' name="password"></div>
                <div><input type='submit' value="Sign in"></div>
            </div>
        </form>
        <?
        return;
    }
    
    $app = $factory->getApplicationPool()->getApplicationsInstancesByNamespace("ns_" . str_replace("-","_",$_GET['app']));
    $app = $app[0];
    
    echo "<div class='applicationarea middle inline' area='middle'>";
    echo "<div class='app ".$app->applicationSettings->appName."' appsettingsid='".$app->applicationSettings->id."' app='".$app->applicationSettings->appName."' appid='".$app->configuration->id."'>";
    $app->renderMobile();
    echo "</div>";
    echo "</div>";
    ?>
</body>
