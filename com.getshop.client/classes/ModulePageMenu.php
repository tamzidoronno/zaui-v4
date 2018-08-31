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
class ModulePageMenu {

    private $entries = array();

    function __construct() {
        
    }

    public static function getTopMenuPms() {
        $menu = new \ModulePageMenu();
        $menu->entries[] = new ModulePageMenuItem("Dashboard", "home", "gsicon-gs-dashboard");
        $menu->entries[] = new ModulePageMenuItem("Bookings", "a90a9031-b67d-4d98-b034-f8c201a8f496", "gsicon-gs-booking");
        $menu->entries[] = new ModulePageMenuItem("New", "4d89b5cf-5a00-46ea-9dcf-46ea0cde32e8", "gsicon-gs-new");
        $menu->entries[] = new ModulePageMenuItem("Availability", "0da68de9-da08-4b60-9652-3ac456da2627", "gsicon-gs-availability");
        $menu->entries[] = new ModulePageMenuItem("Reports", "afe687b7-219e-4396-9e7b-2848f5ed034d", "gsicon-gs-reports");
        $menu->entries[] = new ModulePageMenuItem("Prices", "394bb905-8448-45c1-8910-e9a60f8aebc5", "gsicon-gs-prices");
        $menu->entries[] = new ModulePageMenuItem("Cleaning", "e03b19de-d1bf-4d1c-ac40-8c100ef53366", "gsicon-gs-cleaning");
        $menu->entries[] = new ModulePageMenuItem("CRM", "4f66aad0-08a0-466c-9b4c-71337c1e00b7", "gsicon-users");
        $menu->entries[] = new ModulePageMenuItem("Settings", "messages", "gsicon-gs-gears");
        return $menu;
    }

    /**
     * 
     * @return ModulePageMenuItem[]
     */
    function getEntries() {
        return $this->entries;
    }

    public function renderTop() {
        $menuEntries = $this->getEntries();
        ?>

        <div class="Menu">
            
            <?php
            $this->printChangedMenues();
            ?>
            <div class="menuentries horizontal">
                <div class="entries">
                    <? foreach ($menuEntries as $entry) { ?>
                        <div class="entry"><a href="/index2.php?page=<? echo $entry->getPageId(); ?>&gs_getshopmodule=<? echo \PageFactory::getGetShopModule(); ?>"><div><i class="fa <? echo $entry->getIcon(); ?>"></i>  <? echo $entry->getName(); ?> </div></a></div>
                    <? } ?>
                </div>
            </div>
        </div>
        <?
    }

    public function renderLeft() {
        $menuEntries = $this->getEntries();
        ?>

        <div class="Menu">
            <div class="menuentries vertical">
                <div class="entries">
                    <? foreach ($menuEntries as $entry) { ?>
                        <div class="entry"><a href="/index2.php?page=<? echo $entry->getPageId(); ?>&gs_getshopmodule=<? echo \PageFactory::getGetShopModule(); ?>"><div><i class="fa <? echo $entry->getIcon(); ?>"></i>  <? echo $entry->getName(); ?> </div></a></div>
                                    <? } ?>
                </div>
            </div>
        </div>
        <?
    }

    public static function getPmsLeftMenu() {
        $menu = new \ModulePageMenu();
        $menu->entries[] = new ModulePageMenuItem("Messages", "messages", "");
        $menu->entries[] = new ModulePageMenuItem("Payment methods", "paymentmethods", "");
        $menu->entries[] = new ModulePageMenuItem("Cleaning config", "cleaningconfig", "");
        $menu->entries[] = new ModulePageMenuItem("Self checkin-in terminals", "selfcheckingterminals", "");
        $menu->entries[] = new ModulePageMenuItem("Products / addons", "productsandaddonsconfig", "");
        $menu->entries[] = new ModulePageMenuItem("Terms and conditions", "termsconfig", "");
        $menu->entries[] = new ModulePageMenuItem("Instruction set", "instructionconfig", "");
        $menu->entries[] = new ModulePageMenuItem("Rooms", "roomsconfig", "");
        $menu->entries[] = new ModulePageMenuItem("Notifications", "notifications", "");
        $menu->entries[] = new ModulePageMenuItem("Budget", "budget", "");
        $menu->entries[] = new ModulePageMenuItem("RateManager/TripTease", "triptease", "");
        $menu->entries[] = new ModulePageMenuItem("GetShop Express", "getshopexpressconfig", "");
        $menu->entries[] = new ModulePageMenuItem("Restricitions", "restrictionsconfig", "");
        $menu->entries[] = new ModulePageMenuItem("Channel manager", "channelmanagerconfig", "");
        $menu->entries[] = new ModulePageMenuItem("Lock setup", "lockconfig", "");
        $menu->entries[] = new ModulePageMenuItem("Global setttings", "globalsettings", "");
        $menu->entries[] = new ModulePageMenuItem("PGA", "pga", "");
        return $menu;
    }

    public function printChangedMenues() {
        $factory = IocContainer::getFactorySingelton();
        $modules = $factory->getApi()->getPageManager()->getModules();
        echo "<div class='gs_framework_modules'>";
        foreach ($modules as $module) {
            $moduleActiveClass = $factory->getPage()->javapage->getshopModule == $module->id ? "active" : "";
            $activeModule = $factory->getPage()->javapage->getshopModule == $module->id ? $module : $activeModule;
            if (!$activeModule && $module->id == "cms") {
                $activeModule = $module;
            }
            $icon = "<i class='fa gs".$module->fontAwesome."'></i>";
            $scopeId = $_POST['scopeid'];
            echo "<a class='gs_ignorenavigate' href='/?changeGetShopModule=$module->id&scopeid=$scopeId'><div class='gs_framework_module $moduleActiveClass'>$icon"."<div>".$module->name."</div></div></a>";
        }
        echo "</div>";
        echo "<div class='gs_framework_module modulechangericoncontainer'><i class='fa gsicon-menu modulechangericon'></i> <div>Modules</div></div>";

        
        
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
                padding-bottom: 50px;
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
                height: 120px;
                background-color: #FFF;
                z-index: 1;
                text-align: center;
                box-sizing: border-box;
                font-size: 11px;
                color: rgba(255,255,255,0.8);
                background-color: #23314e;
                vertical-align: central;
                box-sizing: border-box;
                padding-top: 50px;
            }
        </style>
        <?php
    }

}
