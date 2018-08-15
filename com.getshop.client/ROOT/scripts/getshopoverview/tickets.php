<h1>Register tickets on customers</h1>
<?php
$factory = IocContainer::getFactorySingelton();
$bookings = $factory->getApi()->getPmsManager()->getAllBookings("default", null);
$users = $factory->getApi()->getUserManager()->getAllUsersSimple();
foreach($users as $usr) {
    $users[$usr->id] = $usr;
}
?>
<select class='choosecustomer'>
    <option value="">Choose customer</option>
    <?php
    foreach($bookings as $booking) {
        foreach($booking->rooms as $room) {
            $typeid = $room->pmsBookingRoomId;
            if($typeid == "cbdc86c4-de85-4f2d-97b7-24d7da4b5d02" ||
                    $typeid == "be48e766-44db-426b-9868-c4b01daa6e2d" ||
                    $typeid == "bdfdc3b1-68c7-41aa-8d6b-bedc88134a73" ||
                    $typeid == "e4e03262-2ca4-4e52-82bc-e7f79118229a" ||
                    $typeid == "0e639cd7-bc2e-4d07-b8a9-743c188cf35f") {
                continue;
            }
            $user = $users[$booking->userId];
            foreach($room->guests as $guest) {
                if(!$guest->name) {
                    continue;
                }
                $name = "";
                if($user->fullname) {
                    $name = " (" . $user->fullname .")";
                }
                echo "<option value='".$room->pmsBookingRoomId."'>" . $guest->name  . $name . "</option>";
            }
        }
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