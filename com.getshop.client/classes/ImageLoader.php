<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Description of ImageLoader
 *
 * @author boggi
 */
class ImageLoader {

    var $image;
    var $raw;
    var $zoom;
    var $image_type;
    var $displayRaw = true;

    function load($id) {
        $filename = "../uploadedfiles/" . $id;
        if (!file_exists($filename)) {
            $filename = "../demoimages/$id";
        }
        $image_info = getimagesize($filename);
        $this->raw = file_get_contents($filename);
        $this->image_type = $image_info[2];
        if ($this->image_type == IMAGETYPE_JPEG) {
            $this->image = imagecreatefromjpeg($filename);
        } elseif ($this->image_type == IMAGETYPE_GIF) {
            $this->image = imagecreatefromgif($filename);
        } elseif ($this->image_type == IMAGETYPE_PNG) {
            $this->image = imagecreatefrompng($filename);
        }
    }

    function save($filename, $image_type = IMAGETYPE_JPEG, $compression = 75, $permissions = null) {

        if ($image_type == IMAGETYPE_JPEG) {
            imagejpeg($this->image, $filename, $compression);
        } elseif ($image_type == IMAGETYPE_GIF) {

            imagegif($this->image, $filename);
        } elseif ($image_type == IMAGETYPE_PNG) {

            imagepng($this->image, $filename);
        }
        if ($permissions != null) {

            chmod($filename, $permissions);
        }
    }

    function output($image_type = IMAGETYPE_PNG) {
        if($this->displayRaw) {
            echo $this->raw;
        } else if ($image_type == IMAGETYPE_JPEG) {
            imagejpeg($this->image);
        } elseif ($image_type == IMAGETYPE_GIF) {
            imagegif($this->image);
        } elseif ($image_type == IMAGETYPE_PNG) {
            imagepng($this->image);
        }
    }

    function getWidth() {
        return imagesx($this->image);
    }

    function getHeight() {
        return imagesy($this->image);
    }

    function resizeToHeight($height) {
        $ratio = $height / $this->getHeight();
        $width = $this->getWidth() * $ratio;
        $this->resize($width, $height);
    }

    function resizeToWidth($width) {
        $ratio = $width / $this->getWidth();
        $height = $this->getheight() * $ratio;
        $this->resize($width, $height);
    }

    function scale($scale) {
        $width = $this->getWidth() * $scale / 100;
        $height = $this->getheight() * $scale / 100;
        $this->resize($width, $height);
    }

    function resize($in_width, $in_height) {
        //Do not zoom.
        
        
        //Keep the ratio
        if($this->getWidth() > $this->getHeight()) {
            $ratio = $this->getHeight() / $this->getWidth() ;
            $height = $in_width * $ratio;
            $width = $in_width;
        } else if($this->getWidth() <= $this->getHeight()) {
            $ratio = $this->getWidth() / $this->getHeight();
            $width = $in_height * $ratio;
            $height = $in_height;
        }
        
        if ($height > $in_height) {
            $height = $in_height;
            $ratio = $this->getWidth() / $this->getHeight();
            $width = $in_height * $ratio;
        }
        
        if ($width > $in_width) {
            $width = $in_width;
            $ratio = $this->getHeight() / $this->getWidth() ;
            $height = $in_width * $ratio;
        }
        
        
        if (!isset($width)) 
            $width = $in_width; 
 
        if (!isset($height)) 
            $height = $in_height; 

        if (($width > $this->getWidth() || $height > $this->getHeight()) && !$this->zoom) {
            return;
        }
        
        if($this->zoom) {
            $width = $in_width;
            $height = $in_height;
        }
        
        $new_image = imagecreatetruecolor($width, $height);
        imagealphablending($new_image, false);
        imagesavealpha($new_image, true);
        imagecopyresampled($new_image, $this->image, 0, 0, 0, 0, $width, $height, $this->getWidth(), $this->getHeight());
        $this->image = $new_image;
        $this->displayRaw = false;
    }

    public function zoom($zoom) {
        $this->zoom = $zoom;
    }

}

?>
