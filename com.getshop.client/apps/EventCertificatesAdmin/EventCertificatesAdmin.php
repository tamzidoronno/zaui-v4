<?php
namespace ns_a66b64a0_dc80_47c3_8791_55edf4be82d9;

class EventCertificatesAdmin extends \ns_d5444395_4535_4854_9dc1_81b769f5a0c3\EventCommon implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "EventCertificatesAdmin";
    }

    public function render() {
        $this->includefile("certificatesAdmin");
    }
    
    public function createCertificate() {
        $certificate = new \core_eventbooking_Certificate();
        $certificate->id = $_POST['data']['certificateId'];
        $certificate->name = $_POST['data']['name'];
        $certificate->validFrom = $this->convertToJavaDate(strtotime($_POST['data']['from']));
        $certificate->validTo = $this->convertToJavaDate(strtotime($_POST['data']['to']));
        $certificate->backgroundImage = $_POST['data']['imageid'];
        @$certificate->data->{"text1"} = $_POST['data']['text1'];
        $certificate->data->{"text2"} = $_POST['data']['text2'];
        $certificate->data->{"text3"} = $_POST['data']['text3'];
        $certificate->data->{"text4"} = $_POST['data']['text4'];
        $certificate->data->{"text5"} = $_POST['data']['text5'];
        $this->getApi()->getEventBookingManager()->saveCertificate($this->getBookingEngineName(), $certificate);
    }
    
    public function showCertificate() {
    }
    
    public function deleteCertificate() {
        $this->getApi()->getEventBookingManager()->deleteCertificate($this->getBookingEngineName(), $_POST['data']['id']);
    }
}
?>
