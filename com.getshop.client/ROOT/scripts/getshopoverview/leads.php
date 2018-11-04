<?php
$factory = IocContainer::getFactorySingelton();

if(isset($_POST['createlead']) && $_POST['name']) {
    $factory->getApi()->getGetShop()->createLead($_POST['name']);
}
if(isset($_POST['registerfollowup'])) {
    if(isset($_SESSION['lastfollowup']) && $_SESSION['lastfollowup'] == $_POST['registerfollowup']) {
        echo "double submit, avoid this.";
    } else {
        $_SESSION['lastfollowup'] = $_POST['registerfollowup'];
        $start = date("c", strtotime($_POST['date'] . " " . $_POST['start']));
        $end = date("c", strtotime($_POST['date'] . " " . $_POST['end']));
        $comment = $_POST['comment'];
        $leadid = $_POST['leadid'];
        $userid = $_POST['userid'];
        $factory->getApi()->getGetShop()->addLeadHistory($leadid, $comment, $start, $end, $userid);
    }
}
$leads = $factory->getApi()->getGetShop()->getLeads();
$leadstates = array();
$leadstates[0] = "NEW";
$leadstates[4] = "COLD";
$leadstates[1] = "HOT";
$leadstates[2] = "LOST";
$leadstates[3] = "WON";
$leadstates[5] = "DELIVER";

$users = $factory->getApi()->getUserManager()->getAllUsers();
$admins = array();
$loggedonuser = $factory->getApi()->getUserManager()->getLoggedOnUser();
foreach($users as $usr) {
    if($usr->type == 100) {
        $admins[$usr->id] = $usr;
    }
}
?>
<div style='border:solid 1px #ddd; width: 400px;padding: 10px; background-color:#efefef;position:absolute;display:none;' class='addcommentpanel'>
    <form action='' method='POST'>
        <input type='date' name='date'>
        <input type='txt' name='start' value='10:00' style='width:60px'>
        <input type='txt' name='end' value='12:00' style='width:60px'>
        <input type='hidden' name='leadid'>
        <input type='hidden' value='<?php echo rand(0,1000000); ?> ' name='registerfollowup'>
        <select style='padding: 3px;' name='userid'>
            <?php
            foreach($admins as $admin) {
                $selected = "";
                if($admin->id == $loggedonuser->id) {
                    $selected = "SELECTED";
                }
                echo "<option value='" . $admin->id . "' $selected>" . $admin->fullName . " (" . $admin->emailAddress . ")</option>";
            }
            ?>
        </select>
        <div>
            Comment:<br>
            <textarea name='comment' style='width:400px; height: 100px;'></textarea>
        </div>
        <div style='text-align:right; margin-top: 5px;'>
            <input type='submit' value='Register followup'>
        </div>
    </form>
</div>
<?php
$toComplete = array();
?>
<h1>My followups today</h1>
<table  cellspacing='1' cellpadding='1' width="100%" bgcolor='#ddd'>
    <?php
    echo "<tr bgcolor='#fff'>";
    for($i = 0; $i <= 7; $i++) {
        echo "<td valign='top' width='14.28%' style='padding:5px;'>";
        $day = (time() + (86400*$i));
        echo "<b>" . date("d.m.Y (D)", $day) . "</b>";
        foreach($leads as $lead) {
            echo "<table cellspacing='1' cellpadding='1' width='100%'>";
            foreach($lead->leadHistory as $lhist) {
                if(stristr($lhist->comment, "changed lead state")) {
                    continue;
                }
                if($lhist->userId == $loggedonuser->id) {
                    if(!$lhist->completed && strtotime($lhist->historyDate) < time() || date("dmy") == date("dmy", strtotime($lhist->historyDate))) {
                        $lhist->customerName = $lead->customerName;
                        $toComplete[$lhist->leadHistoryId] = $lhist;
                    }
                }
                if($lhist->userId == $loggedonuser->id) {
                    if(date("dmy",$day) == date("dmy", strtotime($lhist->historyDate))) {
                        echo "<tr bgcolor='ffffff'>";
                        echo "<td class='overflow' title='".$lhist->comment."' style='cursor:pointer;' class='markcompleted' historyid='".$lhist->leadHistoryId."'> " . date("H:i", strtotime($lhist->historyDate)) . " - " .  date("H:i", strtotime($lhist->endDate)) . " : "  . $lead->customerName .  "</td>";
                        echo "</tr>";
                    }
                }
            }
            echo "</table>";
        }
        echo "</td>";
    }
    echo "</tr>";
    ?>
</table>
<h1>Need completion</h1>
<?php
$toComplete = (array)$toComplete;
echo "<table>";
foreach($toComplete as $leadHistoryId => $complete) {
    echo "<tr>";
    echo "<td><input type='button' class='markcompleted' style='cursor:pointer;' leadHistoryId='$leadHistoryId' value='Completed'></td>";
    echo "<td>" . date("d.m.y H:i", strtotime($complete->historyDate)) . "</td><td>" . $complete->customerName . "</td><td>" . $complete->comment . "</td>";
    echo "</tr>";
}
echo "</table>";
?>

<h1>Current leads</h1>
<form action='' method='POST'>
    <input type="text" placeholder="Name of lead" name='name'><input type="submit" value='Create lead' name='createlead'>
</form>

<table width='100%' class='customerlist'>
    <tr>
        <th align='left'>Registration date</th>
        <th align='left'>Customer</th>
        <th align='left'>Rooms</th>
        <th align='left'>Beds</th>
        <th align='left'>Phone</th>
        <th align='left'>Email</th>
        <th align='left'>Price offer</th>
        <th align='left'>State</th>
        <th align='left'>Followup date</th>
        <th align='left'>History</th>
        <th align='left'>Add followup</th>
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
    echo "<td >";
    foreach($leadstates as $k => $name) {
        $state = ($k == $lead->leadState) ? "current" : "";
        echo "<span class='leadstatebox $state' stateid='$k'>" . $name . "</span>";
    }
    $followup = null;
    $today = false;
    foreach($lead->leadHistory as $hist) {
        if(date("dmy") == date("dmy", strtotime($hist->historyDate)) && !stristr($hist->comment, "changed lead state")) {
            $today = true;
            $usrid = $hist->userId;
            $title = $hist->comment;
        }
        if(time() < strtotime($hist->historyDate)) {
            $followup = $hist->historyDate;
            $usrid = $hist->userId;
            $title = $hist->comment;
        }
    }
    echo "</td>";
    echo "<td>";
    if($followup) {
        echo "<div title='$title'>";
         echo date("d.m.Y", strtotime($followup));
         echo " by " . $admins[$usrid]->fullName;
         echo "</div>";
    } else {
        if($today) {
            echo "<span style='color:green;font-weight:bold;'>TODAY</span>";
            echo " by " . $admins[$usrid]->fullName;
        } else {
            echo "<span style='color:red;font-weight:bold;'>NOT BEING FOLLOWED UP!</span>";
        }
    }
    echo "</td>";
    echo "<td>";
    echo "<input type='button' value='View history' class='viewhostory' style='cursor:pointer;'>";
    echo "<div style='position:absolute;right:0px; border: solid 1px; padding: 10px; background-color:#efefef;display:none;' class='historypanel'>";
    foreach($lead->leadHistory as $hist) {
        echo date("d.m.Y", strtotime($hist->historyDate)) . "<br>";
        echo "<div>" . $hist->comment . "</div>";
        echo "<div style='color:#bbb'>- " . $admins[$hist->userId]->fullName . "</div>";
        echo "<hr>";
    }
    echo "</div>";
    echo "</td>";
    echo "<td>";
    echo "<input type='button' value='Add followup' class='addfollowupbtn' style='cursor:pointer;'>";
    echo "</td>";
    echo "</tr>";
}
echo "</table>";

?>
<style>
    .overflow {
        white-space: nowrap; 
        overflow: hidden;
        display:inline-block;
        width:100%;
        text-overflow: ellipsis;
    }
    .leadstatebox { border: solid 1px #bbb; text-align: center; width: 80px; margin-right: 5px; display: inline-block; cursor:pointer; }
    .leadstatebox.current { background-color:green; color:#fff; }
    .historyhidden { display: none; }
    .historyshow { cursor:pointer; }
    .row:hover .addcommentbutton { display:block !important; cursor:pointer; }
    .customerlist tr:hover { font-weight: bold; }
</style>

<script>
    
    $('.viewhostory').on('click', function() {
        var row = $(this).closest('.row');
        row.find('.historypanel').toggle();
        
    });
    $('.markcompleted').on('click', function() {
        var historyid = $(this).attr('leadHistoryId');
        $(this).closest('tr').hide();
        $.get("index.php?markCompleted=" + historyid);
    });
    
    $('.addfollowupbtn').on('click', function() {
        var panel = $('.addcommentpanel');
        var row = $(this).closest('.row');
        panel.css('left',$(this).offset().left-450);
        panel.css('top',$(this).offset().top);
        panel.find("input[name='leadid']").val(row.attr('leadid'));
        panel.show();
    });
    
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