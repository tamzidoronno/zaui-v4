<?php
$request_body = file_get_contents('php://input');
$content = file_get_contents($_FILES['upload']['tmp_name']);
$imdata = base64_encode($content);  
?>
{
    "uploaded": true,
    "url": "data:image/x-icon;base64,<?php echo $imdata; ?>"
}