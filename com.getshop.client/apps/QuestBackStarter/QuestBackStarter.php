<?php
namespace ns_f4ebeef1_34c8_4c8b_893a_1acf31ba20df;

class QuestBackStarter extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "QuestBackStarter";
    }
    
    public static function getCurrentRunningTestId() {
        if (isset($_SESSION['ns_cc678bcb_0e87_4c6c_aaad_8ec24ecdf9df_current_testid'])) {
            return $_SESSION['ns_cc678bcb_0e87_4c6c_aaad_8ec24ecdf9df_current_testid'];
        }
        
        return null;
    }

    public function render() {
        echo "<div class='starting'><i class='fa fa-spin fa-spinner'></i> ".$this->__w("Starting")."</div>";
        $_SESSION['ns_cc678bcb_0e87_4c6c_aaad_8ec24ecdf9df_current_testid'] = $_GET['gs_testId'];
        $pageId = $this->getApi()->getQuestBackManager()->getNextQuestionPage(QuestBackStarter::getCurrentRunningTestId());
        
        if (isset($_GET['referenceId'])) {
            $this->getApi()->getUserManager()->assignMetaDataToVirtualSessionUser("questback_referenceId", $_GET['referenceId']);
        }
//        
        if ($pageId == "done") {
            echo "<script> thundashop.common.goToPage('questback_result_page'); </script>";
        } else {
            echo "<script> thundashop.common.goToPage('$pageId'); </script>";
        }
        
        
    }
}
?>
