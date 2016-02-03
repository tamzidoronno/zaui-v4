<?php
namespace ns_a6d68820_a8e3_4eac_b2b6_b05043c28d78;

class Company extends \SystemApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "Company";
    }
    
    public function updateUser() {
        $user = $this->getApi()->getUserManager()->getUserById($_POST['userid']);
        $user->company = array();
        $user->company[] = $_POST['companyid'];
        $this->getApi()->getUserManager()->saveUser($user);
    }

    public function renderConfig() {
        $this->includefile("overview");
    }
    
    /**
     * @param \core_usermanager_data_User $user
     */
    public function renderUserSettings($user) {
        ?>
        <div class="gss_overrideapp" gss_use_app_id="a6d68820-a8e3-4eac-b2b6-b05043c28d78">
            <input type='hidden' gs_model='companymodel' gs_model_attr='userid' value='<?php echo $user->id; ?>'>
            <div class="textfield gss_setting">
                <span class="title"><?php echo $this->__f("Selected company"); ?></span>
                <?
                    $companies = $this->getApi()->getUserManager()->getAllCompanies();
                    echo "<select class='gsschangeusercompany' gs_model_attr='companyid' gs_model='companymodel'>";
                    echo "<option value=''>Set a company</option>";
                    foreach($companies as $company) {
                        $sel = "";
                        if($user->company[0] == $company->id) {
                            $sel = "SELECTED";
                        }
                        echo "<option value='".$company->id."' $sel>" . $company->name . "</option>";
                    }
                    echo "</select>";
                ?>
                <div class="description">
                    <?php echo $this->__("Is this user connected to a company?"); ?>
                </div>
            </div>
            <div class='gss_button_area'>
                  <div class="gss_button" gss_method="updateUser" gss_model="companymodel" gss_success_message="Saved successfully"><i class='fa fa-save'></i><?php echo $this->__("Update"); ?></div>
            </div>
        </div>
        
        <?php
        
    }
    
    public function updateCompanyInformation() {
        $company = $this->getApi()->getUserManager()->getCompany($_POST['companyid']);
        foreach($_POST as $key => $value) {
            $company->{$key} = $value;
        }
        
        $company->address->address = $_POST['address'];
        $company->address->postCode = $_POST['postcode'];
        $company->address->city = $_POST['city'];
        
        $company->invoiceAddress->address = $_POST['invoice_address'];
        $company->invoiceAddress->postCode = $_POST['invoice_postcode'];
        $company->invoiceAddress->city = $_POST['invoice_city'];
        
        
        $this->getApi()->getUserManager()->saveCompany($company);
    }
    
    public function render() {
        
    }
    
    public function deleteCompany() {
        $companyId = $_POST['companyid'];
        $this->getApi()->getUserManager()->deleteCompany($companyId);
    }
    
    public function createCompany() {
        $company = new \core_usermanager_data_Company();
        $company->vatNumber = $_POST['orgid'];
        $comp = $this->getApi()->getUserManager()->saveCompany($company);
        $_POST['value'] = $comp->id;
    }
}
?>
