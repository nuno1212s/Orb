package com.nuno1212s.core.modulemanager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Module Data
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleData {

    String name();

    String version();

    String[] dependencies();

}
