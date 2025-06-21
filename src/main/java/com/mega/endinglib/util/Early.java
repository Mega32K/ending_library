package com.mega.endinglib.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Early {
    private static final Logger LOG = LogManager.getLogger("EndingLibrary");

    public static void log(String str, Object args) {
        LOG.debug(String.format(str, args));
    }

    public static void log(String str) {
        LOG.debug(str);
    }

    public static void log(Object obj) {
        LOG.debug(obj.toString());
    }

    public static void catchException(Throwable e) {
        e.printStackTrace();
        System.exit(-1);
    }
}
