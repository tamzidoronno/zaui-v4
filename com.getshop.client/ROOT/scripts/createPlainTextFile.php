<?php

header('Content-Disposition: attachment; filename="' . $_POST['filename'] . '"');
header('Content-Type: text/plain');
echo base64_decode($_POST['data']);