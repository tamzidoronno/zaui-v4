<?php
/* @var $api GetShopApi */
?>
<div class='backbutton'><i class="fa fa-arrow-left"></i> Back to main menu</div>
<input type="text" class="searchfield" placeholder="Search for a person"></input>
<br>
<br>
<a href="?page=newperson">
    <input type="button" value="Add new person">
</a>
<br>
<br>
<?
$users = $api->getArxManager()->getAllPersons();
foreach($users as $user) {
    /* @var $user core_arx_Person */
    echo "<div class='personrow'>";
    echo "<i class='fa fa-arrow-right'></i>";
    echo "<b>" . $user->firstName . " " . $user->lastName . "</b><br>";
    echo "Cards: ";
    foreach($user->cards as $card) {
        echo $card->cardid . " ";
    }
    echo "<div class='subdata'>";
    foreach($user->accessCategories as $category) {
        echo $category->name. ", ";
    }
    if($user->endDate) {
        echo "expires: " . date("d-m-y", strtotime($user->endDate));
    }
    
    echo "<div class='optionsforuser'>";
    echo "<hr>";
    
    foreach($user->accessCategories as $category) {
        /* @var $category core_arx_AccessCategory */
        echo "<div class='access_category'>";
        echo "<i class='fa fa-trash-o'></i> " . $category->name. ", <span style='float:right;'> <i class='fa fa-edit'></i></span><br>";
        echo "</div>";
        echo $category->startDate . "<br>";
        echo $category->endDate . "<br>";
        echo "<hr>";
    }
    
    $id = $user->id;
    echo "<a href='?page=newperson&id=$id' style='text-decoration:none; color:#fff;'>";
    echo "<div class='edit_person'><i class='fa fa-plus-circle'></i> Edit person</div>";
    echo "</a>";
    echo "<a href='?page=people&removePerson=$id' style='text-decoration:none; color:#fff;'><div class='access_category'><i class='fa fa-trash-o'></i> Remove person</div></a>";
    echo "<div class='access_category donemodifying'><i class='fa fa-thumbs-o-up'></i> Done modifying</div>";
    echo "</div>";
    echo "</div>";
    
    echo "</div>";
}



?>

<style>
    .access_category { font-size: 20px; margin-bottom: 10px; }
    .subdata { padding: 4px; color:#efefef; }
    .enddate { float:right; }
    .personrow { padding: 5px;  border: solid 1px #c1590d; display:block; position:relative;  }
    .personrow .fa-arrow-right { position:absolute; right: 10px; font-size: 30px; top: 10px; }
    .optionsforuser { display:none; }
    .personrow.highlighted .optionsforuser { display:block !important; }
    .personrow.highlighted {
        background-color:#000 !important;
    }
    .personrow.highlighted .fa-arrow-right {
        display:none;
    }
</style>

<script>
    $('.donemodifying').click(function() {
        $('.highlighted').removeClass('highlighted');
    });
    $('.personrow .fa-arrow-right').click(function() {
        var row = $(this).closest('.personrow');
        $('.highlighted').removeClass('highlighted');
        row.addClass('highlighted');
    });
</script>