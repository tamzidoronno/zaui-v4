<?php
/* @var $api GetShopApi */
?>
<div class='backbutton'><i class="fa fa-arrow-left"></i> Back to main menu</div>
<?
$doors = $api->getArxManager()->getAllDoors();
foreach($doors as $door) {
    /* @var $door core_arx_Door */
    echo "<div class='doorentry'>";
    echo "<i class='fa fa-arrow-right' style='float:right;font-size:20px;'></i>";
    echo $door->name;
    echo "<div class='options'>";
    echo "<div class='optionentry pulsopen' externalId='".$door->externalId."' state='open'><i class='fa fa-unlock'></i> Open</div>";
    echo "<div class='optionentry pulsopen' externalId='".$door->externalId."' state='forceOpen'><i class='fa fa-key'></i> Force open</div>";
    echo "<div class='optionentry pulsopen' externalId='".$door->externalId."' state='forceClose'><i class='fa fa-lock'></i> Force close</div>";
    echo "<a href='?page=dooraccesslog&id=".$door->externalId."'><div class='optionentry'><i class='fa fa-list'></i> Access log</div></a>";
    echo "</div>";
    echo "</div>";
}
?>
<style>
    .doorentry { border: solid 1px; padding: 10px; }
    .doorentry .options { display:none; }
    .doorentry.highlighet { background-color:#000; }
    .doorentry.highlighet .fa-arrow-right { display:none; }
    .doorentry.highlighet .options { display:block; }
    .doorentry .optionentry { padding: 10px; font-size: 20px; }
    .doorentry .optionentry i { width: 30px; }
    .doorentry a { color:#fff; text-decoration: none; }
</style>

<script>
    $('.doorentry .fa-arrow-right').click(function() {
        $('.highlighet').removeClass('highlighet');
        $(this).closest('.doorentry').addClass('highlighet');
    });
    
    $('.pulsopen').click(function() {
        var state = $(this).attr('state');
        var row = $(this);
        var id = $(this).attr('externalId');
        
        var data = {
            "id" : id,
            "state" : state
        };
        
        row.fadeOut(function() {
            $.ajax({
                url: 'pulsopen.php',
                data: data,
                success: function() {
                    row.fadeIn();
                },
                dataType: "html"
            });
        });
    });
</script>