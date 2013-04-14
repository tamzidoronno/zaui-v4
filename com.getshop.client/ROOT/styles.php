<?php
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
header('Content-type: text/css');  
header("Cache-Control: must-revalidate");
$theme = $_GET['theme'];
$content = file_get_contents("skin/$theme/styles.css");
$colors = $factory->getStoreConfiguration()->colors;
$content = str_replace("dead01", $colors->textColor, $content);
$content = str_replace("dead02", $colors->baseColor, $content);
$content = str_replace("dead03", $colors->buttonBackgroundColor, $content);
$content = str_replace("dead04", $colors->buttonTextColor, $content);
$content = str_replace("dead05", $colors->backgroundColor, $content);
echo $content;
?>
