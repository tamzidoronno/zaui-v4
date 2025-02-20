<?php
namespace ns_9a242c42_100c_4136_919b_222c0eb98bd1;

class CrmCustomerList extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "CrmCustomerList";
    }

    public function render() {
        $this->renderTable();
    }
    
    public function renderType($user) {
        if ($user->type == 50) {
            $text = $this->__f("Editor");
            return "<i class='gsicon-typewriter' title='$text'></i>";
        }
        
        if ($user->type == 100) {
            $text = $this->__f("Administrator");
            return "<i class='gsicon-crown' title='$text'></i>";
        }
        
        if ($user->type == 10) {
            $text = $this->__f("Customer");
            return "<i class='gsicon-man-woman' title='$text'></i>";
        }
        
        return $user->type; 
    }
    
    public function formatCompany($user) {
        if (!$user->companyObject) {
            return "N/A";
        }
        
        return $user->companyObject->name;
    }
    
    public function formatRowCreatedDate($user) {
        return \GetShopModuleTable::formatDate($user->rowCreatedDate);
    }
    
    public function UserManager_getAllUsersFiltered() {
        $view = new \ns_acb219a1_4a76_4ead_b0dd_6f3ba3776421\CrmCustomerView();
        $view->setUserId($_POST['data']['id']);
        $view->renderApplication(true, $this);
    }
    
    /**
     * 
     * @param \core_usermanager_data_User $user
     * @return type
     */
    public function formatFullName($user) {
        $name = $user->fullName;
        if($user->primaryCompanyUser) {
            $name = "<i class='fa fa-industry'></i> " . $name;
        }
        return $name;
    }

    public function renderTable() {
        $filterApp = new \ns_9f8483b1_eed4_4da8_b24b_0f48b71512b9\CrmListFilter();
        $crmFilter = $filterApp->getSelectedFilter();
        
        $filter = new \core_common_FilterOptions();
        $filter->pageSize = 20;
        $filter->pageNumber = 1;
        
        if($crmFilter['start']) {
            $filter->startDate = $this->convertToJavaDate(strtotime($crmFilter['start']));
            $filter->endDate = $this->convertToJavaDate(strtotime($crmFilter['end']));
        }
        $filter->searchWord = $crmFilter['keyword'];
        $filter->extra = $crmFilter;
        
        
        $args = array($filter);
        
        $attributes = array(
            array('id', 'gs_hidden', 'id'),
            array('rowCreatedDate', 'CREATED', null, 'formatRowCreatedDate'),
            array('type', 'TYPE', null, 'renderType'),
            array('fullname', 'NAME', 'fullName', 'formatFullName'),
            array('email', 'EMAIL', 'emailAddress'),
            array('callPhone', 'PHONE', 'cellPhone'),
            array('company', 'COMPANY', null, 'formatCompany')
        );
        
        $table = new \GetShopModuleTable($this, 'UserManager', 'getAllUsersFiltered', $args, $attributes);
        $table->renderPagedTable();
    }

}
?>
