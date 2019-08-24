<div style='text-align: center; font-size: 20px; margin: 20pX;'>
    Waiting <span id='counter'></span> seconds to see if the device is coming back online.
</div>
<style>
.loader {
  border: 16px solid #f3f3f3; /* Light grey */
  border-top: 16px solid #3498db; /* Blue */
  border-radius: 50%;
  width: 120px;
  height: 120px;
  animation: spin 2s linear infinite;
  text-align: center;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
<center>
    <div class="loader"></div>
</center>

<script>
    var counter = 180;
    setInterval(function() {
        $('#counter').html(counter);
        counter--;
        if(counter < 0) {
            window.location.href='?id=<?php echo $_GET['id']; ?>&step=4&unit=<?php echo $_GET['unit']; ?>';
        }
    }, "1000");
</script>