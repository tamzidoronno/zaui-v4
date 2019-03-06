<h1>Register tickets on customers</h1>
<?php
$factory = IocContainer::getFactorySingelton();
$bookings = $factory->getApi()->getPmsManager()->getAllBookings("default", null);
$users = $factory->getApi()->getUserManager()->getAllUsersSimple();
foreach($users as $usr) {
    $users[$usr->id] = $usr;
}
$companies = $factory->getApi()->getUserManager()->getAllCompanies();

?>
<select class='choosecustomer' style="width:500px;">
    <option value="">Choose customer</option>
    <?php
    foreach($companies as $company) {
        $compuser = $factory->getApi()->getUserManager()->getMainCompanyUser($company->id);
        $system = (array)$factory->getApi()->getSystemManager()->getSystemsForCompany($company->id);
        if(sizeof($system) == 0) {
            continue;
        }
        $addr = array();
        foreach($system as $syst) {
            $addr[] = $syst->webAddresses;
        }
        echo "<option value='".$compuser->id."'> " . $company->name . " (". join(",", $addr) . ")</option>";
    }
    ?>
</select>
<input type="number" placeholder="Hour" style="width:60px;" class='hour'>
<input type="number" placeholder="Min" style="width:60px;" class='minute'>
<input type="text" placeholder="Description" style="width:500px;" class='description'>
<input type="button" value="Add ticket" class='addticket'>

<div id='ticketlist'>
    <?php
    include("ticketlist.php");
    ?>
</div>

<script>
    $('.choosecustomer').chosen({
        search_contains: true
    });
    
    $('.addticket').click(function() {
        var data = {};
        data['roomid'] = $('.choosecustomer').val();
        data['hour'] = $('.hour').val();
        data['minute'] = $('.minute').val();
        data['description'] = $('.description').val();
        
        if(!data['roomid'] || !data['hour'] || !data['minute'] || !data['description']) {
            alert('Input euror');
            return;
        }
        
        $.ajax('addticket.php', {
            method : "POST",
            data: data,
            success : function(res) {
                $('.hour').val("");
                $('.minute').val("");
                $('.description').val("");
                $('#ticketlist').html
                (res);
            }

        });
    });
</script>