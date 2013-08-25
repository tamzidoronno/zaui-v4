<div data-role="content">
    
    <?php
    $calendar = new ns_6f3bc804_02a1_44b0_a17d_4277f0c6dee8\Calendar();
    $calendar->preProcess();
    $calendar->initMonth();
    
    $month = $calendar->month;
    $year = $calendar->year;
    
    $running_day = date('w', mktime(0, 0, 0, $month, 1, $year));
    $days_in_month = date('t', mktime(0, 0, 0, $month, 1, $year));
    $days_in_this_week = 1;
    $day_counter = 0;
    ?>
    <p>Auto Akademiet er en frittstående gruppe som samarbeider med ulike grossister. Dette gjør at kursbredden og aktiviteter er blant bransjens største. Våre kurs har stort fokus på læringsutbytte. Det benyttes de mest riktige og effektive læringsmodellene</p>
    <table cellspacing="0">
        <thead>
            <tr>
                <th>Søn</th>
                <th>Man</th><th>Tir</th><th>Ons</th>
                <th>Tor</th><th>Fre</th><th>Lør</th>     
            </tr>
        </thead>
        <?
        for ($x = 0; $x < $running_day; $x++) {
            echo '<td></td>';
            $days_in_this_week++;
        }
        
        for ($list_day = 1; $list_day <= $days_in_month; $list_day++) {
            
           
            if ($calendar->monthObject) {
                $day = $calendar->getDay($list_day);
                $containsdata = (count($day->entries) == 0) ? '' : 'date_has_event';
                $data = "";

                foreach ($day->entries as $entry) {
                    $eventColor = isset($entry->color) ? 'background-color: '.$entry->color : $eventColor;
                    $data .= "<div>".$entry->title."</div>";
                }
            }

            
            echo "<td class='$containsdata'>$list_day</td>";
            if ($running_day == 6) {
                echo '</tr>';
                if (($day_counter + 1) != $days_in_month) {
                    echo '<tr class="">';
                }
                $running_day = -1;
                $days_in_this_week = 0;
            }
            
            $days_in_this_week++; 
            $running_day++;
            $day_counter++;
        }
        
        if ($days_in_this_week < 8 && $days_in_this_week != 1 ) {
            for ($x = 1; $x <= (8 - $days_in_this_week); $x++) {
                echo  '<td class="calendar-day-np">&nbsp;</td>';
            }
        }
        ?>
        
        <tfoot>
            <th>Søn</th>
            <th>Man</th><th>Tir</th><th>Ons</th>
            <th>Tor</th><th>Fre</th><th>Lør</th>
        </tfoot>
    </table>
    <p>
        Ønsker du å bare se kursene vi tilbyr for et spesielt sted, så kan du trykke på velg sted knappen under.
    </p>
</div>

<div data-role="footer" data-theme="b" data-tap-toggle="false" data-id="foo1" data-position="fixed">
    <div data-role="navbar">
        <ul>
            <li><a href="#">Forrige</a></li>
            <li><a href="#">Velg sted</a></li>
            <li><a href="#">Neste</a></li>
        </ul>
    </div>
</div>

<script>
$('.date_has_event').click(function() {
    $.mobile.changePage('#dayview_'+$(this).text(),  { transition: 'flow' } );
});
</script>