<?php
namespace ns_900e5f6b_4113_46ad_82df_8dafe7872c99;

class CartManager extends \SystemApplication implements \Application {
    /** @var APICartManager */
    public $cart;
    public $order;
    public $oldOrder;
    
    private $shippingApplication;
    private $paymentApplication; 
    
    public function getDescription() {
        return $this->__f("Add a shoppingcart to your header and it will be visible for you customers");
    }
    
    public function getName() {
        return $this->__f("Shopping cart");
    }
    
    public function postProcess() {}
    
    public function preProcess() {
        if (isset($_GET['cartCustomerId'])) {
            $_SESSION['cartCustomerId'] = $_GET['cartCustomerId'];
        }
        
        if (isset($_GET['action'])) {
            $action = $_GET['action'];
            $this->sendAddProductToCartEvent($action);
            $this->sendRemoveProductFromCartEvent($action);
        }
    }
        
    public function checkEmail() {
        $email = $_POST['data']['email'];
        $result = $this->getApi()->getUserManager()->doEmailExists($email);
        if($result) {
            echo "true";
        } else {
            echo "false";
        }
    }
    
    public function getPriceWithTaxSpecifed($price, $product) {
        if (isset($this->getFactory()->getSettings()->remove_taxes) && $this->getFactory()->getSettings()->remove_taxes->value === "true") {
            if (isset($product->taxGroupObject) && $product->taxGroupObject->taxRate > 0 ) {
                $factor = ($product->taxGroupObject->taxRate/100)+1;
                $price = $price / $factor;
            }
        }
        
        return $price;
    }
    
    /**
     * Should only be called if the 
     * preprocessing function is not called 
     */
    private function init() {
        if (isset($_SESSION['cartCustomerId'])) {
            $this->initAddress();
        }
        if(isset($_POST['data']['appId'])) {
            $_SESSION['appId'] = $_POST['data']['appId'];
        }
        
        $this->cart = $this->getApi()->getCartManager()->getCart();
        
        $appPool = $this->getFactory()->getApplicationPool();
        $apps = $this->getShipmentApplications();
        
        if (count($apps) == 1 && !$apps[0]->hasSubProducts()) {
            $this->shippingApplication = $apps[0];
        }

        if (isset($_SESSION['shippingtype'])) {
            $shippingApp = $this->getApi()->getStoreApplicationPool()->getApplication($_SESSION['shippingtype']);
            if ($shippingApp) {
                $this->shippingApplication = $appPool->createInstace($shippingApp);
            } 
            
            if ($this->shippingApplication == null) {
                unset($_SESSION['shippingtype']);
            }
            
            $this->shippingApplication->cart = $this->cart;
        }

        if (isset($_SESSION['appId'])) {
            $app = $this->getApi()->getStoreApplicationPool()->getApplication($_SESSION['appId']);
            $this->paymentApplication = $this->getFactory()->getApplicationPool()->createInstace($app);
            
        }
        
        $paymentApps = $this->getPaymentApplications();
        if (count($paymentApps) == 1) {
            $this->paymentApplication = $paymentApps[0];   
        }
        
        $this->initAddress();
    }
    
    private function initAddress() {
        $user = \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject();
        
        if (isset($_SESSION['cartCustomerId'])) {
            $user = $this->getApi()->getUserManager()->getUserById($_SESSION['cartCustomerId']);
        }
        
        if ($user != null && !isset($_SESSION['tempaddress']) && $user->address != null) {
            $address = $user->address;
            $_SESSION['tempaddress']['fullName'] = $address->fullName;
            $_SESSION['tempaddress']['city'] = $address->city;
            $_SESSION['tempaddress']['postCode'] = $address->postCode;
            $_SESSION['tempaddress']['address'] = $address->address;
            $_SESSION['tempaddress']['emailAddress'] = $address->emailAddress;
            $_SESSION['tempaddress']['phone'] = $address->phone;
        }
    }
    
    private function sendAddProductToCartEvent($action) {
        if ($action == "addtocart") {
            $this->getApi()->getCartManager()->addProduct($_GET['productid'], 1);
        }
    }
    
    private function sendRemoveProductFromCartEvent($action) {
        if ($action == "removeproduct") {
            $this->getApi()->getCartManager()->removeProduct($_GET['cartItemId']);
        }
    }
    
    private function doPayment() {
        if (!isset($this->paymentApplication) || $this->paymentApplication == null) {
            $this->redirectToSuccessPaymentPage();
        } else if (isset($this->paymentApplication) && method_exists($this->paymentApplication, "preProcessPayment")) {
            $this->paymentApplication->order = $this->order;
            $this->paymentApplication->initPaymentMethod();
            $this->doPaymentWithPreProcessing();
        } else {             
            $this->paymentApplication->order = $this->order;
            $this->paymentApplication->initPaymentMethod();
            $finished = $this->paymentApplication->preProcess();
            if (!isset($finished) || !$finished) {
                $this->showRedirectInformation();
            } else {
                $this->redirectToSuccessPaymentPage();
            }
        }
    }
    
    public function render() {
        $this->init();
        if ($this->isSmallCartView()) {
            echo "<div class='small_cart_dom'>";
            $this->includefile("smallcartoverview");
            echo "</div>";
        } else {

            echo "<div class='main_cart_dom'>";
            if(isset($_GET['subpage'])) {
                if($_GET['subpage'] == "paymentform") {
                    $this->includefile("paymentform");
                }
                if($_GET['subpage'] == "gotopayment") {
                    $this->SaveOrder();
                }
            } else {
                $this->includefile("cartmain");
            }
            echo "</div>";
        }
    }
    
    private function doPaymentWithPreProcessing() {
        if (!$this->paymentApplication->preProcessPayment()) {
            echo "<div class='paymentfailed'> Please recheck your payment details.</div>";
            $this->includefile("cartmain");
        } else {
            $this->redirectToSuccessPaymentPage();
        }       
    }
        
    private function redirectToSuccessPaymentPage() {
        \HelperCart::clearSession();
        $this->getApi()->getCartManager()->clear();
        echo $this->__w("Thank you for your order.");
    }
    
    
    public function getTotalAmount() {
        $cartTotal = $this->getApi()->getCartManager()->getCartTotalAmount();
        $shipping = $this->canCalculateShippingPrice() ? $this->getShippingPrice() : 0;
        return $shipping + $cartTotal;
    }
    
    public function canCalculateShippingPrice() {
        if ((isset($this->shippingApplication) && !$this->shippingApplication->hasSubProducts())) {
            return true;
        }
        
        if (isset($this->shippingApplication) && $this->shippingApplication->hasSubProducts() && isset($_SESSION['shippingproduct'])) {
            return true;
        }
        
        if (count($this->getShipmentApplications()) == 0) {
            return true;
        }
        
        return false;
    }
    
    public function getShippingPrice($fromJava=false) {
        if(isset($this->cart->isShippingFree) && $this->cart->isShippingFree) {
            return 0;
        }
        
        if ($this->shippingApplication && $this->canCalculateShippingPrice()) {
            $shippingProduct = isset($_SESSION['shippingproduct']) ? $_SESSION['shippingproduct'] : "";
            $javaShippingPrice = $this->getApi()->getCartManager()->getShippingCost();
            
            $shipping = $this->canCalculateShippingPrice() ? $this->shippingApplication->getShippingCost($shippingProduct) : 0;
            $this->getApi()->getCartManager()->setShippingCost($shipping);
            
            return $this->getApi()->getCartManager()->getShippingCost();
        }
        
        return 0;
    }
    
    private function createUserObject() {
        $user = new \core_usermanager_data_User();
        
        $user->emailAddress = $_SESSION['tempaddress']['emailAddress'];
        $user->username = $_SESSION['tempaddress']['emailAddress'];
        $user->password = $_SESSION['tempaddress']['password'];
        $user->fullName = $_SESSION['tempaddress']['fullName'];
        $user->cellPhone = $_SESSION['tempaddress']['phone'];
        $user->address = $this->createAddress();
        return $user;
    }
    
    private function createAddress() {
        $loggedInUser = \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject();
        
        $address = $this->getApiObject()->core_usermanager_data_Address();
        $address->fullName = $_SESSION['tempaddress']['fullName'];
        $address->city = $_SESSION['tempaddress']['city'];
        $address->postCode = $_SESSION['tempaddress']['postCode'];
        $address->address = $_SESSION['tempaddress']['address'];
        if (isset($_SESSION['tempaddress']['countrycode'])) {
            $address->countrycode = $_SESSION['tempaddress']['countrycode'];
            $clist = $this->getFactory()->getTerritoriesList();
            $address->countryname = $clist[$address->countrycode];
        }
            
        
        if ($loggedInUser != null) {
            $address->emailAddress = $loggedInUser->emailAddress;
        } else {
            $address->emailAddress = $_SESSION['tempaddress']['emailAddress'];
        }
        $address->phone = $_SESSION['tempaddress']['phone'];
        $address->type = "shipment";
        return $address;
    }
    
    private function isSmallCartView() {
        return $this->getConfigurationSetting("smallcartview") == "true";
    }
    
    public function toggleHeaderMode() {
        if ($this->isSmallCartView()) {
            $this->setConfigurationSetting("smallcartview", "false");
        } else {
            $this->setConfigurationSetting("smallcartview", "true");
        }
    }
    public function SaveOrder() {
        $this->init();
        
        if(isset($_SESSION['tempaddress']['password'])) {
            $user = $this->createUserObject();
            $this->getApi()->getUserManager()->createUser($user);
            $_SESSION['loggedin'] = serialize($this->getApi()->getUserManager()->logOn($user->emailAddress, $user->password));
        }
        
 
        if (count($this->getPaymentApplications()) > 0 && !isset($this->paymentApplication)) {
            ob_clean();
            ob_start();
            header('HTTP/1.1 500 Internal Server Error');
            die("PAYMENT_NOT_SELECTED");
        }
        
        if (isset($this->paymentApplication) && $this->paymentApplication->hasSubProducts()) {
            $this->paymentApplication->doSubProductProccessing();
            if (!$this->paymentApplication->isReadyToPay()) {
                die("PLEASE_CHECK");
            }
        }
        
        if (isset($_SESSION['cartCustomerId'])) {
            $this->order = $this->getApi()->getOrderManager()->createOrderForUser($_SESSION['cartCustomerId']);
        } else {
            $address = $this->createAddress();
            $this->order = $this->getApi()->getOrderManager()->createOrder($address);
        }
        
        
        if (isset($this->shippingApplication)) {
            $this->order->shipping = $this->shippingApplication;
            $this->order->shipping->cost = $this->getShippingPrice();
            $this->getApi()->getOrderManager()->saveOrder($this->order);
        }
        
        if (isset($this->paymentApplication)) {
            $this->order->shipping = $this->shippingApplication;
            if($this->order->shipping) {
                $this->order->shipping->cost = $this->getShippingPrice();
            }
            $this->getApi()->getOrderManager()->saveOrder($this->order);
        }
        
        $this->doPayment();
    }
    
    public function LoadShipmentMethods() {
        $this->init();
        $address = $this->createAddress();
        $this->getApi()->getCartManager()->setAddress($address);
        $_SESSION['checkoutstep'] = "shipping";
    }
    
    public function saveShipping() {
        $_SESSION['checkoutstep'] = "payment";
        $_SESSION['shippingtype'] = isset($_POST['data']['shippingType']) ? $_POST['data']['shippingType'] : "";
        $_SESSION['shippingproduct'] = $_POST['data']['shippingProduct'];        
        $this->init();
    }
    
    /**
     * 
     * @return \PaymentApplication[]
     */
    public function getPaymentApplications($fromCheckout=false) {
        $apps = $this->getFactory()->getApi()->getStoreApplicationPool()->getApplications();
        $result = array();
        foreach($apps as $app) {
            if($app->type == "PaymentApplication") {
                $appInstance = $this->getFactory()->getApplicationPool()->createInstace($app);
                if (count($appInstance->getPaymentMethods($fromCheckout=false)) > 0)
                    $result[] = $appInstance;
            }
        }
        
        return $result;
    }
    
    public function updateProductCount() {
        $data = $_POST['data'];
        $cartItemId = $data['cartItemId'];
        $this->getApi()->getCartManager()->updateProductCount($cartItemId, $data['count']);
    }

    public function getShipmentApplications() {
        $shippingApps = $this->getApi()->getStoreApplicationPool()->getShippingApplications();
        
        $result = array();
        if (is_array($shippingApps)) {
            foreach($shippingApps as $app) {
                $result[] = $this->getFactory()->getApplicationPool()->createInstace($app);
            }
        }
        
        return $result;
    }
    
    public function setPrev() {
        if (!isset($_SESSION['checkoutstep']) || $_SESSION['checkoutstep'] == "shipping") {
            $_SESSION['checkoutstep'] = "address";
        }
        
        if ($_SESSION['checkoutstep'] == "payment") {
            $_SESSION['checkoutstep'] = "shipping";
        }
    }
    
    public function getCount() {
        $i = 0;
        foreach ($this->cart->items as $cartItem) {
            $i += $cartItem->count;
        }
        return $i;
    }
    
    public function renderSmallCartView() {
        $this->init();
        $i = $this->getCount();
        echo "<span class='cart_text'>" . $this->__f("cart") . "</span> <span class='cart_count'>(" . $i . ")</span>";
    }
    
    public function saveTempAddress() {
        $_SESSION['tempaddress'] = $_POST['data'];
    }
    
    public function canGoToPayment($fromShipping=false) {
        // one shipment method added and one paymentmethod
        if (count($this->getShipmentApplications()) == 1 
                && isset($this->shippingApplication) 
                && !$this->shippingApplication->hasSubProducts()
                && (isset($this->paymentApplication) && !$fromShipping)
                && !$this->paymentApplication->hasSubProducts()) {
            
            return true;
        }

        if (isset($this->shippingApplication) 
                && !$this->shippingApplication->hasSubProducts()
                && count($this->getPaymentMethods()) == 0) {
           
            return true;
        }
        
        if (count($this->getShipmentApplications()) == 0 && count($this->getPaymentMethods()) == 0) {
            return true;
        }
        
        if (count($this->getShipmentApplications()) == 0 && count($this->getPaymentMethods()) < 2) {
            if (isset($this->paymentApplication)) {
                return !$this->paymentApplication->hasSubProducts();
            }
            return true;
        }
        
        foreach ($this->getPaymentMethods() as $paymentMethod) {
            if ($paymentMethod->getPaymentApplication()->hasSubProducts()) {
                return false;
            }
        }
        
        if (count($this->getPaymentMethods()) < 2 && $fromShipping) {
            return true;
        }
        
        return false;
    }
    
    public function getSelectedPaymentOption() {
        return $this->paymentApplication;
    }

    private function getPaymentCheckoutStep() {
        return "payment";
    }
    
    public function getCheckoutStep() {
        if (isset($this->shippingApplication) 
                && !$this->shippingApplication->hasSubProducts()
                && isset($_SESSION['checkoutstep']) 
                && $_SESSION['checkoutstep'] == "shipping"
                && $this->canGoToPayment()) {
            return $this->getPaymentCheckoutStep();
        }
        
        if (isset($_SESSION['checkoutstep']) && $_SESSION['checkoutstep'] == "shipping") {    
            if (count($this->getShipmentApplications()) == 0) {
                return $this->getPaymentCheckoutStep();
            }
            if (isset($this->shippingApplication) && !$this->shippingApplication->hasSubProducts() && count($this->getShipmentApplications()) == 1) {
                return $this->getPaymentCheckoutStep();
            }
            return "shipping";
        } else if ( isset($_SESSION['checkoutstep']) && $_SESSION['checkoutstep'] == "payment") {
            return $this->getPaymentCheckoutStep();
        }
        
        if (isset($_SESSION['cartCustomerId'])) {
            return $this->getPaymentCheckoutStep();
        }
        
        return "address";
    }

    public function getPaymentMethods($fromCheckout=false) {
        $paymentMethods = array();
        foreach ($this->getPaymentApplications($fromCheckout) as $paymentApplication) {
            $paymentMethods = array_merge($paymentMethods, $paymentApplication->getPaymentMethods());
        }
        
        return $paymentMethods;
    }
    
     public function getPaymentApplication($id) {
        foreach ($this->getPaymentApplications() as $paymentApp) {
            if ($paymentApp->applicationSettings->id == $id) {
                return $paymentApp;
            }
        }
        
        return null;
    }

    public function showRedirectInformation() {
        \HelperCart::clearSession();
        $this->getApi()->getCartManager()->clear();
        echo $this->__w("Please wait while you are being transferred to dibs payment service.");
    }

}   

?>