<?
/* @var $this ns_57db782b_5fe7_478f_956a_ab9eb3575855\SalesPointNewSale */
chdir("../../../../");
include '../loader.php';
$factory = IocContainer::getFactorySingelton(false);
$product = $factory->getApi()->getProductManager()->getProduct($_GET['productId']);

$extras = $product->extras;

if (!$extras) {
    echo "false";
    return;
} 
?>
<div class="productconfigarea" productid="<? echo $product->id; ?>">
<?
foreach ($extras as $extra) {
    echo "<div class='extraoptiongroup' type='$extra->type' extraid='$extra->id'>";
        echo "<h2>".$extra->name."</h2>";

        foreach ($extra->extras as $option) {
        ?>
            <div class="product_config_box checkbox">

                <div class="checkboxinner" optionid="<? echo $option->optionsubid; ?>">
                    <i class="fa fa-check-circle"></i>
                    <i class="fa fa-circle"></i>
                </div>

                <div class="inner">
                    <div class="productname"><? echo $option->name; ?></div>
                    <?
                    if ($option->extraPriceDouble) {
                        echo "( Kr ".$option->extraPriceDouble.",- )";
                    }
                    ?>
                </div>
            </div>
            <?
        }
    echo "</div>";
}
?>

<div class="productionbuttonarea">
    <div class="shop_button addProductToCurrentTab" >
        <?
        foreach ($_POST['data'] as $key => $val) {
            echo $key."='$val' ";
        }
        ?>
        Legg i handlekurv <i class="fa fa-arrow-right"></i> 
    </div>
</div>

</div>