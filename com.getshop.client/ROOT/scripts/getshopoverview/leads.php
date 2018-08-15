<?php
$factory = IocContainer::getFactorySingelton();

if(isset($_POST['createlead']) && $_POST['name']) {
    $factory->getApi()->getGetShop()->createLead($_POST['name']);
}
$leads = $factory->getApi()->getGetShop()->getLeads();
$leadstates = array();
$leadstates[0] = "NEW";
$leadstates[4] = "COLD";
$leadstates[1] = "HOT";
$leadstates[2] = "LOST";
$leadstates[3] = "WON";
?>
<h1>Current leads</h1>
<form action='' method='POST'>
    <input type="text" placeholder="Name of lead" name='name'><input type="submit" value='Create lead' name='createlead'>
</form>

<table width='100%'>
    <tr>
        <th align='left'>Registration date</th>
        <th align='left'>Customer</th>
        <th align='left'>Rooms</th>
        <th align='left'>Beds</th>
        <th align='left'>Phone</th>
        <th align='left'>Email</th>
        <th align='left'>Price offer</th>
        <th align='left'>State</th>
        <th align='right'>History</th>
    </tr>
        
<?php
foreach($leads as $lead) {
    echo "<tr leadid='".$lead->id."' class='row'>";
    echo "<td align='left'>".date("d.m.Y H:i", strtotime($lead->rowCreatedDate)) . "</td>";
    echo "<td style='width:200px;'><input name='customerName' type='txt' value='".$lead->customerName."' style='width:200px;'></td>";
    echo "<td><input name='rooms' type='txt' value='".$lead->rooms."' style='width:60px;'></td>";
    echo "<td><input name='beds' type='txt' value='".$lead->beds."' style='width:60px;'></td>";
    echo "<td><input name='phone' type='txt' value='".$lead->phone."' style='width:100px;'></td>";
    echo "<td><input name='email' type='txt' value='".$lead->email."' style='width:160px;'></td>";
    echo "<td><input name='offerPrice' type='txt' value='".$lead->offerPrice."' style='width:60px;'></td>";
    echo "<td style='width:440px;'>";
    foreach($leadstates as $k => $name) {
        $state = ($k == $lead->leadState) ? "current" : "";
        echo "<span class='leadstatebox $state' stateid='$k'>" . $name . "</span>";
    }
    echo "</td>";
    echo "<td style='width:440px;'>";
    echo "<span class='addcommentbutton' style='position:absolute;right:0px; color:blue; display:none;'>Add comment</span>";
    $historyList = (array)$lead->leadHistory;
    krsort($historyList);
    $i = 0;
    foreach($historyList as $history) {
        $classToAdd = $i == 0 ? "historyshow" : "historyhidden";
        echo "<div class='$classToAdd'>";
        echo date("d.m.Y H:i", strtotime($history->historyDate)) . " : " . $history->comment;
        echo "(" . $leadstates[$history->leadState] . ")";
        echo "</div>";
        $i++;
    }
    if(sizeof($historyList) == 0) {
        echo "<div class='historyshow'></div>";
    }
    echo "</td>";
    echo "</tr>";
}
echo "</table>";

?>
<style>
    .leadstatebox { border: solid 1px #bbb; text-align: center; width: 80px; margin-right: 5px; display: inline-block; cursor:pointer; }
    .leadstatebox.current { background-color:green; color:#fff; }
    .historyhidden { display: none; }
    .historyshow { cursor:pointer; }
    .row:hover .addcommentbutton { display:block !important; cursor:pointer; }
</style>

<script>
    $('.row input').keyup(function() {
        var data = {};
        var row = $(this).closest('.row');
        row.find('input').each(function() {
            data[$(this).attr('name')] = $(this).val();
        });
        data['leadid'] = row.attr('leadid');
           $.ajax('updatelead.php', {
                method : "POST",
                data: data,
                success : function(res) {}

            });
    });
    
    $('.leadstatebox').click(function() {
        var row = $(this).closest('tr');
        var id = row.attr('leadid');
        var btn = $(this);
        var state = $(this).attr('stateid');
        $.get("changeleadstate.php?id="+id+"&state="+state, function() {
            row.find('.current').removeClass('current');
            btn.addClass('current');
        });
    });
    
    $('.historyshow').click(function() {
        var row = $(this).closest('tr');
        row.find('.historyhidden').show();
    });
    
    $('.addcommentbutton').click(function() {
        var comment = prompt("Commment","");
        var row = $(this).closest('.row');
        if(comment) {
            var data = {
                "leadid" : $(this).closest('.row').attr('leadid'),
                "comment" : comment
            }
            $.ajax('addcommenttolead.php', {
                method : "POST",
                data: data,
                success : function(res) {
                    row.find('.historyshow').html(res);
                }

            });
        }
    });
</script>