<?php
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
header("Content-Type: text/xml;charset=UTF8");

$allProducts = $factory->getApi()->getProductManager()->getAllProducts();
$lists = $factory->getApi()->getListManager()->getLists();

$languages = [];
if(isset($factory->getSettings()->languages)) {
    $languages = json_decode($factory->getSettings()->languages->value);
}

echo "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
?>
<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9" xmlns:xhtml="http://www.w3.org/1999/xhtml">
<?
$address = "http://" . $_SERVER['SERVER_NAME'];

foreach ($allProducts as $product) {
    echo "<url>\n";
    echo "<loc>" . $address .  \GetShopHelper::makeSeoUrl($product->name)."</loc>\n";
    echo "</url>\n";
}

function getEntry($id, $entries) {
    foreach ($entries as $entry) {
        if ($entry->id == $id) {
            return $entry;
        }
        
        if ($entry->subentries) {
            $subEntryFound = getEntry($id, $entry->subentries);
            if ($subEntryFound != null) {
                return $subEntryFound;
            }
        }
    }
    
    return null;
    
}

function getLanguagesEntries($entry, $altLanguages) {
    $retEntries = [];
    foreach ($altLanguages as $lang => $entries) {
        $retEntries[$lang] = getEntry($entry->id, $entries);
    }
    return $retEntries;
}

function printEntries($entries, $address, $altLanguages) {
    foreach($entries as $entry) {
        if($entry->pageId != "home" && strlen($entry->pageId) < 20) {
            continue;
        }
        
        if ($entry->subentries && count($entry->subentries)) {
            printEntries($entry->subentries, $address, $altLanguages);
        }
        
        echo "<url>\n";
        
        echo "<loc>" . $address . GetShopHelper::makeSeoUrl($entry->name) . "</loc>\n";
        $langEntries = getLanguagesEntries($entry, $altLanguages);
        foreach ($langEntries as $lang => $langEntry) {
            $addressLang = $address . GetShopHelper::makeSeoUrl($langEntry->name)."_$lang";
            echo '<xhtml:link rel="alternate" hreflang="'.$lang.'" href="'.$addressLang.'"/>'."\n";
        }
        echo "</url>\n";
//        echo "<hr/>";
//        echo "<pre>";
//        print_r($entry);
//        print_r($langEntries);
//        echo "</pre>";
    }
}

foreach ($lists as $list) {
    $factory->getApi()->getStoreManager()->setSessionLanguage($factory->getMainLanguage());
    $entries = $factory->getApi()->getListManager()->getList($list);
    $altLanguages = [];
    
    foreach ($languages as $lang) {
        $factory->getApi()->getStoreManager()->setSessionLanguage($lang);
        $altLanguages[$lang] = $factory->getApi()->getListManager()->getList($list);
    }
    
    if(isset($entries)) {
        printEntries($entries, $address, $altLanguages);
    }
}
echo "</urlset>";
