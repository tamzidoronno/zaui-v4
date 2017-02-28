<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$lists = $factory->getApi()->getPmsEventManager()->getEventList($_GET['bookingengine']);
$i = 0;
$j = 0;

echo "<table class='bizplay_simpledayeventlist'>";

    foreach($lists as $list) {
            if($j < ($_GET['page'] * 12)) {
                $j++;
                continue;
            }
            $time = strtotime($list->date);
            $halfhour = time()+(60*30);

            if($time < $halfhour) {
                continue;
            }
            if($i > 12) {
                break;
            }


            if(!$list->starttime) {
                $list->starttime = date("H:i", strtotime($list->date));
            }

            $title = $list->title;
            $starttime = $list->starttime;
            $shortDesc = $list->shortdesc;
            $place = $list->place;
            $arrangedBy = $list->arrangedBy;
            $day = date("d", $time);
            $month = date("M", $time);


            echo "<tr class='eventlistElement'>";
                echo "<td class='day'>" . $day . "<br>" . $month . "</td>";
                echo "<td class='content'><span class='title'>" . $title . "</span><br>" . nl2br(trim($shortDesc)) . "</td>";
                echo "<td class='kontakt'><span class='place'><b>Sted: </b> " . $place . "</span><br><br><span class='arrangedby'><b>Arrangør: </b>" . $arrangedBy . "</td>";
                echo "<td class='time'>" . $starttime . "</td>";
            echo "</tr>";
            $i++; 
    }
    
echo "</table>"

?>

<style>
    body{
        background-color:#4D4D55;
        font-family:Arial, Helvetica, sans-serif;
    }
    .heading{
        font-size: 48px;
        text-align: center;
        font-weight:bold;
        
        padding:2px;
        
        color: #4D4D55;
        background-color:#FFF;
    }
    .bizplay_simpledayeventlist{
        width: 100%;
        height:auto;
        
        color: #4D4D55;
        background-color: #FFF; 
        position: relative;
        border-collapse: collapse;
        
        /*border:10px solid #4D4D55;*/
        
    }
    tr:nth-child(odd){background-color: #f1f1f1}
    .eventlistElement td{
        padding: 15px;     
    }
    .eventlistElement td.day{
        text-align: center;
        font-size: 48px;
        font-weight: bold;
        width:10%;
    }
    .eventlistElement td.content{
        text-align: justify;
        font-size: 16px;
        padding-right:50px;
    }
    .eventlistElement td.kontakt{
        width:25%;
    }
    td.content .title{
        font-weight: bold;
        font-size: 24px;
    }
    .eventlistElement td.time{
        text-align: left;
        font-size: 48px;
        font-weight: bold;
        width:10%;
    }
</style>