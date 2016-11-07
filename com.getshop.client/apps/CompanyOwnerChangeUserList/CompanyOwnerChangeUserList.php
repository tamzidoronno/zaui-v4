<?php
namespace ns_f6117018_449b_4bba_96a3_b775be089f86;

class CompanyOwnerChangeUserList extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "CompanyOwnerChangeUserList";
    }

    public function render() {
        $companyOwner = \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject() != null && \ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->isCompanyOwner;
        $editorOrAbove = $this->getUser()->type >= 50;
        
        if ($companyOwner || $editorOrAbove) {
            $this->includefile("changeUserList");
        }
    }
}
?>
