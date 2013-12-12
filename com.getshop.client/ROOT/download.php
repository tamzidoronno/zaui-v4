<?php
if (!isset($_GET['file']))
    return;

if ($_GET['file'] == 1)
    $file = "BOSCH ESI 2.0 Kursunderlag.pdf";

if ($_GET['file'] == 2) 
    $file = "Kursevaluering.pdf";

if ($_GET['file'] == 3)
    $file = "Oppgaver ESI Autoakademiet.pptx";

if ($_GET['file'] == 4)
    $file = "7.1 Innforing Hybrid og EV teknikk 1.0.pdf";

if ($_GET['file'] == 5)
    $file = "Bransjestandard  2011.odt nr2.pdf 12.12.13.pdf";

if (!isset($file)) {
    return;
}

header("Content-Type: application/octet-stream");

//$file = $_GET["file"] .".pdf";
header("Content-Disposition: attachment; filename=$file");   
header("Content-Type: application/octet-stream");
header("Content-Type: application/download");
header("Content-Description: File Transfer");            
$file = "../docs/$file";
header("Content-Length: " . filesize($file));
flush(); // this doesn't really matter.
$fp = fopen($file, "r");
while (!feof($fp))
{
    echo fread($fp, 65536);
    flush(); // this is essential for large downloads
} 
fclose($fp); 
?>