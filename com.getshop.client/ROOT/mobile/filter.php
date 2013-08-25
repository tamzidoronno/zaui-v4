<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
$activeFilters = $factory->getApi()->getCalendarManager()->getFilters();
?>
<div data-role="content">
    Velg et av stedene under.
    <div data-role="controlgroup">
        <? foreach ($activeFilters as $filter) { 
            echo "<a href='#' data-role='button'>$filter</a>";
        } ?>    
    </div>
    
</div>
