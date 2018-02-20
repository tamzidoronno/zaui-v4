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

<style>
    .languagebutton { float:right; text-decoration: none; margin-right: 10px; padding: 10px; }
</style>