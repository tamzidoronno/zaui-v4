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

if ($_GET['file'] == 6)
    $file = "MBM kundemottaker hefte AK.pdf";

if ($_GET['file'] == 7)
    $file = "2.1 Dieselteknikk versjon 14.01.2014.pdf";

if ($_GET['file'] == 8)
    $file = "6.1 Bedriftsledelse pluss - kursunderlag-1.pdf";

if ($_GET['file'] == 9)
    $file = "6.1 Verkstedledelse - kursunderlag.pdf";

if ($_GET['file'] == 10)
    $file = "1.1 Kunnskapsfornying i elektronikk.pdf";

if ($_GET['file'] == 11)
    $file = "1.2 Auto Kommunikasjonsteknikk_2014_LK.pdf";

if (!isset($file)) {
    return;
}

header("Content-Type: application/octet-stream");

header("Pragma: public");
header("Expires: 0");
header("Cache-Control: must-revalidate, post-check=0, pre-check=0");
header("Cache-Control: public");
header("Content-Description: File Transfer");
header("Content-type: application/pdf");
header("Content-Disposition: attachment; filename=\"$file\"");
header("Content-Transfer-Encoding: binary");

$file = "../docs/$file";
$fileSize = filesize($file);
header("Content-Length: $fileSize");

$fp = fopen($file, "r");
while (!feof($fp))
{
    echo fread($fp, 65536);
    flush(); // this is essential for large downloads
} 
fclose($fp); 
?>
