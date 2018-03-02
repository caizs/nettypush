package org.caizs.nettypush.core.common;

import static io.netty.util.internal.StringUtil.isNullOrEmpty;

public class Util {

    public static void checkNull(Object obj, String param) {
        if (obj == null) {
            throw new BizException(param + "不存在");
        }
    }

    public static void checkBlank(String obj, String param) {
        if (isNullOrEmpty(obj)) {
            throw new BizException(param + "为空");
        }
    }

    public static boolean isBlank(String obj) {
        return isNullOrEmpty(obj);
    }

}
