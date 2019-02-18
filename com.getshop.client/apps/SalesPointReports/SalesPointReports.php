<?php
namespace ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7;

class SalesPointReports extends \ns_57db782b_5fe7_478f_956a_ab9eb3575855\SalesPointCommon implements \Application {
    private $users = array();
    
    private $cachedPaymentNames = array();
    
    public function getDescription() {
        
    }

    public function getName() {
        return "SalesPointReports";
    }

    public function render() {
        if ($this->preRender()) {
            return;
        }
        
        $this->addOrderToReport();
        $this->includefile("leftmenu");
        
        $tab = isset($_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_activetab']) ? $_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_activetab'] : "";
        echo "<div class='reportarea'>";
            if ($tab == "reportlist") {
                $this->showReportList(true);
            } else {
                $this->includefile("xreport");
            }
        echo "</div>";
    }
    
    public function isCurrentZReportASavedReport() {
        return isset($_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_selected_z_report']) && $_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_selected_z_report'];
    }

    public function getCurrentZReport() {
        if ($this->isCurrentZReportASavedReport()) {
            return $this->getApi()->getPosManager()->getZReport($_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_selected_z_report'], $this->getSelectedCashPointId());
        } else {
            return $this->getApi()->getPosManager()->getZReport("", $this->getSelectedCashPointId());
        }
    }
    
    public function cancelCurrentZReport() {
        unset($_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_selected_z_report']);
    }

    /**
     * 
     * @param \core_ordermanager_data_Order $order
     * @return \core_usermanager_data_User
     */
    public function getUserById($userId) {
        if (!array_key_exists($userId, $this->users)) {
            $user = $this->getApi()->getUserManager()->getUserById($userId);
            $this->users[$userId] = $user;
        }
        
        return $this->users[$userId];
    }
    
    public function createZReport() {
        $this->getApi()->getPosManager()->createZReport($this->getSelectedCashPointId());
    }
    
    public function formatRowCreatedDate($row) {
        return $this->formatDate($row->rowCreatedDate);
    }
    
    public function formatCashPointName($row) {
        return $this->getApi()->getPosManager()->getCashPoint($row->cashPointId)->cashPointName;
    }
    
    public function formatDate($date) {
        return date('d.m.Y H:i:s', strtotime($date));
    }
    
    public function formatActivateButton($row) {
        $retString = "";
        
        if (strstr(\ns_df435931_9364_4b6a_b4b2_951c90cc0d70\Login::getUserObject()->emailAddress, "getshop.com")) {
            $retString = '<i zreportid="'.$row->id.'" gs_precheck="app.SalesPointReports.getpassword" gsclick="deleteZReport" class="gs_shop_small_icon fa fa-trash"></i> ';
        }
        
        $retString .= "<div class='gs_shop_small_icon' gsclick='selectZReport' zreportid='$row->id'>".$this->__f("show")."</div>";
        
        return $retString;
    }
    
    public function formatUser($row) {
        $user = $this->getUserById($row->createdByUserId);
        return $user ? $user->fullName : "N/A";
    }
    
    public function showXReport($fromRender=false) {
        $_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_activetab'] = "xreport";
        $this->includefile('xreport');
        die();
    }
    
    public function showReportList($fromRender=false) {
        $_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_activetab'] = "reportlist";
        
        echo "<div class='reportheader'>".$this->__f("You have the following reports")."</div>";
        $filterOptions = new \core_common_FilterOptions();
        $args = array($filterOptions);

        $attributes = array(
            array('rowaction', '', '', 'formatActivateButton'),
            array('id', 'gs_hidden', 'id'),
            array('rowCreatedDate', 'CREATED DATE', '', 'formatRowCreatedDate'),
            array('user', 'Created by', '', 'formatUser'),
            array('totalAmount', 'TOTAL', 'totalAmount'),
            array('cashPointName', 'CASHPOINT', '', 'formatCashPointName')
        );
        
        $table = new \GetShopModuleTable($this, 'PosManager', 'getZReportsUnfinalized', $args, $attributes);
        $table->renderPagedTable();
        if (!$fromRender) 
            die();
    }

    public function selectZReport() {
        $_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_activetab'] = "xreport";
        $_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_selected_z_report'] = $_POST['data']['zreportid'];
    }

    public function addOrderToReport() {
        if (isset($_POST['data']['addorder']) && isset($_POST['data']['password']) && isset($_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_selected_z_report']) && $_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_selected_z_report']) {
            $this->getApi()->getPosManager()->addOrderIdToZReport($_POST['data']['addorder'], $_SESSION['ns_c20ea6e2_bc0b_4fe1_b92a_0c73b67aead7_selected_z_report'], $_POST['data']['password']);
        }
    }

    public function getPaymentMethodName($paymentNameSpaceId) {
        if (isset($this->cachedPaymentNames[$paymentNameSpaceId])) {
            return $this->cachedPaymentNames[$paymentNameSpaceId];
        }
        
        $paymentAppId = str_replace("ns_", "", $paymentNameSpaceId);
        $paymentAppId = str_replace("_", "-", $paymentAppId);
        $app = $this->getApi()->getStoreApplicationPool()->getApplication($paymentAppId);
        $instance = $this->getFactory()->getApplicationPool()->createInstace($app);
        $name = $instance->getName();
        $this->cachedPaymentNames[$paymentNameSpaceId] = $name;
        return $name;
    }

    /**
     * @return \core_pos_CanCloseZReport
     */
    public function getCloseResult() {
        return $this->closeResult;
    }
    
    public function canMakeZReport() {
        $this->closeResult = $this->getApi()->getPosManager()->canCreateZReport($this->getSelectedMultilevelDomainName(), $this->getSelectedCashPointId());
        $this->includefile("closeerrors");
        return $this->closeResult->canClose;
    }
    
    public function deleteZReport() {
        
    }
    
    public function deleteOrder() {
        $this->getApi()->getOrderManager()->deleteOrder($_POST['data']['orderid']);
    }

}
?>

