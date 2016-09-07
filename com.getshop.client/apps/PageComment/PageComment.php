<?php
namespace ns_9fef9b15_a4c3_49a4_866c_d422bd244e91;

class PageComment extends \MarketingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PageComment";
    }

    public function render() {
        $this->includefile("comments");
    }
    
    public function saveComment() {
        $pagecomment = new \core_pagemanager_data_PageComment();
        $pagecomment->comment = $_POST['data']['comment'];
        $pagecomment->pageId = $this->getPage()->getId();
        $this->getApi()->getPageManager()->addComment($pagecomment);
    }
    
    public function deleteComment() {
        $this->getApi()->getPageManager()->deleteComment($_POST['data']['commentid']);
    }
}
?>
