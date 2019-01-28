<?php

namespace ns_f1706b4c_f779_4eb7_aec3_ee08f182e090;

class GetShopInbox extends \MarketingApplication implements \Application {

    public function getDescription() {
        
    }

    public function getName() {
        return "GetShopInbox";
    }

    public function render() {
        echo "<div class='leftmenu'>";
        $this->includefile("leftmenu");
        echo "</div>";

        echo "<div class='workarea'>";
        $this->includefile("topmenu");
        $this->includefile("emails");
        echo "</div>";
    }

    /**
     * @return \core_googleapi_GmailMessage[]
     */
    public function getEmails() {
        if (isset($_SESSION['ns_f1706b4c_f779_4eb7_aec3_ee08f182e090_current_view']) && $_SESSION['ns_f1706b4c_f779_4eb7_aec3_ee08f182e090_current_view'] == "unassigned") {
            return $this->getApi()->getGmailApiManager()->getAllUnassignedMessages();
        } else {
            return $this->getApi()->getGmailApiManager()->getMyUnsolvedMessages();
        }
    }

    public function doConnection() {
        $this->getApi()->getGmailApiManager()->connectMessageToCompany($_POST['data']['msgid'], $_POST['data']['companyid']);
    }

    public function changeMenu() {
        $_SESSION['ns_f1706b4c_f779_4eb7_aec3_ee08f182e090_current_view'] = $_POST['data']['tab'];
    }

    public function assignTo() {
        $userId = $_POST['data']['userid'];
        foreach ($_POST['data']['msgs'] as $msgId) {
            $this->getApi()->getGmailApiManager()->assignMessageToUser($msgId, $userId);
        }
    }

    public function markAsIgnored() {
        foreach ($_POST['data']['msgs'] as $msgId) {
            $this->getApi()->getGmailApiManager()->markAsArchived($msgId);
        }
    }

    public function createMailView() {
        $_SESSION['ns_f1706b4c_f779_4eb7_aec3_ee08f182e090_currentmail'] = $_POST['data']['msgid'];
        $this->includefile("mailview");
        die();
    }

    public function sendReply() {
        $lightMsg = $this->getApi()->getGmailApiManager()->getMessageLight($_POST['data']['msgid']);
        $response = $_POST['data']['content'];
        $response .= "On ".date('d.m.Y H:i', strtotime($lightMsg->date)).", ".htmlentities($lightMsg->from)." wrote:";
        $response .= "<div style='border-left: solid 2px #DDD; padding-left: 20px; '>".$this->getContent($lightMsg)."</div>";
        $this->getApi()->getGmailApiManager()->replyEmail($lightMsg->id, $response);
    }

    public function getContent($lightMsg) {
        $parts = $this->getApi()->getGmailApiManager()->getMessageParts($lightMsg->messageId);

        foreach ($parts as $part) {
            if ($part->base64 && $part->contentId) {
                $cid = str_replace("<", "", $part->contentId);
                $cid = str_replace(">", "", $cid);
                $imgtypes = explode(";", $part->contentType);
                $imgtype = $imgtypes[0];

                foreach ($parts as $ipart) {
                    $ipart->html = str_replace("cid:" . $cid, "data:" . $imgtype . ";base64," . $part->base64, $ipart->html);
                }
            }
        }

        $htmlPartFound = false;
        $content = "";
        
        foreach ($parts as $part) {
            if ($part->html) {
                $htmlPartFound = true;
                $content = $part->html;
            }
        }

        if (!$htmlPartFound) {
            foreach ($parts as $part) {
                if ($part->text) {
                    $content = nl2br($part->text);
                }
            }
        }
        
        return $content;
    }

}

?>
