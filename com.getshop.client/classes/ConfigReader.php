<?php
/**
 * Description of ConfigReader
 *
 * @author boggi
 */
class ConfigReader {
    function getConfig($key) {
        $config = $this->readConfigFile();
        return $config[$key];
    }
    
    function readConfigFile() {
        
        $result = array();
        if(is_dir('../etc') && file_exists("../etc/config.txt")) {
            $content = file_get_contents("../etc/config.txt");
            $contents = explode("\n", $content);
    
            foreach($contents as $line) {
                if (strlen($line) < 1)
                    continue;
                
                $splitted = explode("=", $line);
                $result[$splitted[0]] = $splitted[1];
            }
        } else {
            $result['port'] = 23232;
            $result['backenddb'] = "backenddb.getshop.com";
        }
        return $result;
    }
}

?>
