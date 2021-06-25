<?php
namespace ns_dcc56763_43cf_470f_87c3_ee305a5a517b;

class PmsCrm extends \WebshopApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PmsCrm";
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
    
    /**
     * 
     * @param \core_usermanager_data_User $user
     * @return type
     */
    public function formatFullName($user) {
        $name = "";
        if($user->deactivated) {
            $name .= "<span style='color:#bbb;'>" . $user->fullName . "(deactivated)</span>";   
        } else {
            $name = $user->fullName;
        }
        if($user->primaryCompanyUser) {
            $name = "<i class='fa fa-industry'></i> " . $name;
        }
        return $name;
    }

    public function UserManager_getAllUsersFiltered() {
        $view = new \ns_acb219a1_4a76_4ead_b0dd_6f3ba3776421\CrmCustomerView();
        $view->setUserId($_POST['data']['id']);
        $view->renderApplication(true, $this);
    }
    
    public function formatRowCreatedDate($user) {
        return \GetShopModuleTable::formatDate($user->rowCreatedDate);
    }
    
    /**
     * @param \core_usermanager_data_User $user
     */
    public function formatLastedBooked($user) {
        return \GetShopModuleTable::formatDate($user->lastBooked);
    }
    
    public function formatCompany($user) {
        if (!$user->companyObject) {
            return "N/A";
        }
        
        return $user->companyObject->name;
    }
    
    /**
     * @param \core_usermanager_data_User $user
     */
    public function formatBookings($user) {
        $res = array();
        if(!$user->additionalCrmData->numberOfBookings) { $user->additionalCrmData->numberOfBookings = 0; }
        if(!$user->additionalCrmData->numberOfRooms) { $user->additionalCrmData->numberOfRooms = 0; }
        if(!$user->additionalCrmData->numberOfGuests) { $user->additionalCrmData->numberOfGuests = 0; }
        
        $res[] = "<span title='number of bookings'>" . $user->additionalCrmData->numberOfBookings . "</span>";
        $res[] = "<span title='number of rooms'>" . $user->additionalCrmData->numberOfRooms . "</span>";
        $res[] = "<span title='number of guests'>" . $user->additionalCrmData->numberOfGuests . "</span>";
        return "(" . join(",", $res) . ")";
    }
    
    /**
     * @param \core_usermanager_data_User $user
     */
    public function formatRooms($user) {
    }
    
    /**
     * @param \core_usermanager_data_User $user
     */
    public function formatGuests($user) {
    }
    
    /**
     * @param \core_usermanager_data_User $user
     */
    public function formatAfterStay($user) {
        if($user->additionalCrmData->invoiceAfterStay) {
            return "<i class='fa fa-check'></i>";
        }
        return "";
    }
    
    /**
     * @param \core_usermanager_data_User $user
     */
    public function formatExTax($user) {
        $res = round($user->orderAmount);
        if($user->showExTaxes) {
            $res .= "<br><i class='fa fa-check'></i>";
        }
        
        return $res;
    }
    
    /**
     * @param \core_usermanager_data_User $user
     */
    public function formatDiscounts($user) {
        if($user->additionalCrmData->hasDiscount) {
            $discountType = "";
            if($user->additionalCrmData->discountType == 0) {
                $discountType = "%";
            }
            return  $user->additionalCrmData->discount ."$discountType";
            
        }
        return "";
    }
    
    
    
    public function renderTable() {
        $filterApp = new \ns_9f8483b1_eed4_4da8_b24b_0f48b71512b9\CrmListFilter();
        $crmFilter = $filterApp->getSelectedFilter();
        
        $filter = new \core_common_FilterOptions();
        $filter->pageSize = 20;
        $filter->pageNumber = 1;
        
        if(isset($crmFilter['start']) && $crmFilter['start']) {
            $filter->startDate = $this->convertToJavaDate(strtotime($crmFilter['start']));
            $filter->endDate = $this->convertToJavaDate(strtotime($crmFilter['end']));
        }
        if(isset($crmFilter['keyword'])) {
            $filter->searchWord = $crmFilter['keyword'];
        }
        $filter->extra = $crmFilter;
        
        
        $args = array($filter);
        
        $attributes = array(
            array('id', 'gs_hidden', 'id'),
            array('rowCreatedDate', 'CREATED', null, 'formatRowCreatedDate'),
            array('fullname', 'NAME', 'fullName', 'formatFullName'),
            array('email', 'EMAIL', null, 'formatEmail'),
            array('callPhone', 'PHONE', 'cellPhone'),
            array('company', 'COMPANY', null, 'formatCompany'),
            array('lastBooked', 'LastBooked', null, 'formatLastedBooked'),
            array('other', 'Book.', null, 'formatBookings'),
            array('extax', 'ExTax', null, 'formatExTax'),
            array('discount', 'Discount', null, 'formatDiscounts'),
            array('afterstay', 'After stay', null, 'formatAfterStay')
        );

        if (isset($_POST['data']['newpagenumber'])) {
            $filter->pageNumber = $_POST['data']['newpagenumber'];
        }
        if (isset($_POST['data']['newpagesize'])) {
            $filter->pageSize = $_POST['data']['newpagesize'];
        }
        
        $data = $this->getApi()->getPmsManager()->getAllCrmUsers($this->getSelectedMultilevelDomainName(), $filter);
        
        $table = new \GetShopModuleTable($this, 'UserManager', 'getAllUsersFiltered', $args, $attributes);
        $table->setData($data);
        $table->renderPagedTable();
    }
    
    public function formatEmail($row) {
        return htmlentities($row->emailAddress);
    }
    
}
?>
