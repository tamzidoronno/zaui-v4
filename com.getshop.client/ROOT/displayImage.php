<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of handler
 *
 * @author ktonder
 */
ob_start();
include '../loader.php';
session_cache_limiter('none');

if (!is_string($_GET['id']) || (preg_match('/^[\d_]*[0-9A-F]{8}-[0-9A-F]{4}-4[0-9A-F]{3}-[89AB][0-9A-F]{3}-[0-9A-F]{12}[.\w]*$/i', $_GET['id']) !== 1)) {
    echo "Invalid id";
    return;
}
$id = explode('.',$_GET['id']);
$imageId = $id[0];

$factory = IocContainer::getFactorySingelton();
if(!$factory->getApi()->getUUIDSecurityManager()->hasAccess($imageId, true, false)) {
    echo "Access denied";
    return;
}


ob_start();

$imageLoader = new ImageLoader();
$imageLoader->load($imageId);


if (isset($_GET['rotation'])) {
    $imageLoader->rotate($_GET['rotation']);
}
if(isset($_GET['zoom'])) {
    $imageLoader->zoom(true);
}

if(isset($_GET['crop'])) {
    $imageLoader->cropImage($_GET['x'], $_GET['y'], $_GET['x2'], $_GET['y2']);
}

if(isset($_GET['width']) && isset($_GET['height'])) {
    $imageLoader->resize($_GET['width'], $_GET['height']);
} elseif(isset($_GET['height'])) {
    $imageLoader->resizeToHeight($_GET['height']);
} elseif(isset($_GET['width'])) {
    $imageLoader->resizeToWidth($_GET['width']);
}

if (!$imageLoader->found()) {
    $img = $factory->getApi()->getImageManager()->getBase64EncodedImageLocally($imageId);
    $img = str_replace("data:image/png;base64,", "", $img);
    echo base64_decode($img);
} else {
    $imageLoader->output();
}


$PageContent = ob_get_contents();
$HashID = md5($PageContent);

header("Content-type: image/png");

header("Cache-Control: max-age=360");
header('Expires: '.gmdate('D, d M Y H:i:s \G\M\T', time() + 360));
header('ETag: ' . $HashID);


ob_end_clean();
echo $PageContent;
?>