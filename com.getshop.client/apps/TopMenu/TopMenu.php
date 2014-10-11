<?php
namespace ns_1051b4cf_6e9f_475d_aa12_fc83a89d2fd4;

/**
 * @author boggi
 */
class TopMenu extends \SystemApplication implements \Application {
    var $entries;
    var $currentMenuEntry;
    
    public function getDescription() {
        return $this->__f("A topmenu enables you to display top menu entries");
    }

    public function getName() {
        return $this->__f("Top menu");
    }

    public function postProcess() {
        
    }

    public function preProcess() {
        
    }
    
    public function setAsHomePage() {
        $storeConfig = $this->getFactory()->getStoreConfiguration();
        $storeConfig->homePage = $_POST['data']['id'];
        $this->getApi()->getStoreManager()->saveStore($storeConfig);
    }
    
    public function addEntry() {
        $entry = $this->getApiObject()->core_listmanager_data_Entry();
        $entry->name = $_POST['data']['newentry'];
        $entry->pageType = -1;
        $entry = $this->getApi()->getListManager()->addEntry($this->getConfiguration()->id, $entry, "");
        $_GET['page'] = $entry->pageId;
        $this->getFactory()->clearCachedPageData();
        $this->getFactory()->initPage();
    }
    
    public function renameEntry() {
        if(isset($_POST['data']['renameId'])) {
            if($_POST['data']['newentry']) {
                $entry = $this->getApi()->getListManager()->getListEntry($_POST['data']['renameId']);
                $entry->name = $_POST['data']['newentry'];
                $this->getApi()->getListManager()->updateEntry($entry);
            }
        }
    }

    public function render() {
        $this->entries = $this->getApi()->getListManager()->getList($this->getConfiguration()->id);
        $this->includefile("TopMenuTemplateEntries");
    }
    
    public function deleteEntry() {
        $id = $_POST['data']['id'];
        $this->getApi()->getListManager()->deleteEntry($id, $this->getConfiguration()->id);
    }
    
    public function getCurrentMenuEntry() {
        return $this->currentMenuEntry;
    }
    
    public function setCurrentMenuEntry($entry) {
        $this->currentMenuEntry = $entry;
    }
    
    public function getEntries() {
        return $this->entries;
    }
    
    public function MoveEntry() {
        $id = $_POST['data']['id'];
        $after = @$_POST['data']['after'];
        $this->getApi()->getListManager()->orderEntry($id, $after, "");
    }
	
	public function printEntries($entries, $pageId, $menu, $container, $isTop, $currentUser) {
        if(isset($entries)) {
            $counter = 0;
            foreach ($entries as $entry) {
                
                if ($entry->userLevel != null && $entry->userLevel > 0 && ($currentUser == null || $entry->userLevel > $currentUser->type)) {
                    continue;
                }
                
                $counter++;
                $first = !isset($first) ? "first" : "";
                $last = (count($entries) == $counter) ? "last" : "";

                if (!$entry->pageId) {
                    $entry->pageId = "home";
                }

                $selected = "";
                $selected_entry = "";
                if ($pageId == $entry->pageId) {
                    $selected = "selected";
                    $selected_entry  = "selected_entry";
                }

                $menu->setCurrentMenuEntry($entry);
                /* @var $entry core_listmanager_data_Entry */
                ?><li class="configable entry <? echo $first; echo $last; echo " ".$selected_entry; ?>" interrupt="false" menuid="<? echo  $entry->id; ?>">
                    <?php
                    if (\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::isAdministrator() && $isTop) {
                        $container->includeFile("LeftMenuTemplateMenuConfiguration", "LeftMenu");
                        echo "<span class='edit'></span>";
                    }
                    if (@$_POST['event'] == "renameEntry" && @$_POST['data']['id'] == $entry->id) {
                        ?>
                        <span gstype="form" method="renameEntry">
                            <input gstype="submitenter" method="renameEntry" class='renametopmenuinput'  gsname="newentry" type="text" placeholder="<?php echo $entry->name; ?>">
                            <input type="hidden" gsname="renameId" value="<?php echo $entry->id; ?>">
                        </span>

                        <?php
                    } else {
                        ?>
                        <span>
                            <?php
                            /* @var $entry \core_listmanager_data_Entry */
                            $hardlink = "false";
                            $target = "";
                            if($entry->hardLink) {
                                $hardlink="true";
                                $url = $entry->hardLink;
                                $target = "_new";
                            } else {
                                $link = "?page=".$entry->pageId;
                                $url = \GetShopHelper::makeSeoUrl($entry->name);
                            }
                            $icon = "";
                            if($entry->fontAwsomeIcon) {
                                $icon = "<i class='fa " . $entry->fontAwsomeIcon."'></i>";
                            }
                            if($entry->subentries) {
                                ?>
                                <a ajaxlink="<?php echo $link; ?>" href="<? echo $url; ?>" target="<? echo $target; ?>" class="<?php echo $selected; ?>" hardlink="<?php echo $hardlink; ?>"><?php echo $icon.$entry->name; ?></a>
                                <?
                                echo '<ul class="subentries" id="topmenuline">';
                                    printEntries($entry->subentries, $pageId, $menu, $container, false, $currentUser);
                                echo '</ul>';
                            } else {
                            ?>
                            <a ajaxlink="<?php echo $link; ?>" href="<? echo $url; ?>" target="<? echo $target; ?>" class="<?php echo $selected; ?>" hardlink="<?php echo $hardlink; ?>"><?php echo $icon.$entry->name; ?></a>
                            <? } ?>
                        </span>
                        <?php
                    }
                    ?>
                <span class='droparea'></span>
                </li>
                <?
            }
        }
    }
}

?>
