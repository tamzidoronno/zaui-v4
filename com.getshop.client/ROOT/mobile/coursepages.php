<?php
$entries = $factory->getApi()->getListManager()->getList("71544ed8-406d-4496-baf2-b569975ebb20");
    foreach ($entries as $entry) { 
        foreach($entry->subentries as $subentry) {
            echo '<div data-role="page" id="'.$subentry->pageId.'">';
                include 'header.html';
                echo '<div data-role="content" class="ContentManager">';
                    $page = $factory->getApi()->getPageManager()->getPage($subentry->pageId);
                    foreach($page->pageAreas->middle->applications as $app) {
                        if ($app->appName == "ContentManager") {
                            $contentManager = new \ns_320ada5b_a53a_46d2_99b2_9b0b26a7105a\ContentManager();
                            $contentManager->setConfiguration($app);
                            $contentManager->render();
                        }
                    }
                echo "</div>";
            echo "</div>";
        }
    }
?>
