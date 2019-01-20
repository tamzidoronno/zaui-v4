<?php

/* 
 * This is a simple way to create excel reports.
 * 1. Post the array of data that you wish to create en file of
 * 
 * $_POST['filename'] 
 * $_POST['data'] (jsonecoded data)
 * 
 */

include 'excel.php';
chdir("../");
include '../loader.php';

$factory = IocContainer::getFactorySingelton();

$filename = $_POST['filename'];

header('Content-Disposition: attachment; filename="' . $filename . '.xlsx"');
header('Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet');
header('Content-Transfer-Encoding: binary');
header('Cache-Control: must-revalidate');
header('Pragma: public');


$data = base64_decode($_POST['data']);
$data = json_decode($data);
if(isset($data->useMultipleSheets)) {
    unset($data->useMultipleSheets);
    $sheets = array();
    $factory->getApi()->getExcelManager()->startExcelSheet();
    foreach($data as $name => $sheet) {
        $rewRes = array();
        foreach($sheet as $row) {
            $newRow = array();
            foreach($row as $field) {
                if($field instanceof stdClass) {
                    $field = json_encode($field);
                }
                $newRow[] = $field;
            }
            $rewRes[] = $newRow;
        }
        base64_decode($factory->getApi()->getExcelManager()->prepareExcelSheet($name, $rewRes));
    }
    echo base64_decode($factory->getApi()->getExcelManager()->getPreparedExcelSheet());
} else {
    $newRes = array();
    foreach($data as $row) {
        $newRow = array();
        foreach($row as $field) {
            if($field instanceof stdClass) {
                $field = json_encode($field);
            }
            $newRow[] = $field;
        }
        $rewRes[] = $newRow;
    }

    echo base64_decode($factory->getApi()->getExcelManager()->getBase64Excel($rewRes));
}