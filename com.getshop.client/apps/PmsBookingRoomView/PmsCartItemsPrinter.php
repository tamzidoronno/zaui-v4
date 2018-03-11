<?php
namespace ns_f8cc5247_85bf_4504_b4f3_b39937bd9955;

class PmsCartItemsPrinter {
    /* @var $api GetShopApi */
    var $api;
    
    function __construct($api) {
        $this->api = $api;
    }

    public function printItems($cartItems) {
        $total = 0;
        foreach($cartItems as $item) {
            /* @var $item \core_cartmanager_data_CartItem */
            $product = $this->getApi()->getProductManager()->getProduct($item->product->id);
            ?>
            <div class='cartitemrow'>
                <span class="col orderid">
                    <?php
                    if($item->orderId) {
                        $order = $this->getApi()->getOrderManager()->getOrder($item->orderId);
                        echo $order->incrementOrderId;
                    }
                    ?>
                </span>
                <span class="col colproduct">
                    <?php echo $product->name; ?>
                </span>
                <span class="col coldatestart">
                    <?php
                    if($item->startDate) {
                        echo date("d.m.Y", strtotime($item->startDate)); 
                    }
                    ?>
                </span>
                <span class="col coldateend">
                    <?php 
                    if($item->endDate) {
                        echo date("d.m.Y", strtotime($item->endDate));
                    }
                    ?>
                </span>
                <span class="col colcount">
                    <?php echo $item->count; ?>
                </span>
                <span class="col colprice">
                    <?php echo round($item->product->price); ?>
                </span>
                <span class="col coltotal">
                    <?php 
                    $total += ($item->product->price * $item->count);
                    echo round($item->product->price) * $item->count; 
                    ?>
                </span>
            </div>
            <?php
        }
        ?>
        <div class='cartitemrow' style="border-bottom: solid 0px;">
            <span class="col orderid">
            </span>
            <span class="col colproduct">
                Total unpaid amount
            </span>
            <span class="col coldatestart">
            </span>
            <span class="col coldateend">
            </span>
            <span class="col colcount">
            </span>
            <span class="col colprice">
            </span>
            <span class="col coltotal">
                <?php echo $total; ?>
            </span>
        </div>

        <?php
    }

    /**
     * @return \GetShopApi
     */
    public function getApi() {
        return $this->api;
    }
}
?>

<style>
    .cartitemrow { border-bottom: solid 1px #bbb; padding: 5px; }
    .colproduct { width: 200px !important; }
    .orderid { width: 50px !important; }
    .coldatestart,.coldateend { width: 70px !important; }
    .colprice,.coltotal,.colcount { width: 30px !important; }
</style>
    