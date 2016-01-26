<?php
namespace ns_92e3b7d6_c3ac_463e_90ba_4f966613f8dd;

class SedoxFileUpload extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "SedoxFileUpload";
    }

    public function render() {
        $this->includefile("uploadmodal");
        $this->includefile("uploadform");
    }
    
    public function doesProductExists() {
        echo "false";
    }
    
    // TODO : select product based on data['md5']
    public function selectProductBaseOnMd5() {
        echo "false";
    }
    
    public function uploadFile() {
        $seperator = ";base64,";
        $index = strrpos($_POST['data']['fileBase64'], $seperator)+  strlen($seperator);
        $base64 = substr($_POST['data']['fileBase64'], $index);

        $_SESSION['sedox_file_uploaded'] = $base64;
        $_SESSION['sedox_file_uploaded_filename'] = $_POST['data']['fileName'];
    }
    
    public function completeUpload() {
        $filename = $_SESSION['sedox_file_uploaded_filename'];
        $filecontent = $_SESSION['sedox_file_uploaded'];

        $slave = isset($_POST['data']['selected_parther']) ? $_POST['data']['selected_parther'] : null;
        $useCredit = isset($_POST['data']['upload_widthdraw']) && $_POST['data']['upload_widthdraw'] == "true" ? true : false;
        $geartype = isset($_POST['data']['upload_automatic']) && $_POST['data']['upload_automatic'] == "true" ? "auto" : "man";
        
        // Save filecontent
        $sedoxProduct = new \core_sedox_SedoxProduct();
        $sedoxProduct->brand = $_POST['data']['upload_brand'];
        $sedoxProduct->model = $_POST['data']['upload_model'];
        $sedoxProduct->engineSize = $_POST['data']['upload_enginesize'];
        $sedoxProduct->power = $_POST['data']['upload_power'];
        $sedoxProduct->year = $_POST['data']['upload_year'];
        $sedoxProduct->tool = $_POST['data']['upload_tool'];
        $sedoxProduct->gearType = $geartype;

        $options = new \core_sedox_SedoxBinaryFileOptions();
        $options->requested_adblue = $_POST['data']['upload_adblue'] == "true";
        $options->requested_decat = $_POST['data']['upload_decat'] == "true";
        $options->requested_dpf = $_POST['data']['upload_dpf'] == "true";
        $options->requested_dtc = $_POST['data']['upload_dtc'] == "true";
        $options->requested_egr = $_POST['data']['upload_egr'] == "true";
        $options->requested_vmax = $_POST['data']['upload_vmax'] == "true";
        
        $savedProduct = $this->getApi()->getSedoxProductManager()->createSedoxProduct($sedoxProduct, $filecontent, $filename, $slave, "webpage", $_POST['data']['upload_comment'], $useCredit, $options);
        if (isset($_POST['data']['upload_reference']) && $_POST['data']['upload_reference'] != "") {
            $this->getApi()->getSedoxProductManager()->addReference($savedProduct->id, $_POST['data']['upload_reference']);
        }

        $this->clear();
    }
    
    private function clear() {
        unset($_SESSION['sedox_file_uploaded']);
        unset($_SESSION['sedox_file_uploaded_filename']);
    }

    public function getSlaves() {
        $userId = $this->getUser()->id;
        return $this->getApi()->getSedoxProductManager()->getSlaves($userId);
    }

}
?>
