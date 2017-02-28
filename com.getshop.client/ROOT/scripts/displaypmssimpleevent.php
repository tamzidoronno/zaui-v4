<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$readevent = $_GET['readevent'];
$time = "";

if(isset($_GET['time'])) {
    $time = $_GET['time'];
}

$res = $factory->getApi()->getPmsEventManager()->getEntryShort($_GET['engine'], $readevent, $time);
$title = $res->title;
$shortDesc = $res->shortdesc;
$imageid = $res->imageId;
$logoid = $res->logoId;
$category = $res->category;
$contact = $res->contact;

$arrangedBy = $res->arrangedBy;
$location = $res->location;
$starttime = $res->starttime;
$description = $res->description;

if(!$time) {
    foreach($res->dateRanges as $range) {
        $time = strtotime($range->start);
    }
}

$day = date("d", $time);
$month = date("M", $time);

echo "<div class='displayarea'>";
    echo "<h1>" . $title . "</h1>";
    echo "<img id='imgtodisplay' src='/displayImage.php?id=" . $imageid . "'>";
    echo "<p>Om: " . $shortDesc . "</p>";
    if($description) {
        echo "<p>Beksrivelse: " . $description . "</p>";
    }
    echo "<img id='logotodisplay' src='/displayImage.php?id=". $logoid . "'>";
    
    echo "<table><tr>";
    echo "<td class='arrangedBy'>Arrangør: " . $arrangedBy . "</td>";
    echo "<td class='arrangedBy'>Sted: " . $location . "</td>";
    echo "<td class='starttime'>Klokkeslett: " . $starttime . "</td>";
    echo "<td class='date'>Dato: " . $day . ". " . $month . "</td>";
    echo "<td class='contact'>Kontakt: " . $contact . "</td>";
    echo "</tr></table>";
echo "<div>";
?>

<style>
    .displayarea{
        position: relative;
        width:100%;
    }
    .displayarea h1{
        text-align: center;
        color: #4D4D55;
    }
    .displayarea #imgtodisplay{
        margin-left: 2.5%;
        border:1px solid #4D4D55;
        width: 95%;
    }
    .displayarea #logotodisplay{
        margin-right: 2.5%;
        padding:5px;
        border:1px solid #4D4D55;
        width: 10%;
        float:right;
    }
    .displayarea p{
        text-align: justify;
        margin: 2.5%;
        color:#000;
        font-size: 16px;
    }
    .displayarea table{
        width: 95%;
        margin: 2.5%;
    }
    .displayarea tr{

    }
    .displayarea td{
        font-size: 20px;
        color:#4D4D55;
        background-color: #fff;
        
        padding:5px;
        text-align: center;
        width:20%;
        border: 1px solid #4D4D55;
        border-radius: 0;
    }
</style>
    
