package com.chenyx.redis.tool.constant;

/**
 * @desc redis 返回结果枚举
 * @auhtor chenyx
 * @date 2020-11-09
 * */
public enum RedisResultEnum {

    SUC(1,"成功！"),FAIL(0,"失败！");

    private Integer code;

    private String desc;

    RedisResultEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }


    public static RedisResultEnum getEnumByCode(Integer code) {
        for (RedisResultEnum typeEnum : RedisResultEnum.values()) {
            if (typeEnum.getCode()== code) {
                return typeEnum;
            }
        }
        return null;
    }

    public static String getDesc(Integer code) {
        for (RedisResultEnum typeEnum : RedisResultEnum.values()) {
            if (typeEnum.getCode() == code) {
                return typeEnum.getDesc();
            }
        }
        return null;
    }


}
