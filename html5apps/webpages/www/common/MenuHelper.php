<?php

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of MenuHelper
 *
 * @author ktonder
 */
class MenuHelper {
    public static function sortBySequence($a, $b) {
        return $a->menuSequence > $b->menuSequence;
    }
    
    public function buildMenu($pages) {        
        usort($pages, array("MenuHelper", "sortBySequence"));
        foreach ($pages as $page) {
            $className = get_class($page);
            
            if (!$page->subpageof && !$page->hiddenFromMenu) {
                $link = $page->canNavigateTo ? "/?page=$className" : "#";
                echo "<a href='$link'>";
                    echo "<div class='entry entry_$className'>".$page->getMenuName();
                        $this->printSubPages($pages, $page);
                    echo "</div>";
                echo "</a>";
            }
        }
    }

    public function printSubPages($pages, $page) {
        $subEntries = array();
        
        foreach ($pages as $ipage) {
            
            if ($ipage->subpageof == get_class($page)) {
                $subEntries[] = $ipage;
            }
        }
        
        if (count($subEntries)) {
            echo "<div class='subentries'>";
                foreach ($subEntries as $ipage) {
                    $className = get_class($ipage);
                    echo "<a href='?page=$className'>";
                        echo "<div class='entry subentry'><div class='title'>".$ipage->getMenuName()."</div>";
                            $largeMenuDesc = $ipage->getLargeMenuText();
                            if ($largeMenuDesc) {
                                echo "<div class='largedesc'>".$largeMenuDesc."</div>";
                            }
                            $this->printSubPages($pages, $ipage);
                        echo "</div>";
                        
                        
                    echo "</a>";
                }
            echo "</div>";
        }
    }

}
