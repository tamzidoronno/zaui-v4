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

ob_start();
if(isset($_SERVER['HTTP_IF_MODIFIED_SINCE'])){
  // if the browser has a cached version of this image, send 304
    header('Last-Modified: '.$_SERVER['HTTP_IF_MODIFIED_SINCE'],true,304);
    exit;
}

$imageLoader = new ImageLoader();
$imageLoader->load($_GET['id']);

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

if($factory->includeSeo()) {
    $imageLoader->output(IMAGETYPE_JPEG);    
} else {
    $imageLoader->output();
}

$PageContent = ob_get_contents();
ob_end_clean();
$HashID = md5($PageContent);
 
header("Content-type: image/png");
header("Cache-Control: max-age=2052000");
header('Expires: '.gmdate('D, d M Y H:i:s \G\M\T', time() + 2052000));
header('ETag: ' . $HashID);


echo $PageContent;
?>