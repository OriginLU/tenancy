package com.zeroone.tenancy.model;


import java.util.EventObject;

public class DatasourceActionEvent extends EventObject {


    private static final long serialVersionUID = -372474798093840890L;


    private Integer status;


    private String tenantCode;


    private Long eventOccurredTime;


    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public DatasourceActionEvent(Object source) {
        super(source);
    }

    public DatasourceActionEvent(Object source, Integer status, String tenantCode, Long eventOccurredTime) {
        super(source);
        this.status = status;
        this.tenantCode = tenantCode;
        this.eventOccurredTime = eventOccurredTime;
    }

    public static DatasourceActionEvent build(Object source, String tenantCode, Integer action) {
        return new DatasourceActionEvent(source, action, tenantCode, System.currentTimeMillis());
    }


    public Integer getStatus() {
        return status;
    }


    public String getTenantCode() {
        return tenantCode;
    }


    public Long getEventOccurredTime() {
        return eventOccurredTime;
    }
}
