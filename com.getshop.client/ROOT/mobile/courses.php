<div data-role="content">
    <p> Her finner du en liste over vårt kurstilbud. Klikk på en av kategoriene for å se undermenyen, klikk så på en av undermenyene for å få utvidet informasjon.</p>
    <?php
    $entries = $factory->getApi()->getListManager()->getList("71544ed8-406d-4496-baf2-b569975ebb20");
    foreach ($entries as $entry) { ?>        
        <div data-role="collapsible" data-theme="b" data-content-theme="d"  data-inset="false">
            <h4><? echo $entry->name; ?></h4>
            
            <ul data-role="listview">
                <? foreach($entry->subentries as $subentry) {?>
                    <li><a href="#<? echo $subentry->pageId; ?>"><? echo $subentry->name; ?></a></li>
                <? } ?>
            </ul>
        </div>
    <? } ?>
</div>