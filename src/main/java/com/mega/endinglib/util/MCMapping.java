package com.mega.endinglib.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MCMapping {
    public static int isWorkingspace = 0;

    public static boolean isWorkingspaceMode() {
        if (isWorkingspace == 0) {
            if (isDevelopmentEnvironment()) {
                isWorkingspace = 1;
            } else isWorkingspace = 2;
        }
        return isWorkingspace == 1;
    }

    public static boolean isDevelopmentEnvironment() {
        Path projectDir = Paths.get(System.getProperty("user.dir")).getParent();
        return Files.exists(projectDir.resolve(".gradle")) &&
                Files.exists(projectDir.resolve("build"));
    }
}
