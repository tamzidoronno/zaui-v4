<div class='header'>
<?php
/* @var $factory Factory */
$languages = $factory->getLanguageReadable();
$selected = $factory->getSelectedLanguage();
$codes = (array)$factory->getLanguageCodes();
if(sizeof($codes) > 0) {
    echo "<a class='languagebutton' href='?setLanguage=$selected'>" . $languages[$selected] . "</a> ";
    foreach($codes as $code) {
        echo "<a class='languagebutton' href='?setLanguage=$code'>" . $languages[$code] . "</a> ";
    }
}
?>
<div style="clear:both;"></div>
</div>
<script
  src="https://code.jquery.com/jquery-3.3.1.min.js"
  integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8="
  crossorigin="anonymous"></script>
<script>
    function getshop_redirectoToFront() {
        $.ajax({
            "url" : "paymentterminal.php",
            "method" : "GET",
            success : function() {
                window.location.href="paymentterminal.php";
            }
        });
    }
</script>

<style>
    .languagebutton { float:right; text-decoration: none; margin-right: 10px; padding: 10px; }
</style>