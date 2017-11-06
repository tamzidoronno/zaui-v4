<h1>Start booking</h1>
<form action='bookingprocess.php?method=startBooking&callback=function' method='POST'>
<textarea style='width:600px; height: 200px;' name='body'>{
  "start" : "<?php echo date("M d, Y h:i:s A", time()); ?>",
  "end" : "<?php echo date("M d, Y h:i:s A", time()+86400); ?>",
  "rooms" : 2,
  "adults" : 4,
  "children" : 2,
  "discountCode" : "",
  "bookingId" : ""
}</textarea><br>
    <input type='submit'>
</form>
