<head>
<link href="//maxcdn.bootstrapcdn.com/font-awesome/4.2.0/css/font-awesome.min.css" rel="stylesheet">
<script type="text/javascript" src="scripts/jquery-1.9.0.js"></script>
<meta name="viewport" content="initial-scale=1, maximum-scale=1">

</head>
<?

chdir("../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton();

$api = $factory->getApi();
if(isset($_GET['logout'])) {
    $api->getUserManager()->logout();
}

echo "<body>";
if($factory->getApi()->getArxManager()->isLoggedOn()) {
    include("loggedon.php");
} else {
    include("loggedout.php");
}
echo "</body>";
?>

<style>
    body {
        background-color:#ff6e01;
        color:#fff;
    }
</style>
