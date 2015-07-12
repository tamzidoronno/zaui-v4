<div style='text-align: center; font-size: 30px;text-transform: uppercase'>
Arx Services
</div>
<hr>
<bR>
<?
$page = "loggedonmenu.php";
if(isset($_GET['page'])) {
    $page = $_GET['page'];
}

switch($page) {
    case "people":
        include("people.php");
        break;
    case "doors":
        include("doors.php");
        break;
    case "dooraccesslog":
        include("dooraccesslog.php");
        break;
    case "newperson":
        include("newperson.php");
        break;
    case "info":
        include("info.php");
        break;
    default:
        include("loggedonmenu.php");
}

?>

<style>
    .backbutton { height: 20px;  text-align: center; padding:10px; border-radius: 3px; background-color:#bf580d; margin-bottom: 20px;}
    .backbutton i { float:left;}
    .searchfield { width: 100%; border: solid 1px #fff; border-radius: 3px;text-align: center;font-size: 20px;padding: 5px; }
</style>

<script>
    $('.backbutton').click(function() {
        window.location.href="/arx";
    });
</script>