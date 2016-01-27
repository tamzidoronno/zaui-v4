<?php
namespace ns_1475891a_3154_49f9_a2b4_ed10bfdda1fc;

class SedoxAdmin extends \ns_5278fb21_3c0a_4ea1_b282_be1b76896a4b\SedoxCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SedoxAdmin";
    }

    public function render() {
        $this->includefile("uploadmodal");
        $this->includefile("sedoxadminlist");
    }
    
    public function getFilesToProcess() {
        
    }
    
    public function getTotalPages() {
        return 10;
    }
    
    public function uploadFile() {
        $seperator = ";base64,";
        $index = strrpos($_POST['data']['fileBase64'], $seperator)+  strlen($seperator);
        $base64 = substr($_POST['data']['fileBase64'], $index);

        $_SESSION['SEDOX_FILE_ADMIN_UPLOADED'] = $base64;
        $_SESSION['SEDOX_FILE_ADMIN_UPLOADED_FILENAME'] = $_POST['data']['fileName'];
    }
    
    public function deleteFile() {
        $this->getApi()->getSedoxProductManager()->removeBinaryFileFromProduct($_POST['data']['productid'], $_POST['data']['sedox_file_id']);
        $_POST['data']['productId'] = $_POST['data']['productid'];
        $_POST['data']['fileId'] = "";
        $this->fileSelected();
    }
    
    public function purchaseProductOnly() {
        
    }
    
    public function finalizeFileUpload() {
        $base64 = $_SESSION['SEDOX_FILE_ADMIN_UPLOADED'];
        $filename = $_SESSION['SEDOX_FILE_ADMIN_UPLOADED_FILENAME'];
        $this->getApi()->getSedoxProductManager()->addFileToProduct($base64, $filename, $_POST['data']['type'], $_POST['data']['productid']);
    }
    
    public function markRowAsExpanded() {
        if (isset($_SESSION['sedox_admin_row_expanded']) && $_SESSION['sedox_admin_row_expanded'] == $_POST['data']['productid']) {
            unset($_SESSION['sedox_admin_row_expanded']);
        } else {
            $_SESSION['sedox_admin_row_expanded'] = $_POST['data']['productid'];
        }
    }
    
    public function isExpanded($productId) {
        if (isset($_SESSION['sedox_admin_row_expanded']) && $_SESSION['sedox_admin_row_expanded'] == $productId) {
            return true;
        }
        
        return false;
    }
    
    public function toggleProductSalable() {
        $product = $this->getApi()->getSedoxProductManager()->getProductById($_POST['data']['productid']);
        $this->getApi()->getSedoxProductManager()->toggleSaleableProduct($product->id, !$product->saleAble);
    }
    
    public function markProductAsStarted() {
        $product = $this->getApi()->getSedoxProductManager()->getProductById($_POST['data']['productid']);
        $this->getApi()->getSedoxProductManager()->toggleStartStop($product->id, !$product->started);
    }
    
    public function sendFileByMail() {
        $files = [$_POST['data']['sedox_file_id']];
        $this->getApi()->getSedoxProductManager()->sendProductByMail($_POST['data']['productid'], "", $files);
    }
    
    public function notifyByEmail() {
        $files = [$_POST['data']['sedox_file_id']];
        $this->getApi()->getSedoxProductManager()->notifyForCustomer($_POST['data']['productid'], "");
    }
    
    public function purchaseOrder() {
        $files = [$_POST['data']['sedox_file_id']];
        $this->getApi()->getSedoxProductManager()->purchaseOnlyForCustomer($_POST['data']['productid'], $files);
    }

    /**
     * 
     * @param \core_sedox_SedoxProduct $product
     */
    public function getRequestedString($product) {
        if (count($product->binaryFiles) < 1) {
            return $product->remapType;
        }
        
        $options = $product->binaryFiles[0]->options;
        
        $requests = ucwords($options->requested_remaptype)." -";
        $requests .= $options->requested_adblue == "1" ? " AD Blue" : "";
        $requests .= $options->requested_decat == "1" ? " Decat" : "";
        $requests .= $options->requested_dpf == "1" ? " DPF" : "";
        $requests .= $options->requested_dtc == "1" ? " DTC" : "";
        $requests .= $options->requested_egr == "1" ? " EGR" : "";
        $requests .= $options->requested_vmax == "1" ? " Vmax" : "";
        
        return $requests;
    }

}
?>
