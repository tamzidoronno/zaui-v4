<head>
<link href="//maxcdn.bootstrapcdn.com/font-awesome/4.2.0/css/font-awesome.min.css" rel="stylesheet">
<script type="text/javascript" src="scripts/jquery-1.9.0.js"></script>
<meta name="viewport" content="initial-scale=1, maximum-scale=1">
<script type="text/javascript" src="/js/jquery-1.9.0.js"></script>
<script type="text/javascript" src="/js/jquery.ui/js/jquery-ui-1.9.2.custom.min.js"></script>
<link rel="stylesheet" type="text/css" href="/js/jquery.ui/css/smoothness/jquery-ui-1.9.2.custom.min.css">
<link rel="stylesheet" type="text/css" href="/js/datatables/demo_table.css">
<link rel="stylesheet" type="text/css" href="/js/datatables/demo_page.css">
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
