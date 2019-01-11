package com.yonyou.iuap.corp.demo.constraint;

/**
 * 推送事件定义
 */
public enum EventType {

    /**
     * 检查回调地址有效性
     */
    CHECK_URL,

    /**
     * 员工增加
     */
    STAFF_ADD,

    /**
     * 员工更改
     */
    STAFF_UPDATE,

    /**
     * 员工启用
     */
    STAFF_ENABLE,

    /**
     * 员工停用
     */
    STAFF_DISABLE,

    /**
     * 员工删除
     */
    STAFF_DELETE,


    /**
     * 部门创建
     */
    DEPT_ADD,

    /**
     * 部门修改
     */
    DEPT_UPDATE,

    /**
     * 部门启用
     */
    DEPT_ENABLE,

    /**
     * 部门停用
     */
    DEPT_DISABLE,

    /**
     * 部门删除
     */
    DEPT_DELETE,


    /**
     * 用户增加
     */
    USER_ADD,

    /**
     * 用户删除
     */
    USER_DELETE,


    /** UNKNOWN **/
    UNKNOWN


}
