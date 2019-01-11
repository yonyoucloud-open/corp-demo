package com.yonyou.iuap.corp.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.yonyou.iuap.corp.demo.constraint.EventType;

import java.io.Serializable;

/**
 * 事件内容
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventContent implements Serializable {

    /**
     * 事件类型
     **/
    private EventType type;

    /**
     * 事件唯一的业务 uuid
     **/
    private String eventId;

    /**
     * 事件创建的 unix 时间戳
     **/
    private long timestamp;

    /**
     * 事件涉及的租户 id
     **/
    private String tenantId;

    /**
     * 变动的 staff id
     **/
    private String[] staffId;

    /**
     * 变动的 dept id
     **/
    private String[] deptId;

    /**
     * 变动的 user id
     */
    private String[] userId;

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String[] getStaffId() {
        return staffId;
    }

    public void setStaffId(String[] staffId) {
        this.staffId = staffId;
    }

    public String[] getDeptId() {
        return deptId;
    }

    public void setDeptId(String[] deptId) {
        this.deptId = deptId;
    }

    public String[] getUserId() {
        return userId;
    }

    public void setUserId(String[] userId) {
        this.userId = userId;
    }
}
