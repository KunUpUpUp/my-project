package com.seasugar.registry.excetions;

public enum ExceptionEnum {
    NO_SUCH_CLASS(8848, "IOC中不存在该类"),
    DEFAULT_NOT_NULL_MSG(8849,"对象不能为null"),
    DEFAULT_NOT_EMPTY_MSG(8850,"字符串不能为空"),
    DEFAULT_NOT_BLANK_MSG(8851,"字符串不能为空或只包含空白字符"),
    DEFAULT_NOT_EMPTY_COLLECTION_MSG(8852,"集合不能为空"),
    DEFAULT_IS_TRUE_MSG(8853,"表达式必须为true"),
    DEFAULT_IS_FALSE_MSG(8854,"表达式必须为false"),
    DEFAULT_GREATER_THAN_MSG(8855,"值必须大于指定值"),
    DEFAULT_LESS_THAN_MSG(8856,"值必须小于指定值"),
    ERROR_MSG(8857,"非法消息")
    ;

    Integer code;
    String msg;

    ExceptionEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}