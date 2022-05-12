package com.ooamo.common.lang;

import lombok.Data;

import java.io.Serializable;

/**
 * 结果类型以及详情
 * 统一的前端返回类型
 *
 * 简单的说就是每当客户端访问某个能开启会话功能的资源，web服务器就会创建一个HTTPSession对象，
 * 每个HTTPSession对象都会占用一定的内存，如果在同一个时间段内访问的用户太多，就会消耗大量的服务器内存，
 * 为了解决这个问题我们使用一种技术：session的持久化
 *
 * 什么是session持久化？
 *
 * web服务器会把暂时不活动的并且没有失效的HTTPSession对象转移到文件系统或数据库中储存，服务器要用时在把他们转载到内存。
 *
 * 现在我们言归正传，为什么要实现序列化？
 *
 * 把HTTPSession保存到文件系统或数据库中需要采用序列化的方式，把HTTPSession从文件系统或数据库中装载到内存需要
 * 采用反序列化来恢复对象的每个属性，所以我们要实现java.io.Serializable
 *
 * 我的理解：就像我们搬桌子，桌子太大了不能通过比较小的门，所以我们要把它拆了再运进去，这个拆桌子的过程就是序列化。
 *
 * 而反序列化就是等我们需要用桌子的时候再把它窦起来，这个过程就是反序列化
 */

@Data
public class Result implements Serializable {

    // 0成功，-1失败
    private int status;
    private String msg;
    private Object data;
    private String action;

    public static Result success() {
        return Result.success("操作成功", null);
    }

    public static Result success(Object data) {
        return Result.success("操作成功", data);
    }

    public static Result success(String msg, Object data) {
        Result result = new Result();
        result.status = 0;
        result.msg = msg;
        result.data = data;
        return result;
    }

    public static Result fail(String msg) {
        Result result = new Result();
        result.status = -1;
        result.data = null;
        result.msg = msg;
        return result;
    }

    public Result action(String action){
        this.action = action;
        return this;
    }

}
