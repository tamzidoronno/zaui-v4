 <script type="text/javascript" src="js/jquery-1.9.0.js"></script>
<style>
    body { background-color:#000; color:#fff; padding: 40px; }
    .col { width: 33%; display:inline-block; }
    .tabtotal { border-top: solid 1px; padding-top: 10px; margin-top: 10px; }
    .cartitemline { font-size: 26px; }
    .oldprice {  text-decoration: line-through; margin-right: 10px; }
</style>

<script>
    setInterval(function() {
        $.get( "/refresher.php", function( data ) {
            if(data === "1") {
                window.location.reload();
            }
        });
    }, "200");
</script>

<?php
include '../loader.php';
session_start();
$pageFactory = new \PageFactory("salespoint");
$page = $pageFactory->getPage(@$_GET['page']);
$showingModal = isset($_SESSION['gs_currently_showing_modal']) ? "active" : "";
$printPageMenuInModulesMenu = true;
$versionnumber = "1.0";
$factory = IocContainer::getFactorySingelton();

$app = new \ns_57db782b_5fe7_478f_956a_ab9eb3575855\SalesPointNewSale();
$tab = $app->getCurrentTab();
$cartItems = (array)$tab->cartItems;

if(sizeof($cartItems) == 0) {
    return;
}

foreach ($cartItems as $item) {
    $foodIcon = $item->product->isFood ? "<i title='Food' class='fa fa-cutlery'></i> " : "";

    $priceToUse = $item->product->price;

    if ($item->overridePriceIncTaxes) {
        $priceToUse = $item->overridePriceIncTaxes;
    }

    $priceText = $app->getPriceHtml($item);

    echo "<div class='cartitemline' cartitemid='$item->cartItemId'>";
    echo "<div class='col count'>" . $item->count . "</div>";
    echo "<div class='col productname'>$foodIcon" . $item->product->name . "</div>";
    echo "<div class='col price'>" . $priceText . "</div>";

    echo "<div class='details'>";

    if (count((array) $item->product->selectedExtras)) {

        foreach ($item->product->selectedExtras as $optionId => $extraIds) {
            echo "<div class='row_for_extras'>";
            $extraOption = $this->getExtraOption($item->product->extras, $optionId);
            if (count($extraIds)) {
                echo "<b>" . $extraOption->name . "</b>:";
                $i = 0;
                foreach ($extraIds as $extraId) {
                    $i++;
                    $extraOptionSub = $this->getExtraOption($extraOption->extras, $extraId);
                    echo $extraOptionSub->name;
                    if ($i < count($extraIds)) {
                        echo ",";
                    }
                }
            }
            echo "</div>";
        }
    }
    ?>

    <span class="gs_shop_small_icon" title="<? echo $app->__f("Discount"); ?>" cartitemid='<? echo $item->cartItemId; ?>' gstype='numpad' gsmethod='methodToExecute' gsnumpadtitle='Please enter discount percent' gsnumpad_on_ok='app.SalesPointNewSale.itemDiscountChange'>
        <span class="fa-stack fa-lg">
            <i class="fa fa-certificate fa-stack-2x"></i>
            <i class="fa fa-tag fa-stack-1x fa-inverse"></i>
        </span> 
    </span>

    <span class="gs_shop_small_icon removefromtab">
        <span class="fa-stack fa-lg">
            <i class="fa fa-stack-2x  fa-trash "></i>
        </span> 
    </span>
    <?
}
?>

<div class="total tabtotal">
    <?php
    $total = $factory->getApi()->getPosManager()->getTotal($tab->id);
     echo "<div class='col count'>" . $app->__f("Total") . "</div>";
     echo "<div class='col count'></div>";
     echo "<div class='col count'>" . $total . "</div>";

    if ($tab->cashWithDrawal) {
        ?>
        <div>
            + Cash withdraw: <? echo $tab->cashWithDrawal; ?>
        </div>
    <? } ?>

</div>