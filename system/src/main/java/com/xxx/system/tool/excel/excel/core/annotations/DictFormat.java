/*
 * Decompiled with CFR 0.152.
 */
package com.xxx.system.tool.excel.excel.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.FIELD})
@Retention(value=RetentionPolicy.RUNTIME)
@Inherited
public @interface DictFormat {
    public String value();
}


