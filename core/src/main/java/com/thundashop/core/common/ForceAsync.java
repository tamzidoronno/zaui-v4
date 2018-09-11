/*
 * This annotation is meant to be used in some very rare situations, 
 * Like if you have an API call that is using only read only and takes some time to complete.
 *
 * Be aware that this can cause race conditions.
 */
package com.thundashop.core.common;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(value = RetentionPolicy.RUNTIME)
public @interface ForceAsync {}

