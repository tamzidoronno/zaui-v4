<?php
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of handler
 *
 * @author boggi
 */
session_start();
ob_start();
include '../loader.php';
$factory = IocContainer::getFactorySingelton();
$appBase = new ApplicationBase();

$data = file_get_contents($_FILES['upload']['tmp_name']);
$fileId = \FileUpload::storeFile($data);
$funcNum = $_GET['CKEditorFuncNum'] ;
$url = "/displayImage.php?id=$fileId&height=2000&width=2000";
?>
<html><body>
        <script type="text/javascript">
            window.parent.CKEDITOR.tools.callFunction('<?php echo $funcNum; ?>', '<?php echo $url; ?>');
        </script>
    </body>
</html>
