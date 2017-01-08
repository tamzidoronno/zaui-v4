<?php
namespace ns_6b14626c_cb58_4ffe_a584_06e26c3eea59;

class PrinterConfig extends \ReportingApplication implements \Application {
    public function getDescription() {
        
    }

    public function getName() {
        return "PrinterConfig";
    }

    public function render() {
        $this->includefile("printers");
    }
    
    public function cratePrinter() {
        $printer = new \core_printmanager_Printer();
        $printer->name = $_POST['data']['name'];
        $printer->type = $_POST['data']['type'];   
        $this->getApi()->getStorePrintManager()->savePrinter($printer);
    }
    
    public function deletePrinter() {
        $this->getApi()->getStorePrintManager()->deletePrinter($_POST['data']['printerid']);
    }
    
    
}
?>
