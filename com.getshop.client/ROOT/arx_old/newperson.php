<?
$access_categories = 0;

if(isset($_POST['access_categories'])) {
    $access_categories = $_POST['access_categories'];
}
/* @var $api GetShopApi */
$categories = $api->getArxManager()->getAllAccessCategories();

$person = new core_arx_Person();

$id = "";
if(isset($_GET['id'])) {
    $id = $_GET['id'];
}

if(isset($_POST['add_card'])) {
    $card = new core_arx_Card();
    $card->cardid = $_POST['cardname'];
    $card->format = $_POST['type'];
    $api->getArxManager()->addCard($id, $card);
}

if(isset($_GET['removeCard'])) {
    $card = new core_arx_Card();
    $card->cardid = $_GET['removeCard'];
    $card->deleted = true;
    $api->getArxManager()->addCard($id, $card);
}

if(isset($_GET['id'])) {
    $person = $api->getArxManager()->getPerson($id);
}


if(isset($_GET['delete'])) {
    $person->deleted = true;
    $api->getArxManager()->updatePerson($person);
    echo "Deleted - <a href='?page=people'>back</a>";
    return;
}


if(isset($_POST['add_category'])) {
    $category = new core_arx_AccessCategory();
    $category->startDate = date('M d, Y H:i:s A', strtotime($_POST['startdate']));
    $category->endDate = date('M d, Y H:i:s A', strtotime($_POST['enddate']));
    $category->name = $_POST['name'];
    $person->accessCategories[] = $category;
    $api->getArxManager()->updatePerson($person);
}

if(isset($_GET['removeCategory'])) {
    foreach($person->accessCategories as $index => $cat) {
        if($cat->name == $_GET['removeCategory']) {
            unset($person->accessCategories[$index]);
        }
    }
    $api->getArxManager()->updatePerson($person);
}


if(isset($_POST['createnewperson'])) {
    $firstName = $_POST['firstName'];
    $lastName = $_POST['lastName'];
    $person = new core_arx_Person();
    $person->firstName = $firstName;
    $person->lastName = $lastName;
    
    $api->getArxManager()->updatePerson($person);
    header("location: /arx/?page=people");
}

?>
<div class='backbutton'><i class="fa fa-arrow-left"></i> Back to main menu</div>
<h1>Person details - <a href="?page=newperson&id=<? echo $id; ?>&delete=1"><i class='fa fa-trash-o'></i></a></h1>
<form action="" method="POST" id="personform">
    First name<br>
    <input type="text" name="firstName" value='<? echo $person->firstName; ?>'><br>
    Last name<br>
    <input type="text" name="lastName" value='<? echo $person->lastName; ?>'><br>
    <br>
    <input type="submit" value='Create new person' name='createnewperson'>
</form>

<hr>
<h1>Cards</h1>
Add a new card
<form action="" method="POST">
    Name<br>
    <input type="text" name="cardname"><br>
    Type<br>
    <select name="type">
        <option value="card">Card</option>
        <option value="kode">Code</option>
    </select><br>
    <input type="submit" value="Add card" name="add_card">
</form>
<?
foreach($person->cards as $card) {
    /* @var $card core_arx_Card */
    $trash = "<a href='?page=newperson&id=$id&removeCard=".$card->cardid."'><i class='fa fa-trash-o'></i></a>";
    echo "$trash Card number: " . $card->cardid . ", format: " . @$card->format . ", description: " . @$card->description . "<br>";
}
?>
<hr>
<h1>Access categories.</h1>
<?
echo "<form action='?page=newperson&id=$id' method='POST'>";
echo "<select name='name'>";
foreach($categories as $category) {
    echo "<option value='$category->name'>" . $category->name . "</option>";
}
echo "</select><br>";
echo "<input type='text' value='01-01-2015' name='startdate'><br>";
echo "<input type='text' value='01-01-2016' name='enddate'><br>";
echo "<input type='submit' value='Add category' name='add_category'><br>";
echo "</form>";


if(is_array($person->accessCategories)) {
    foreach($person->accessCategories as $category) {
        /* @var $category core_arx_AccessCategory */
        echo "<a href='?page=newperson&id=$id&removeCategory=".$category->name."'><i class='fa fa-trash-o'></i></a> " . $category->name . " - " . $category->startDate . " - " . $category->endDate . "<bR><br>";
    }
}
?>

<script>
    $('#access_categories').on('change', function() { 
        $('#personform').submit();  
    });
</script>