<?php

function gen_uuid() {
    return sprintf('%04x%04x-%04x-%04x-%04x-%04x%04x%04x',
                    // 32 bits for "time_low"
                    mt_rand(0, 0xffff), mt_rand(0, 0xffff),
                    // 16 bits for "time_mid"
                    mt_rand(0, 0xffff),
                    // 16 bits for "time_hi_and_version",
                    // four most significant bits holds version number 4
                    mt_rand(0, 0x0fff) | 0x4000,
                    // 16 bits, 8 bits for "clk_seq_hi_res",
                    // 8 bits for "clk_seq_low",
                    // two most significant bits holds zero and one for variant DCE1.1
                    mt_rand(0, 0x3fff) | 0x8000,
                    // 48 bits for "node"
                    mt_rand(0, 0xffff), mt_rand(0, 0xffff), mt_rand(0, 0xffff)
    );
}

function storeFile($content) {
    $id = gen_uuid();

    if(!is_dir("../../uploadedfiles")) {
        mkdir("../../uploadedfiles");
    }

    $fp = fopen("../../uploadedfiles/".$id, "w");
    fwrite($fp, $content);
    fclose($fp);
    chmod("../../uploadedfiles/".$id, 0444);

    return $id;
}
    
$content = base64_decode(str_replace("data:image/png;base64,", "", $_POST['data']));
$imgId = storeFile($content);
echo $imgId;