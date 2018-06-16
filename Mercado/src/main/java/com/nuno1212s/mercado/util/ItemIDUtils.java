package com.nuno1212s.mercado.util;

import org.apache.commons.lang.RandomStringUtils;

/**
 * ItemIDUtils4
 */
public class ItemIDUtils {

    public static final int ID_LENGTH = 15;

    /**
     * @return A random ID with the length of ID_LENGTH
     */
    public static String getNewRandomID() {
        return RandomStringUtils.random(ID_LENGTH, true, true);
    }

}
