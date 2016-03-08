<?php
namespace ns_c6fe1e9b_a5f6_400b_aaa1_a422d2400222;

class RegistrationForm extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "RegistrationForm";
    }

    public function render() {
        ?>
        <span gstype="form" class="registrationform<?php echo $this->getConfigurationSetting("number"); ?>" method='doRegistration'>
        <?php
            $this->includefile("registrationform" . $this->getConfigurationSetting("number"));
        ?>
            <span class="shop_button" gstype='submit'><?php echo $this->__w("Create an account"); ?></span>
        </span>
        <?php
    }
    
    public function getRules() {
        $rules = $this->getConfigurationSetting("rules");
        if($rules) {
            return json_decode($rules);
        }
        return new \core_bookingengine_data_RegistrationRules();
    }
    
    /**
     * @return \FieldGenerator
     */
    public function getFieldGenerator() {
        $fieldgen = new \FieldGenerator();
        $fieldgen->setData($this->getRules(), $this->getFactory(), "", "whateverid", "saveForm");
        return $fieldgen;
    }
    
    public function doRegistration() {
        $gen = $this->getFieldGenerator();
        $this->errors = $gen->validatePostedForm();
        if(sizeof($this->errors) == 0) {
            $this->registerNewUser();
        }
    }
    
    public function saveForm() {
        $gen = new \FieldGenerator();
        $rules = $gen->createBookingRules();
        $this->setConfigurationSetting("rules", json_encode($rules));
    }

    public function validateField($key) {
        if(isset($this->errors[$key])) {
            echo "<span class='errortext'>* " . $this->errors[$key] . "</span>";
        }
    }

    public function registerNewUser() {
        $gen = $this->getFieldGenerator();
        $user = $gen->createUserObject();
        $this->getApi()->getUserManager()->createUser($user);
        $login = new \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login();
        if($user->username) {
            $login->commonLogin($user->username, $user->password);
        } else {
            $login->commonLogin($user->emailAddress, $user->password);
        }
    }

}
?>
