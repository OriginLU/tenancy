package com.zeroone.tenancy.enums;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum DatasourceStatusEnum {

    INIT(1,"就绪"),
    CREATE(2,"创建"),
    OVERRIDE(3,"重新创建"),
    RUNNING(4,"运行中"),
    DESTORY(5,"销毁"),
    ERROR_INPUT(-1,"错误状态"),

    ;


    private final int status;

    private final String desc;

    private final static Map<Integer, DatasourceStatusEnum> valueMap;

    static {
        valueMap = new HashMap<>(DatasourceStatusEnum.values().length);
        for (DatasourceStatusEnum type : DatasourceStatusEnum.values()) {
            valueMap.put(type.getStatus(), type);
        }
    }

    public static DatasourceStatusEnum fromType(int code) {
        return Optional.ofNullable(valueMap.get(code)).orElse(DatasourceStatusEnum.ERROR_INPUT);
    }

    DatasourceStatusEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public int getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
