<?php
$myfile = fopen("newfile.txt", "w") or die("Unable to open file!");
$txt = file_get_contents('php://input');
fwrite($myfile, $txt);
?>