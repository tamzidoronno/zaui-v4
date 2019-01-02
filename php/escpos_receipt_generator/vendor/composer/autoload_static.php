<?php

// autoload_static.php @generated by Composer

namespace Composer\Autoload;

class ComposerStaticInit8c251b1591711a0ad8b2f8564329da83
{
    public static $prefixLengthsPsr4 = array (
        'M' => 
        array (
            'Mike42\\' => 7,
        ),
    );

    public static $prefixDirsPsr4 = array (
        'Mike42\\' => 
        array (
            0 => __DIR__ . '/..' . '/mike42/escpos-php/src/Mike42',
        ),
    );

    public static function getInitializer(ClassLoader $loader)
    {
        return \Closure::bind(function () use ($loader) {
            $loader->prefixLengthsPsr4 = ComposerStaticInit8c251b1591711a0ad8b2f8564329da83::$prefixLengthsPsr4;
            $loader->prefixDirsPsr4 = ComposerStaticInit8c251b1591711a0ad8b2f8564329da83::$prefixDirsPsr4;

        }, null, ClassLoader::class);
    }
}
