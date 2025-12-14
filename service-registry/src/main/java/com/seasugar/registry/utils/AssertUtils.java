package com.seasugar.registry.utils;

import com.seasugar.registry.excetions.ExceptionEnum;

public class AssertUtils {
    private AssertUtils() {
        // 私有构造方法，不允许调用
    }

    public static void AssertNotNull(Object obj) throws RuntimeException {
        if (obj == null) {
            throw new RuntimeException(ExceptionEnum.NO_SUCH_CLASS.getMsg());
        }
    }
}
