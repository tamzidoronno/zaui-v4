/*
 * This will start a new thread each time its invoked.
 *
 * WORKS ONLY FOR COMPONENTS NOT SCOPED WITH: @GetShopSession
 */
package com.thundashop.core.common;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(value = RetentionPolicy.RUNTIME)
public @interface GetShopNotSynchronized {}

