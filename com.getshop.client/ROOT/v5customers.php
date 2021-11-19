<?php
    define('PMS_V5_CUSTOMERS', ['687cd85e-4812-405a-9532-7748acc29d13']);

    function isV5Customer($storeId)
    {
        return in_array($storeId, PMS_V5_CUSTOMERS);
    }

?>