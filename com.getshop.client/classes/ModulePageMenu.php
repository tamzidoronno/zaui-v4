<?php
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of ModulePageMenu
 *
 * @author ktonder
 */
if(file_exists('/thundashopimages/v5customers.php'))
{
    include '/thundashopimages/v5customers.php';
}
else
{
    function isV5Customer($storeId)
    {
        return false;
    }
}


class ModulePageMenu {

    public $entries = array();
    private $moduleName;

    function __construct($moduleName="pms") {
        $this->moduleName = $moduleName;
    }

    /**
     * 
     * @return ModulePageMenuItem[]
     */
    function getEntries() {
        return $this->entries;
    }

    /**
     * 
     * @param core_usermanager_data_User $user
     */
    public function renderTop($user, $printPageMenuInModulesMenu=false, $store = null) {
        ?>

        <div class="Menu">
            <?php
            $this->printChangedMenues($user, $printPageMenuInModulesMenu, $store);
            if (!$printPageMenuInModulesMenu) {
                $this->printHorizantalMenu($user, $store);
            }
            ?>
        </div>
        <?
    }

    public function renderNumPad() {
        ?>
        <div class='gsnumpad'>
            <div class='innernumpad'>
                <div class='numpadtitle'> TITLE </div>
                <div class='oldvalue'> Old value: <span></span></div>
                <div class='numpadvalue'></div>
                <div class='gs_numpad_element' value='CANCEL'>X</div>
                <div class='gs_numpad_element' value='3'>3</div>
                <div class='gs_numpad_element' value='2'>2</div>
                <div class='gs_numpad_element' value='1'>1</div>
                <div class='gs_numpad_element' value='-'>+/-</div>
                <div class='gs_numpad_element' value='6'>6</div>
                <div class='gs_numpad_element' value='5'>5</div>
                <div class='gs_numpad_element' value='4'>4</div>
                <div class='gs_numpad_element' value='x'><-</div>
                <div class='gs_numpad_element' value='9'>9</div>
                <div class='gs_numpad_element' value='8'>8</div>
                <div class='gs_numpad_element' value='7'>7</div>
                <div class='gs_numpad_element h2' value='OK'>OK</div>
                <div class='gs_numpad_element' value='.'>,</div>
                <div class='gs_numpad_element' value='0'>0</div>
                <input type='text' readonly='true' class='numpadinput' style='display: none;'/>
            </div>
        </div>
        <?
    }

    public function renderLeft($moduleName="pms", $printPageMenuInModulesMenu=false) {
        $menuEntries = $this->getEntries();
        $renderMenuInModules = $printPageMenuInModulesMenu ? "yes" : "no";
        ?>

        <div class="Menu" menurenderedinmodules="<? echo $renderMenuInModules; ?>">
            <div class="menuentries vertical">
                <div class="entries">
                    <? foreach ($menuEntries as $entry) { ?>
                        <div class="entry"><a href="/<?echo $moduleName;?>.php?page=<? echo $entry->getPageId(); ?>&gs_getshopmodule=<? echo \PageFactory::getGetShopModule(); ?>"><div><i class="fa <? echo $entry->getIcon(); ?>"></i>  <? echo $entry->getName(); ?> </div></a></div>
                    <? } ?>
                </div>
            </div>
        </div>
        <?
    }

    public static function getComfortLeftMenu() {
        $menu = new \ModulePageMenu("comfort");
        $menu->entries[] = new ModulePageMenuItem("States", "statesconfig", "");
        $menu->entries[] = new ModulePageMenuItem("Comfort units", "comfortunits", "");
        $menu->entries[] = new ModulePageMenuItem("Room configuration", "roomconfiguration", "");
        return $menu;
    }
    
    public static function getPmsLeftMenu() {
        $menu = new \ModulePageMenu();
        $menu->entries[] = new ModulePageMenuItem("Messages", "messages", "");
        $menu->entries[] = new ModulePageMenuItem("Payment methods", "paymentmethods", "");
        $menu->entries[] = new ModulePageMenuItem("Products / addons", "productsandaddonsconfig", "");
        $menu->entries[] = new ModulePageMenuItem("Terms and conditions", "termsconfig", "");
        $menu->entries[] = new ModulePageMenuItem("Instruction set", "instructionconfig", "");
        $menu->entries[] = new ModulePageMenuItem("Rooms", "roomsconfig", "");
        $menu->entries[] = new ModulePageMenuItem("Notifications", "notifications", "");
        $menu->entries[] = new ModulePageMenuItem("GetShop Express", "getshopexpressconfig", "");
        $menu->entries[] = new ModulePageMenuItem("Restrictions", "restrictionsconfig", "");
        $menu->entries[] = new ModulePageMenuItem("Channel manager", "channelmanagerconfig", "");
        $menu->entries[] = new ModulePageMenuItem("Lock setup", "lockconfig", "");
        $menu->entries[] = new ModulePageMenuItem("Global setttings", "globalsettings", "");
        $menu->entries[] = new ModulePageMenuItem("Segmentation", "segmentation", "");
        return $menu;
    }

    public function printChangedMenues($user, $printPageMenuInModulesMenu, $store) {
        $factory = IocContainer::getFactorySingelton();
        $modules = $factory->getUserAccessModules();
        echo "<div class='gs_framework_modules'>";
        
            if ($printPageMenuInModulesMenu) {
                echo "<div class='inner_header_top_menu_gs'>Menu</div>";
                $this->printHorizantalMenu($user, $store);
            }

            $gsadminmenues = array();
            $gsadminmenues[] = "salespoint";
            $gsadminmenues[] = "settings";
            $gsadminmenues[] = "account";
            $gsadminmenues[] = "invoice";   
            
            echo "<div class='inner_header_top_menu_gs'>Modules</div>";
            
            foreach ($modules as $module) {
                if($factory->getStore()->id == "13442b34-31e5-424c-bb23-a396b7aeb8ca" && !in_array($module->id, $gsadminmenues)) {
                    continue;
                }
                $moduleActiveClass = $factory->getPage()->javapage->getshopModule == $module->id ? "active" : "";
                $activeModule = $factory->getPage()->javapage->getshopModule == $module->id ? $module : $activeModule;
                if (!$activeModule && $module->id == "cms") {
                    $activeModule = $module;
                }
                $icon = "<i class='fa gs".$module->fontAwesome."'></i>";
                $scopeId = $_POST['scopeid'];
                if($module->name == "PMS") {
                    echo "<a class='gs_ignorenavigate' href='pms.php'><div class='gs_framework_module $moduleActiveClass'>$icon<br>PMS</div></a>";
                } elseif ($module->name == "Salespoint") {
                    echo "<a class='gs_ignorenavigate' href='pos.php'><div class='gs_framework_module $moduleActiveClass'>$icon<br/>SalesPoint</div></a>";
                } elseif (strtolower($module->id) == "pmsconference") {
                    echo "<a class='gs_ignorenavigate' href='/pmsconference.php'><div class='gs_framework_module $moduleActiveClass'><i class='fa fa-group'></i><br/>Conference</div></a>";
                } elseif (strtolower($module->id) == "express") {
                    echo "<a class='gs_ignorenavigate' href='/express.php'><div class='gs_framework_module $moduleActiveClass'><i class='fa fa-fighter-jet'></i><br/>Express</div></a>";
                } elseif ($module->name == "Comfort") {
                    echo "<a class='gs_ignorenavigate' href='/comfort.php'><div class='gs_framework_module $moduleActiveClass'><i class='fa fa-sun-o'></i><br/>Comfort</div></a>";
                } elseif (strtolower($module->name) == "apac") {
                    echo "<a class='gs_ignorenavigate' href='apac.php'><div class='gs_framework_module $moduleActiveClass'>$icon<br/>Apac</div></a>";
                } elseif (strtolower($module->name) == "invoicing") {
                    echo "<a class='gs_ignorenavigate' href='invoicing.php'><div class='gs_framework_module $moduleActiveClass'>$icon<br/>Invoicing</div></a>";
                } elseif (strtolower($module->name) == "settings") {
                    echo "<a class='gs_ignorenavigate' href='settings.php'><div class='gs_framework_module $moduleActiveClass'>$icon<br/>Settings</div></a>";
                } else {
                    echo "<a class='gs_ignorenavigate' href='/?changeGetShopModule=$module->id&scopeid=$scopeId'><div class='gs_framework_module $moduleActiveClass'>$icon<br>$module->name</div></a>";
                }
            }
            //switch to v5
            $storeId = $factory->getApi()->getStoreManager()->getStoreId();
            if(isV5Customer($storeId)) {
                $v5_url = 'https://v5-'.$_SERVER['SERVER_NAME'].'?token='.session_id();
                echo "<a class='gs_ignorenavigate' href='$v5_url'><div class='gs_framework_module'><i class='fa fa-toggle-up'></i><br/>Switch to V5</div></a>";    
            }

            if ($printPageMenuInModulesMenu) {
                $this->printSupportMenuIcon();
            }
                    
            if($factory->getStore()->id == "13442b34-31e5-424c-bb23-a396b7aeb8ca") {
                echo "<a class='gs_ignorenavigate' href='/getshop.php?page=inbox&gs_getshopmodule='><div class='gs_framework_module active'><i class='fa fa-group'></i><br>Getshop support</div></a>";
            }
            
            echo "<a class='gs_ignorenavigate' href='/logout.php?goBackToHome=true'><div class='gs_framework_module'><i class='gsicon-user-lock'></i> <div>Sign out</div></div></a>";
        echo "</div>";
        $text = $printPageMenuInModulesMenu ? "Menu" : "Modules";
        
        echo "<div class='gs_framework_module modulechangericoncontainer'><i class='fa gsicon-menu modulechangericon'></i>";
            echo "<div>$text</div>"; 
        echo "</div>";
        
        if (!$printPageMenuInModulesMenu) {
            $this->printSupportMenuIcon();
        }

        
        
        ?>
        <script>
            $('.modulechangericon').click(function() {
                $('.gs_framework_modules').toggle();
            });
        </script>
        <style>
            .modulechangericoncontainer { float:left; }
            .gs_framework_modules {
                text-align: left;
                position: absolute;
                width: 623px;
                left: 80px;
                top: 76px;
                z-index: 3;
                font-size: 0px;
                display: none;  
                background-color: #23314e;
                padding-bottom: 10px;
                border-top: solid 1px rgba(255,255,255,0.2);
            }
            .gs_framework_module i { font-size: 40px; margin-top:5px;cursor:pointer; }
            .gs_framework_module { display:inline-block;color:#fff; width: 80px;text-align: center; }
            .gs_framework_modules .gs_framework_module i {
                padding-bottom: 10px;
            }

            .gs_framework_modules .gs_framework_module {
                display:  inline-block;
                right: 30px;
                top: 0px;
                width: 120px;
                height: 100px;
                background-color: #FFF;
                z-index: 1;
                text-align: center;
                box-sizing: border-box;
                font-size: 11px;
                color: rgba(255,255,255,0.8);
                background-color: #23314e;
                vertical-align: central;
                box-sizing: border-box;
                padding-bottom: 20px;
            }
        </style>
        <?php
    }

    public static function getSalesPointSettingsLeftMenu() {
        $menu = new \ModulePageMenu();
        $menu->entries[] = new ModulePageMenuItem("GetShop Devices", "devices", "");
        $menu->entries[] = new ModulePageMenuItem("Cashpoints", "cashpoints", "");
        $menu->entries[] = new ModulePageMenuItem("Views", "views", "");
        $menu->entries[] = new ModulePageMenuItem("Tables", "tables", "");
        $menu->entries[] = new ModulePageMenuItem("Other", "othersettings", "");
        $menu->entries[] = new ModulePageMenuItem("Warehouses", "warehouses", "");
        return $menu;
    }

    public static function getApacLeftMenu() {
        $menu = new \ModulePageMenu();
        $menu->entries[] = new ModulePageMenuItem("Gateways", "configuration", "fa fa-server");
        $menu->entries[] = new ModulePageMenuItem("Access Groups", "accessgroups", "fa fa-users");
        $menu->entries[] = new ModulePageMenuItem("Locks", "lockssetting", "fa fa-key");
        $menu->entries[] = new ModulePageMenuItem("General Settings", "gensettings", "fa fa-gears");
        return $menu;
    }

    public static function getSettingsLeftMenu() {
        $menu = new \ModulePageMenu();
        $menu->entries[] = new ModulePageMenuItem("Other", "othersettings", "fa gsicon-gs-gears");
        $menu->entries[] = new ModulePageMenuItem("User accounts", "useraccounts", "fa fa-user");
        $menu->entries[] = new ModulePageMenuItem("Mail settings", "mailsettings", "fa fa-inbox");
        
        return $menu;
    }

    public function printHorizantalMenu($user, $store = null) {
        $pluginpages = array();
        if($store != null) {
            $pluginpages = $store->configuration->additionalPlugins;
        }
        $menuEntries = $this->getEntries();
        if ($this->moduleName == "pms") {
            $useraccess = (array)$user->pmsPageAccess;
        }
        
        if ($this->moduleName == "salespoint") {
            $useraccess = (array)$user->salesPointPageAccess;
        }
        
        ?>
        <div class='mobilemenubtn' onclick='$(".pmsmenuentries").show();$(".gsarea[area=\"header\"] .mobilemenubtn").hide();'>
            <i class='fa fa-bars'></i>
            Menu
        </div>
        
        <div class="pmsmenuentries horizontal">
                <div class="pmsentries">
                    <? foreach ($menuEntries as $entry) {
                        if(!empty($useraccess)) {
                            if(!in_array($entry->getPageId(),$useraccess)) {
                                continue;
                            }
                        }
                        if($entry->getPageId() == "getshopsupport") {
                            continue;
                        }
                        $subEntries = $entry->getSubEntries();
                        
                        $activeSubEntries = array();
                        foreach ($subEntries as $subEntry) {
                            if ($subEntry->shouldPluginPageBeVisibleForGetShopAdminsWhenDeactived() && stristr($user->emailAddress, "@getshop.com")) {
                                $activeSubEntries[] = $subEntry;
                                continue;
                            }
                            
                            if ($subEntry->getPluginPageName() != null && !in_array($subEntry->getPluginPageName(),$pluginpages)) {
                                continue;
                            }
                            
                            $activeSubEntries[] = $subEntry;
                        }
                        
                        $hassubs =  count($activeSubEntries) ? "hassubentries" : false;

                        if($entry->getPageId() == "home" && $this->moduleName == 'support') {
?>
                        <div class="entry">

                            <a href="https://support.zauistay.com/hc/en-us/" target="_blank"><div><i class="fa <? echo $entry->getIcon(); ?>"></i>  <? echo $entry->getName(); ?> </div></a>
                        </div>
                        <?php
                            continue;
                        }

                        ?>
                        <div class="entry <?php echo $hassubs; ?>">

                            <a href="?page=<? echo $entry->getPageId(); ?>&gs_getshopmodule=<? echo \PageFactory::getGetShopModule(); ?>"><div><i class="fa <? echo $entry->getIcon(); ?>"></i>  <? echo $entry->getName(); ?> </div></a>
                            <?php
                            if ($hassubs) {
                                echo "<div class='gss_dropdownmenu'>";
                            }
                            
                                foreach ($activeSubEntries as $subEntry) {
                                    echo "<span>";
                                        $subPageId = $subEntry->getPageId();
                                        $subPageName = $subEntry->getName();
                                        echo "<a href='/pms.php?page=$subPageId'><div class='pmssubentry'>$subPageName</div></a>";
                                    echo "</span>";
                                }

                            if ($hassubs) {
                                echo "</div>";
                            }
                            ?>
                        </div>
                    <?php } ?>
                </div>
            </div>
        <?
    }

    public function printSupportMenuIcon() {
        if ($this->moduleName == "support")
            return;
        
        $factory = IocContainer::getFactorySingelton();
        if($factory->getStore()->id != "13442b34-31e5-424c-bb23-a396b7aeb8ca") {
            echo "<span id='opensupportcenter' href='/getshopsupport.php' target='_blank'>";
                echo "<div class='gs_framework_module modulechangericoncontainer' id='opensupportcases' style='position:relative;'><i class='fa fa-question'></i>";
                    echo "<div class='support'>".$factory->__f("I Need Help")."</div>";
                echo "</div>";
            echo "</span>";
        echo "</span>";
        }
        
        ?>
        <script>
            $('#opensupportcenter').on('click',function() {
                if($(this).find('#ticketnotifications').length > 0) {
                    if($('.ticketnotificationsarea').is(':visible')) {
                        $('.ticketnotificationsarea').hide();
                    } else {
                        $('.ticketnotificationsarea').fadeIn();
                    } 
                } else {
                    window.open("https://support.zauistay.com/hc/en-us", "_blank");
                }
            });
            
            function getSupportTicketsNotifications() {
                $.get("/getticketnotifcations.php", function(res) {
                    if($('.ticketnotificationsarea').is(':visible')) {
                        return;
                    }
                    $('#opensupportcases').find('#ticketnotifications').remove();
                    $('#opensupportcases').prepend(res);
                });
            }
            setTimeout(function() {
                getSupportTicketsNotifications();
            }, "3000");
            
        </script>
        <?php
    }

}
