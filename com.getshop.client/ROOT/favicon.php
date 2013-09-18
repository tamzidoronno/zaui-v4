<?php
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
header('content-type:image/x-icon');


if (isset($factory->getSettings()->{'favicon'})) {
    $content = file_get_contents($factory->getSettings()->{'favicon'}->value);
    echo $content;
}
?>
