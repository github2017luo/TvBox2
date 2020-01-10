package com.easy.tvbox.utils;

/**
 * 调用系统自带页面
 */
public class SystemUtils {

    /**
     * @param version 2.1.3==>20103 2.30.59==>23059
     * @return
     */
    public static int getVersion(String version) {
        int code = 0;
        if (version != null && version.contains(".")) {
            String[] versions = version.split("\\.");
            if (versions.length == 3) {
                code += Integer.valueOf(versions[0]) * 10000;
                code += Integer.valueOf(versions[1]) * 100;
                code += Integer.valueOf(versions[2]);
            }
        }
        return code;
    }
}
