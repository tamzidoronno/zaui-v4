<?php
chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$lists = $factory->getApi()->getPmsEventManager()->getEventList($_GET['bookingengine']);
$i = 0;
$j = 0;

echo "<table class='bizplay_simpledayeventlist'>";
    echo "<div style='font-size:44px;text-align:center;'> HVA SKJER? </div>";
    foreach($lists as $list) {
            if($j < (@$_GET['page'] * 11)) {
                $j++;
                continue;
            }
            $time = strtotime($list->date);
            $halfhour = time()-(60*30);

            if($time < $halfhour) {
                continue;
            }
            if($i > 11) {
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
                echo "<td class='content'><span class='title'>" . $title . "</span><br><span class='place'><b>Sted: </b> ". $place ."</span></td>";
                echo "<td class='tdArranged'><span class='arrangedby'><b>Arrangør: </b>" . $arrangedBy . "</td>";
                echo "<td class='time'>" . $starttime . "</td>";
            echo "</tr>";
            $i++; 
    }
    
echo "</table>"

?>

<style>
    body{
        padding-top:200px;
        padding-bottom: 200px;
        background-color:#fff;
        font-family:Arial, Helvetica, sans-serif;
        font-size:20px;
    }
    .bizplay_simpledayeventlist{
        width: 100%;
        height:auto;
        
        color: #4D4D55;
        background-color: #FFF; 
        position: relative;
        border-collapse: collapse;  
    }
    tr:nth-child(odd){background-color: #f1f1f1}
    .eventlistElement td{
        height:150px;
        padding: 15px;     
    }
    .eventlistElement td.day{
        text-align: center;
        font-size: 32px;
        font-weight: bold;
        width:10%;
    }
    td.content .title{
        font-weight: bold;
        font-size: 32px;
    }
    .eventlistElement td.time{
        text-align: left;
        font-size: 32px;
        font-weight: bold;
        width:10%;
    }
    .eventlistElement .tdArranged{
        width:28%;
    }
    .eventlistElement .arrangedby,
    .eventlistElement .place{
        font-size:20px;
    }
</style>