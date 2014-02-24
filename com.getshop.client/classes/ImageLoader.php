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

    function cropImage($x, $y, $x2, $y2) {
        $this->displayRaw = false;
        $left = $x;
        $top = $y;
        $crop_width = $x2-$x;
        $crop_height = $y2-$y;
        
        $canvas = imagecreatetruecolor($crop_width, $crop_height);
        imagealphablending($canvas, false);
        imagesavealpha($canvas, true);
        imagecopy($canvas, $this->image, 0, 0, $left, $top, $this->getWidth(), $this->getHeight());
        $this->image = $canvas;
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
        if ($this->displayRaw) {
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

    public function zoom($zoom) {
        $this->zoom = $zoom;
    }

    public function rotate($degrees) {
        imagesavealpha($this->image , true);
        $pngTransparency = imagecolorallocatealpha($this->image , 0, 0, 0, 127);
        imagefill($this->image , 0, 0, $pngTransparency);
        $this->image= imagerotate($this->image, -1*$degrees, $pngTransparency);
    }

}

?>
