<?php
$factory = IocContainer::getFactorySingelton(false);
$settings = $factory->getApi()->getPageManager()->getApplicationsBasedOnApplicationSettingsId("8cc26060-eef2-48ac-8174-914f533dc7ed");
$instances = [];
foreach ($settings as $setting) {
    $instances[] = $factory->getApplicationPool()->createAppInstance($setting);
}

$activeInstance = null;
foreach ($instances as $instance) {
    if ($instance->getCurrentUser()) {
        $activeInstance = $instance;
    }
}

if (!$activeInstance) {
    return;
}

$activeInstance->ping();
?>