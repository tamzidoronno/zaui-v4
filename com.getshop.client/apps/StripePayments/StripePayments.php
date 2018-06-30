<?php
namespace ns_3d02e22a_b0ae_4173_ab92_892a94b457ae;

class StripePayments extends \PaymentApplication implements \Application {
    public function getDescription() {
        return "Start accepting payments using stripe.";
    }

    public function getName() {
        return "StripePayments";
    }

    public function render() {
        
    }
    
    public function addPaymentMethods() {
        $namespace = __NAMESPACE__;
        $this->addPaymentMethod("Visa / MasterCard", "/showApplicationImages.php?appNamespace=$namespace&image=Dibs.png", "dibs");
    }
    
    
    public function simplePayment() {
        $this->preProcess();
    }
    
    /**
     * Should display you configuration page.
     */
    public function renderConfig() {
        $this->includefile("stripeconfiguration");
    }
    
    public function hasPaymentLink() {
        return true;
    }

    public function completeCheckout() {
        require('../apps/StripePayments/stripe-php-6.10.0/init.php');
        \Stripe\Stripe::setApiKey("sk_test_BQokikJOvBiI2HlWgH4olfQ2");

        // Token is created using Checkout or Elements!
        // Get the payment token ID submitted by the form:
        $token = $_POST['data']['token'];
        $order = $this->getApi()->getOrderManager()->getOrderWithIdAndPassword($_POST['data']['orderid'], "gfdsg9o3454835nbsfdg");
        $amount = $this->getApi()->getOrderManager()->getTotalAmount($order);
        $amount *= 100;
        $amount = round($amount);
        $currency = \ns_9de54ce1_f7a0_4729_b128_b062dc70dcce\ECommerceSettings::fetchCurrencyCode();

        $charge = \Stripe\Charge::create([
            'amount' => $amount,
            'currency' => $currency,
            'description' => 'GetShop order' . $order->incrementOrderId,
            'source' => $token,
        ]);
        
        if($charge['paid'] == 1) {
            $amount = $charge['amount'] / 100;
            $this->getApi()->getOrderManager()->markAsPaidWithPassword($order->id, $this->convertToJavaDate(time()), $amount, "fdsvb4354345345");
            echo "1";
        } else {
            echo "0";
        }
    }
    
    public function preProcess() {
        $email = "";
        $currency = \ns_9de54ce1_f7a0_4729_b128_b062dc70dcce\ECommerceSettings::fetchCurrencyCode();
        if(isset($this->order->cart->address->emailAddress)) {
            $email = $this->order->cart->address->emailAddress;
        }
        $amount = $this->getApi()->getOrderManager()->getTotalAmount($this->order)*100;
        $amount = round($amount);

        $key = $this->getConfigurationSetting("key");
        $title = $this->getConfigurationSetting("title");
        
        ?>
        <div style='text-align: center; font-size: 16px;'>
            <i class='fa fa-spin fa-spinner'></i>
            <div>Loading payment window. Please wait</div>
        </div>
        <script src="https://checkout.stripe.com/checkout.js"></script>
        <script>
            var handler = StripeCheckout.configure({
            key: '<?php echo $key; ?>',
            image: 'https://stripe.com/img/documentation/checkout/marketplace.png',
            locale: 'auto',
            token: function(token) {
              var event = thundashop.Ajax.createEvent('','completeCheckout',"ns_3d02e22a_b0ae_4173_ab92_892a94b457ae\\StripePayments", {
                  "orderid" : '<?php echo $this->order->id;?>',
                  "token" : token.id
              });

              thundashop.Ajax.postWithCallBack(event,function(res) {
                  if(res == "1") {
                      window.location.href='?page=payment_success';
                  } else {
                      window.location.href='?page=payment_failed';
                  }
              });
            }
          });
          
        handler.open({
          name: '<?php echo $title; ?>',
          description: 'Payment for order <?php echo $this->order->incrementOrderId; ?>',
          zipCode: false,
          email : "<?php echo $email; ?>",
          currency : "<?php echo $currency; ?>",
          amount: <?php echo $amount; ?>
        });
        e.preventDefault();

        // Close Checkout on page navigation:
        window.addEventListener('popstate', function() {
          handler.close();
        });
        </script>
        <?php
    }
    

    public function saveSettings() {
        $this->setConfigurationSetting("key", $_POST['key']);
        $this->setConfigurationSetting("title", $_POST['title']);
    }
    
}
?>
