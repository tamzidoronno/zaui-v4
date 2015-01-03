<?
include '../loader.php';

$factory = IocContainer::getFactorySingelton();
if (isset($_POST['payment_method_nonce'])) {
    $factory->getApi()->getBrainTreeManager()->pay($_POST['payment_method_nonce']);
}
?>
<script src="https://js.braintreegateway.com/v2/braintree.js"></script>

<form id="checkout" action="#" method="post">
    <input data-braintree-name="number" value="4111111111111111">
    <input data-braintree-name="expiration_date" value="10/20">
    <input type="submit" id="submit" value="Pay">
</form>

<script>
    var token = "<? echo $factory->getApi()->getBrainTreeManager()->getClientToken(); ?>";
    braintree.setup(token, "custom", {id: "checkout"});
</script>
