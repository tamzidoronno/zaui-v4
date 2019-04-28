<?php
namespace ns_3d02e22a_b0ae_4173_ab92_892a94b457ae;

class StripePayments extends \PaymentApplication implements \Application {
    public function getDescription() {
        return "Start accepting payments using stripe.";
    }

    public function getName() {
        return "StripePayments";
    }
    public function getIcon() {
        return "card.png";
    }

    public function render() {
        if ($this->getCurrentOrder()->status != 7) {
            $this->renderOnlinePaymentMethod();
        } else {
            $this->renderDefault();
        }
    }
    
    public function hasPaymentProcess() {
         return ($this->order != null && $this->order->status != 7);
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
        $token = $_POST['data']['token'];
        $orderId = $_POST['data']['orderid'];
        $paid = $this->getApi()->getStripeManager()->createAndChargeCustomer($orderId, $token);
        if($paid) {
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

        $key = $this->getConfigurationSetting("pkey");
        $title = $this->getConfigurationSetting("title");
        
//        if(!$this->getApi()->getStoreManager()->isProductMode()) {
//            $key = "pk_test_g6do5S237ekq10r65BnxO6S0";
//            $title = "TEST";
//        }
        ?>
        <div style='text-align: center; font-size: 16px;'>
            <i class='fa fa-spin fa-spinner'></i>
            <div>Loading payment window. Please wait</div>
        </div>
        <script src="https://checkout.stripe.com/checkout.js" async></script>
        <script>
            
            var timeout = setInterval(function() {
                
                if(typeof(StripeCheckout) === "undefined") {
                    return;
                }
                clearInterval(timeout);
                
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
                      if(res === "1") {
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
                
            }, "500");
        </script>
        <?php
    }
    

    public function saveSettings() {
        $this->setConfigurationSetting("key", $_POST['key']);
        $this->setConfigurationSetting("pkey", $_POST['pkey']);
        $this->setConfigurationSetting("title", $_POST['title']);
    }

    public function getColor() {
        return "blue";
    }    
}
?>
