<?php
namespace ns_cbb4df6d_3990_41cc_8b41_3cef9c850e12;

class ReportProblem extends \MarketingApplication implements \Application {
    var $failed = false;
    var $submitted = false;
    
    public function getDescription() {
        
    }

    public function getName() {
        return "ReportProblem";
    }
    
    public function submitForm() {
        foreach($_POST['data'] as $key => $val) {
            if(!$val) {
                $this->failed = true;
                return;
            }
        }
        $msg = $_POST['data'];
        
        $entry = new \core_listmanager_data_Entry();
        $entry->metaData = $msg;
        $entry->name = "problem";
        $entry->prefix = "47";
        $entry->phone = $_POST['data']['phone'];
        $entry->emailToSendConfirmationTo = "andreas@wigvard.no";
        $entry = $this->getApi()->getListManager()->addUnsecureEntry("problemreporter", $entry);
        $this->submitted = true;
        $url = "http://" . $_SERVER['SERVER_NAME'] . "/scripts/confirmEntry.php?id=" . $entry->id;
        $this->getApi()->getListManager()->askConfirmationOnEntry($entry->id, "Bekreft ditt innlegg ved å åpne følgende link: " . $url);
    }

    public function render() {
        if($this->submitted) {
            echo "<i class='fa fa-check'></i> Takk for din melding, du vil nå få en tekstmelding hvor du blir bedt om å bekrefte meldingen din.";
        } else {
            $this->includefile("regform");
        }
    }
}
?>