<?php

/**
 * Description of CommunicationHelper
 *
 * @author boggi
 */
class CommunicationHelper {

    public $errors = array();
    var $socket;
    var $connected = false;
    var $port = 25554;
    public $errorCodes = array();
    public $host = "";
    public $sessionId = "";
    
    private function getClass($array) {
        if (!is_array($array) || !isset($array['className'])) {
            return null;
        }

        $className = $array['className'];
        $className = str_replace("com.thundashop.", "", $className);
        $className = str_replace(".", "_", $className);
        $class = new $className();

        return $class;
    }

    private function createThundashopObject($array) {
        $class = $this->getClass($array);

        if ($class == null && !is_array($array)) {
            return $array;
        }

        foreach (array_keys($array) as $key) {
            if (!is_object($class)) {
                $class[$key] = $this->createThundashopObject($array[$key]);
            } else {
                $class->$key = $this->createThundashopObject($array[$key]);
            }
        }

        return $class;
    }

    public function connect() {
        if ($this->connected) {
            return;
        }
        
        if ($this->port === false) {
            return;
        }
        
        $this->socket = @fsockopen($this->host, $this->port, $erstr, $errno, 220);
        if (!$this->socket) {
            header("HTTP/1.0 500 Internal server error");
            include_once('../template/maintenance-mode.html');
            //echo "The system are being updated / upgraded, please retry in about 10 seconds.";
            //echo "<script>setTimeout(function() { document.location = document.location; }, 10000)</script>";
            exit(0);
        }
        $this->connected = true;
    }

    function cast($destination, $sourceObject) {
        if ($sourceObject == null)
            return;

        if (is_string($destination)) {
            $destination = new $destination();
        }
        $sourceReflection = new ReflectionObject($sourceObject);
        $destinationReflection = new ReflectionObject($destination);
        $sourceProperties = $sourceReflection->getProperties();
        foreach ($sourceProperties as $sourceProperty) {
            $sourceProperty->setAccessible(true);
            $name = $sourceProperty->getName();
            $value = $sourceProperty->getValue($sourceObject);
            if ($destinationReflection->hasProperty($name)) {
                $propDest = $destinationReflection->getProperty($name);
                $propDest->setAccessible(true);
                $propDest->setValue($destination, $value);
            } else {
                $destination->$name = $value;
            }
        }
        return $destination;
    }

    public function object_unset_nulls($obj) {
        if (!is_array($obj) && !is_object($obj))
            return $obj;

        $arrObj = is_object($obj) ? get_object_vars($obj) : $obj;
        foreach ($arrObj as $key => $val) {
            $val = (is_array($val) || is_object($val)) ? $this->object_unset_nulls($val) : $val;

            if (is_array($obj))
                $obj[$key] = $val;
            else
                $obj->$key = $val;

            if ($val === null || $val === "")
                unset($obj->$key);
        }
        return $obj;
    }

    public function sendMessage($event, $debug=false) {
        $event["sessionId"] = $this->sessionId;
        $event["getShopModuleName"] = \PageFactory::$moduleId;
        
        $data = json_encode($event);
        $res = "";
        $this->connect();
        $len = fputs($this->socket, $data . "\n");
        if($len != strlen($data)+1) {
            $this->connected = false;
            $this->errors[] = "failed on " . $data . " sent: " . $len . " size compared to : " . strlen($data)+1;
        }
        
        stream_set_timeout($this->socket, 300);
        $res = stream_get_line($this->socket, 2147483647, "\n");
        if ($debug)
            echo $res;
        
        $object = json_decode($res, false);
        if (json_last_error() != 0) {
            $object = new stdClass();
            $object->jsonDecodeErorCode = json_last_error();
            $object->additionalInformation = "Something went wrong while doing json_decode of data.";
            if (trim($res) == "false") {
                return false;
            }
            if (trim($res) == "true") {
                return true;
            }

            if (substr($res, 0, 1) == "[" || substr($res, 0, 1) == "[") {
                echo $res;
                echo "Failed to decode json error : " . json_last_error();
                echo "Tried to decode: ";
                print_r($res);
                exit(0);
            }

        }
        if (isset($object->jsonDecodeErorCode)) {
            $handler = new LanguageHandler();
            $result = array();
            $result['error'] = "json decode error: " . $object->jsonDecodeErorCode . ", Failed to parse json object after being fetched from api. PHP object could not be created.";
            $result['error_text'] = $result['error'];
            $result['error_method'] = $event['method'];
            $result['interfaceName'] = $event['interfaceName'];
            $this->errors[] = $result;
            $this->errorCodes[] = $object->jsonDecodeErorCode;
            echo $res;
            if (isset($_POST['synchron'])) {
                http_response_code(400);
                echo json_encode($result);
                die();
            }
        }
        
        if (isset($object->errorCode)) {
            $handler = new LanguageHandler();
            $result = array();
            $result['error'] = $object->errorCode . "," . $handler->getErrorMessage($object->errorCode);
            $result['error_text'] = $this->getErrorMessage($object, $handler);
            $result['error_additional_info'] = $object->additionalInformation;
            $result['error_method'] = $event['method'];
            $result['interfaceName'] = $event['interfaceName'];
            $this->errors[] = $result;
            $this->errorCodes[] = $object->errorCode;
            
            if (isset($_POST['synchron'])) {
                http_response_code(400);
                echo json_encode($result);
                die();
            }
            
            return null;
        } else {
            return $this->createThundashopObject($object);
        }
    }

    private function getErrorMessage($object, $handler) {
        if (isset($object->errorMessage)) {
            return $object->errorMessage;
        }
        return $handler->getErrorMessage($object->errorCode);
    }
}

?>
